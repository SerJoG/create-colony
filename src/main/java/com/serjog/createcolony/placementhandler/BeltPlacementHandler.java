package com.serjog.createcolony.placementhandler;

import com.ldtteam.structurize.api.RotationMirror;
import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.serjog.createcolony.resources.CreateResources;
import com.serjog.createcolony.utils.BlockPosUtil;
import com.serjog.createcolony.utils.ItemUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BeltPlacementHandler implements IPlacementHandler {
    @Override
    public boolean canHandle(Level level, BlockPos pos, BlockState state) {
        return state.is(AllBlocks.BELT.get());
    }

    private final Map<BlockPos, Map<BlockPos, List<ItemStack>>> beltItems = new HashMap<>();
    private final Map<BlockPos, SortedMap<BlockPos, BeltInfo>> beltParts = new HashMap<>();

    @Override
    public List<ItemStack> getRequiredItems(Level level, BlockPos pos, BlockState state, @Nullable CompoundTag tag, boolean b) {
        final List<ItemStack> requiredItems = new ArrayList<>();

        if (state.hasProperty(BeltBlock.PART)) {
            // End pieces need a Shaft
            if (state.getValue(BeltBlock.PART) != BeltPart.MIDDLE) {
                requiredItems.add(ItemUtils.stackFromDeferred(CreateResources.Items.shaft));
            }
            // Start piece represents the belt cost and items on it
            if (state.getValue(BeltBlock.PART) == BeltPart.START) {
                requiredItems.add(ItemUtils.stackFromDeferred(CreateResources.Blocks.belt));

                if (tag != null && tag.contains("Inventory", Tag.TAG_COMPOUND)) {
                    final CompoundTag inv = tag.getCompound("Inventory");
                    final var items = inv.getList("Items", Tag.TAG_COMPOUND);
                    for (Tag itemEntity : items) {
                        if (itemEntity instanceof CompoundTag itemTag) {
                            requiredItems.add(ItemUtils.stackFromNBT(level, itemTag.getCompound("Item")));
                        }
                    }
                }
            }
        }

        if (tag != null && tag.contains("Controller")) {
            final BlockPos controllerPos = BlockPosUtil.readBlockPos(tag, "Controller");
            final var allBeltItems = beltItems.computeIfAbsent(controllerPos, ignored -> new HashMap<>());
            final int length = tag.getInt("Length");

            if (allBeltItems.size() + 1 == length && !allBeltItems.containsKey(pos)) {
                for (var items : allBeltItems.values()) {
                    requiredItems.addAll(items);
                    items.clear();
                }
                allBeltItems.put(pos, requiredItems);
            } else if (allBeltItems.size() < length) {
                allBeltItems.put(pos, requiredItems);
                return List.of();
            } else {
                return allBeltItems.getOrDefault(pos, List.of());
            }
        }
        return requiredItems;
    }

    @Override
    public ActionProcessingResult handle(Level world, BlockPos pos, BlockState state, @Nullable CompoundTag tag, boolean complete, BlockPos center, RotationMirror settings) {
        if (tag == null || !tag.contains("Controller")) return ActionProcessingResult.DENY;

        final BlockPos controllerPos = BlockPosUtil.readBlockPos(tag, "Controller");
        final int length = tag.getInt("Length");

        final var knownParts = beltParts.computeIfAbsent(controllerPos, ignored -> new TreeMap<>());
        knownParts.put(pos, new BeltInfo(pos, state, tag));

        if (knownParts.size() == length) {
            for (Map.Entry<BlockPos, BeltInfo> entry : knownParts.entrySet()) {
                final var info = entry.getValue();

                // Flag 3 is the standard 1.21 update flag (Update Block + Send to Client)
                if (!world.setBlock(info.pos(), info.state(), 3)) {
                    for (var alreadyPlaced : knownParts.headMap(entry.getKey()).values()) {
                        world.removeBlock(alreadyPlaced.pos(), false);
                    }
                    return ActionProcessingResult.DENY;
                }

                // Apply BlockEntity data (TileEntity)
                if (info.tag() != null) {
                    var be = world.getBlockEntity(info.pos());
                    if (be != null) {
                        be.loadWithComponents(info.tag(), world.registryAccess());
                    }
                }
            }
            beltParts.remove(controllerPos);
            beltItems.remove(controllerPos);
        }

        return ActionProcessingResult.SUCCESS;
    }

    private record BeltInfo(BlockPos pos, BlockState state, @Nullable CompoundTag tag) {}
}
