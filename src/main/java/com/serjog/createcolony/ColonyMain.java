package com.serjog.createcolony;

import com.serjog.createcolony.placementhandler.PlacementHandler;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ColonyMain.MODID)
public class ColonyMain {
    public static final String MODID = "createcolony";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ColonyMain(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(PlacementHandler::initialiseHandlers);

        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Create Colonial Setup");
        LOGGER.info("Blueprint Support: {}", Config.isBlueprintSupportEnabled());
        LOGGER.info("Clipboard Integration: {}", Config.isClipboardEnabled());
        LOGGER.info("Builder AI Extensions: {}", Config.isBuilderAIEnabled());
    }
}
