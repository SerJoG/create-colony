package com.serjog.createcolony.placementhandler;

import com.ldtteam.structurize.api.RotationMirror;
import com.serjog.createcolony.ColonyMain;
import com.serjog.createcolony.resources.CreateResources;
import com.serjog.createcolony.utils.ItemUtils;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrainBogeyPlacementHandler extends SimplePlacementHandler {
    @Override
    public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
        return blockState.getBlock().getDescriptionId().contains("bogey");
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
        return List.of(ItemUtils.stackFromDeferred(CreateResources.Items.trainCasing));
    }

    @Override
    public ActionProcessingResult handle(Level world, BlockPos pos, BlockState blockState, @Nullable CompoundTag tileEntityData, boolean complete, BlockPos centerPos, RotationMirror settings) {
        world.setBlock(pos, blockState, 3);

        if (tileEntityData != null) {
            var be = world.getBlockEntity(pos);
            if (be instanceof StandardBogeyBlockEntity standardBogeyBe) {
                standardBogeyBe.loadWithComponents(tileEntityData, world.registryAccess());
                standardBogeyBe.setChanged();
                world.sendBlockUpdated(standardBogeyBe.getBlockPos(), standardBogeyBe.getBlockState(), standardBogeyBe.getBlockState(), 3);
            }
        }

        return ActionProcessingResult.SUCCESS;
    }
}
