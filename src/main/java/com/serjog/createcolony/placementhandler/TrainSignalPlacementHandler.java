package com.serjog.createcolony.placementhandler;

import com.ldtteam.structurize.api.RotationMirror;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.serjog.createcolony.ColonyMain;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.minecraft.nbt.DoubleTag.valueOf;

public class TrainSignalPlacementHandler extends SimplePlacementHandler {
    @Override
    public boolean canHandle(Level world, BlockPos pos, BlockState blockState) {
        return (blockState.getBlock() instanceof SignalBlock);
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
        List<ItemStack> needed = new ArrayList<>();
        needed.add(new ItemStack(blockState.getBlock().asItem()));
        return needed;
    }

    @Override
    public ActionProcessingResult handle(Blueprint blueprint, Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, RotationMirror settings) {
        if (tileEntityData != null) {
            rotateSignalNbt(tileEntityData, settings);
        }

        world.setBlock(pos, blockState, 3);

        if (tileEntityData != null) {
            var be = world.getBlockEntity(pos);
            if (be instanceof SignalBlockEntity signalBe) {
                signalBe.loadWithComponents(tileEntityData, world.registryAccess());
                signalBe.refreshBlockState();
                signalBe.setChanged();
                world.sendBlockUpdated(signalBe.getBlockPos(), signalBe.getBlockState(), signalBe.getBlockState(), 3);
            }
        }

        return ActionProcessingResult.SUCCESS;
    }

    private void rotateSignalNbt(CompoundTag nbt, RotationMirror rm) {
        Rotation rotation = rm.rotation();
        Mirror mirror = rm.mirror();

        if (nbt.contains("Id")) {
            nbt.putUUID("Id", UUID.randomUUID());
        }

        if (ColonyMain.LOGGER.isDebugEnabled()) {
            ColonyMain.LOGGER.debug("Full NBT before: {}", nbt);
        }

        if (nbt.contains("TargetTrack", Tag.TAG_INT_ARRAY)) {
            int[] arr = nbt.getIntArray("TargetTrack");
            if (arr.length == 3) {
                BlockPos localPosFromSignal = new BlockPos(arr[0], arr[1], arr[2]);
                BlockPos newPosFromSignal;
                if (mirror == Mirror.NONE) {
                    newPosFromSignal = localPosFromSignal.rotate(rotation);
                } else {
                    newPosFromSignal = new BlockPos(-localPosFromSignal.getX(), localPosFromSignal.getY(), localPosFromSignal.getZ()).rotate(rotation);
                }
                nbt.putIntArray("TargetTrack", new int[]{newPosFromSignal.getX(),  newPosFromSignal.getY(), newPosFromSignal.getZ()});
                if ((rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180) && mirror == Mirror.NONE && nbt.contains("TargetDirection")) {
                    int currentDir = nbt.getInt("TargetDirection");
                    currentDir = 1 - currentDir;
                    nbt.putInt("TargetDirection", currentDir);
                } else if ((rotation == Rotation.COUNTERCLOCKWISE_90) && mirror == Mirror.FRONT_BACK && nbt.contains("TargetDirection")) {
                    int currentDir = nbt.getInt("TargetDirection");
                    currentDir = 1 - currentDir;
                    nbt.putInt("TargetDirection", currentDir);
                }
                if (nbt.contains("PrevAxis", Tag.TAG_LIST)) {
                    ListTag axis = nbt.getList("PrevAxis", Tag.TAG_DOUBLE);
                    if ((rotation == Rotation.CLOCKWISE_90 ||  rotation == Rotation.COUNTERCLOCKWISE_90)) {
                        ListTag newAxis = new ListTag();
                        newAxis.add(valueOf(axis.getDouble(2)));
                        newAxis.add(valueOf(axis.getDouble(1)));
                        newAxis.add(valueOf(axis.getDouble(0)));
                        nbt.put("PrevAxis", newAxis);
                    }
                }
                if (nbt.contains("Bezier", Tag.TAG_COMPOUND)) {
                    CompoundTag bezierNbt = nbt.getCompound("Bezier");
                    if (bezierNbt.contains("Key", Tag.TAG_INT_ARRAY)) {
                        int[] keyCoords = bezierNbt.getIntArray("Key");

                        int x = keyCoords[0];
                        int y = keyCoords[1];
                        int z = keyCoords[2];

                        BlockPos localPosFromKey = new BlockPos(x, y, z);
                        BlockPos newPosFromKey = localPosFromKey;

                        if (mirror == Mirror.FRONT_BACK) {
                            if (rotation == Rotation.NONE) {
                                newPosFromKey = new BlockPos(-x, y, z);
                            } else if (rotation == Rotation.COUNTERCLOCKWISE_90) {
                                newPosFromKey = new BlockPos(z, y, x);
                                int currentDir = nbt.getInt("TargetDirection");
                                currentDir = 1 - currentDir;
                                nbt.putInt("TargetDirection", currentDir);
                            }  else if (rotation == Rotation.CLOCKWISE_180) {
                                newPosFromKey = new BlockPos(x, y, -z);
                            } else if (rotation == Rotation.CLOCKWISE_90) {
                                newPosFromKey = new BlockPos(-z, y, -x);
                            }
                        }

                        if (mirror == Mirror.NONE) {
                            newPosFromKey = localPosFromKey.rotate(rotation);
                            if (rotation == Rotation.CLOCKWISE_90) {
                                int currentDir = nbt.getInt("TargetDirection");
                                currentDir = 1 - currentDir;
                                nbt.putInt("TargetDirection", currentDir);
                            }
                        }

                        bezierNbt.putIntArray("Key", new int[]{newPosFromKey.getX(), newPosFromKey.getY(), newPosFromKey.getZ()});
                    }
                }
            }
        }

        if (ColonyMain.LOGGER.isDebugEnabled()) {
            ColonyMain.LOGGER.debug("Full NBT after: {}", nbt);
        }
    }
}
