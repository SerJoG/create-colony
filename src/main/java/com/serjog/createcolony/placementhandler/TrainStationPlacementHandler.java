package com.serjog.createcolony.placementhandler;

import com.ldtteam.structurize.api.RotationMirror;
import com.ldtteam.structurize.blueprints.v1.Blueprint;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TrainStationPlacementHandler extends SimplePlacementHandler {
    @Override
    public boolean canHandle(Level world, BlockPos pos, BlockState blockState) {
        return blockState.getBlock() instanceof StationBlock;
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
            rotateStationNbt(tileEntityData, settings);
        }

        world.setBlock(pos, blockState, 3);

        if (tileEntityData != null) {
            var be = world.getBlockEntity(pos);
            if (be instanceof StationBlockEntity stationBe) {
                stationBe.loadWithComponents(tileEntityData, world.registryAccess());
                stationBe.refreshBlockState();
                stationBe.setChanged();
            }
        }

        return ActionProcessingResult.SUCCESS;
    }

    private void rotateStationNbt(CompoundTag nbt, RotationMirror rm) {
        Rotation rotation = rm.rotation();
        Mirror mirror = rm.mirror();

        if (nbt.contains("TargetTrack", Tag.TAG_INT_ARRAY)) {
            int[] arr = nbt.getIntArray("TargetTrack");
            if (arr.length == 3) {
                BlockPos localPosFromStation = new BlockPos(arr[0], arr[1], arr[2]);
                BlockPos newPosFromStation;
                //System.out.println("TargetDirection: " + nbt.getInt("TargetDirection"));
                if (mirror == Mirror.NONE) {
                    newPosFromStation = localPosFromStation.rotate(rotation);
                } else {
                    newPosFromStation = new BlockPos(-localPosFromStation.getX(), localPosFromStation.getY(), localPosFromStation.getZ()).rotate(rotation);
                }
                nbt.putIntArray("TargetTrack", new int[]{newPosFromStation.getX(),  newPosFromStation.getY(), newPosFromStation.getZ()});
                if ((rotation == Rotation.CLOCKWISE_180 || rotation == Rotation.COUNTERCLOCKWISE_90) && mirror == Mirror.NONE && nbt.contains("TargetDirection")) {
                    int currentDir = nbt.getInt("TargetDirection");
                    currentDir = 1 - currentDir;
                    nbt.putInt("TargetDirection", currentDir);
                } else if ((rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.NONE) && mirror == Mirror.FRONT_BACK && nbt.contains("TargetDirection")) {
                    int currentDir = nbt.getInt("TargetDirection");
                    currentDir = 1 - currentDir;
                    nbt.putInt("TargetDirection", currentDir);
                }
            }
        }
    }
}
