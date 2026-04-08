package com.serjog.createcolony.placementhandler;

import com.ldtteam.structurize.api.RotationMirror;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.mojang.logging.LogUtils;
import com.serjog.createcolony.ColonyMain;
import com.simibubi.create.content.trains.observer.TrackObserverBlock;
import com.simibubi.create.content.trains.observer.TrackObserverBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.minecraft.nbt.DoubleTag.valueOf;

public class TrainObserverPlacementHandler extends SimplePlacementHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public boolean canHandle(Level world, BlockPos pos, BlockState blockState) {
        return (blockState.getBlock() instanceof TrackObserverBlock);
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
        List<ItemStack> needed = new ArrayList<>();
        needed.add(new ItemStack(blockState.getBlock().asItem()));
        return needed;
    }

    @Override
    public IPlacementHandler.ActionProcessingResult handle(Blueprint blueprint, Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, RotationMirror settings) {
        if (tileEntityData != null) {
            rotateObserverNbt(tileEntityData, settings);
        }

        world.setBlock(pos, blockState, 3);

        if (tileEntityData != null) {
            var be = world.getBlockEntity(pos);
            if (be instanceof TrackObserverBlockEntity trackObserverBe) {
                trackObserverBe.loadWithComponents(tileEntityData, world.registryAccess());
                trackObserverBe.refreshBlockState();
                trackObserverBe.setChanged();
            }
        }

        return IPlacementHandler.ActionProcessingResult.SUCCESS;
    }

    private void rotateObserverNbt(CompoundTag nbt, RotationMirror rm) {
        Rotation rotation = rm.rotation();
        Mirror mirror = rm.mirror();

        if (nbt.contains("Id")) {
            nbt.putUUID("Id", UUID.randomUUID());
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Full NBT before: {}", nbt);
        }

        if (nbt.contains("TargetTrack", Tag.TAG_INT_ARRAY)) {
            int[] arr = nbt.getIntArray("TargetTrack");
            if (arr.length == 3) {
                BlockPos localPosFromObserver = new BlockPos(arr[0], arr[1], arr[2]);
                BlockPos newPosFromObserver;
                if (mirror == Mirror.NONE) {
                    newPosFromObserver = localPosFromObserver.rotate(rotation);
                } else {
                    newPosFromObserver = new BlockPos(-localPosFromObserver.getX(), localPosFromObserver.getY(), localPosFromObserver.getZ()).rotate(rotation);
                }
                nbt.putIntArray("TargetTrack", new int[]{newPosFromObserver.getX(),  newPosFromObserver.getY(), newPosFromObserver.getZ()});
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
                            }  else if (rotation == Rotation.CLOCKWISE_180) {
                                newPosFromKey = new BlockPos(x, y, -z);
                            } else if (rotation == Rotation.CLOCKWISE_90) {
                                newPosFromKey = new BlockPos(-z, y, -x);
                            }
                        }

                        if (mirror == Mirror.NONE) {
                            newPosFromKey = localPosFromKey.rotate(rotation);
                        }

                        bezierNbt.putIntArray("Key", new int[]{newPosFromKey.getX(), newPosFromKey.getY(), newPosFromKey.getZ()});
                    }
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Full NBT after: {}", nbt);
        }
    }
}
