package com.serjog.createcolony.placementhandler;

import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class SimplePlacementHandler implements IPlacementHandler {
    @Override
    public abstract boolean canHandle(Level level, BlockPos blockPos, BlockState blockState);

    @Override
    public abstract List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b);
}
