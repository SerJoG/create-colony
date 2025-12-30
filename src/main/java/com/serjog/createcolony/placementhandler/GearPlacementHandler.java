package com.serjog.createcolony.placementhandler;

import com.serjog.createcolony.utils.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import static com.serjog.createcolony.resources.CreateResources.Blocks.andesiteEncasedCogwheel;
import static com.serjog.createcolony.resources.CreateResources.Blocks.andesiteEncasedLargeCogwheel;
import static com.serjog.createcolony.resources.CreateResources.Blocks.brassEncasedCogwheel;
import static com.serjog.createcolony.resources.CreateResources.Blocks.brassEncasedLargeCogwheel;
import static com.serjog.createcolony.resources.CreateResources.Items.cogwheel;
import static com.serjog.createcolony.resources.CreateResources.Items.largeCogwheel;

public class GearPlacementHandler extends SimplePlacementHandler {
    private final Map<ResourceLocation, ItemStack> gearRequirements = Map.of(
            andesiteEncasedCogwheel.getId(), ItemUtils.stackFromDeferred(cogwheel),
            brassEncasedCogwheel.getId(), ItemUtils.stackFromDeferred(cogwheel),
            andesiteEncasedLargeCogwheel.getId(), ItemUtils.stackFromDeferred(largeCogwheel),
            brassEncasedLargeCogwheel.getId(), ItemUtils.stackFromDeferred(largeCogwheel)
    );

    @Override
    public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
        return gearRequirements.containsKey(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()));
    }

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
        final ItemStack requirement = gearRequirements.get(BuiltInRegistries.BLOCK.getKey(blockState.getBlock()));
        return requirement == null ? List.of() : List.of(requirement);
    }
}
