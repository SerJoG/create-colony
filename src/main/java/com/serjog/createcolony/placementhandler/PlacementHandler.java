package com.serjog.createcolony.placementhandler;

import com.ldtteam.structurize.placement.handlers.placement.IPlacementHandler;
import com.mojang.logging.LogUtils;
import com.serjog.createcolony.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;

public class PlacementHandler {
    private static void addHandler(IPlacementHandler handler) {
        com.ldtteam.structurize.placement.handlers.placement.PlacementHandlers.add(handler);
    }
    public static void initialiseHandlers(FMLLoadCompleteEvent ignored) {
        addHandler(new BeltPlacementHandler());
        addHandler(new ChainConveyorPlacementHandler());
        addHandler(new CopyCatPlacementHandler());
        addHandler(new DeployerPlacementHandler());
        addHandler(new EncasedShaftPlacementHandler());
        addHandler(new EncasedPipePlacementHandler());
        addHandler(new GearPlacementHandler());
        addHandler(new TrainBogeyPlacementHandler());
        addHandler(new TrainStationPlacementHandler());
        addHandler(new TrackPlacementHandler());
        if (Config.isDebugLoggingEnabled()) {
            addHandler(new DebugPlacementHandler());
        }
    }

    private static class DebugPlacementHandler implements IPlacementHandler {
        private static final Logger LOGGER = LogUtils.getLogger();

        @Override
        public boolean canHandle(Level level, BlockPos blockPos, BlockState blockState) {
            if (level instanceof ServerLevel serverLevel) {
                LOGGER.debug("Structurize checking block: {} at pos: {}", blockState, blockPos);
            }
            return false;
        }

        @Override
        public List<ItemStack> getRequiredItems(Level level, BlockPos blockPos, BlockState blockState, @Nullable CompoundTag compoundTag, boolean b) {
            return List.of();
        }
    }
}
