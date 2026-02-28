package com.serjog.createcolony.datapackhandler;

import com.mojang.logging.LogUtils;
import com.serjog.createcolony.ColonyMain;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@EventBusSubscriber(modid = ColonyMain.MODID)
public class DataPackRegistrar {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String MOD_RAILS = "railways";         // Create: Steam 'n' Rails
    private static final String MOD_INTERIORS = "create_interiors";       // Create: Interiors

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        boolean hasRails = ModList.get().isLoaded(MOD_RAILS);
        boolean hasInteriors = ModList.get().isLoaded(MOD_INTERIORS);

        // --- PRIORITY LOGIC ---

        // Max priority
        if (hasRails && hasInteriors) {
            registerPack(event, "rail_int_compat_pack", "Create Colony: Ultimate Compat");
        }
        else if (hasRails) {
            registerPack(event, "railways_pack", "Create: Steam & Rails Compat");
        }
        else if (hasInteriors) {
            registerPack(event, "interiors_pack", "Create: Interiors Compat");
        }
        // Default
        else {
            registerPack(event, "base_pack", "Create Colony: Base");
        }
    }

    private static void registerPack(AddPackFindersEvent event, String folderName, String title) {
        try {
            var modFile = ModList.get().getModFileById(ColonyMain.MODID).getFile();
            Path resourcePath = modFile.findResource("compat_packs/" + folderName);

            if (resourcePath == null || !Files.exists(resourcePath)) {
                LOGGER.warn("Create Colonies: WARNING! Unable to find the pack '{}'. " +
                                "Check if 'src/main/resources/compat_packs/{}' exists.",
                        folderName, folderName);
                return;
            }

            PackLocationInfo info = new PackLocationInfo(
                    ColonyMain.MODID + "_" + folderName,
                    Component.literal(title),
                    PackSource.BUILT_IN,
                    Optional.empty()
            );

            PackSelectionConfig config = new PackSelectionConfig(true, Pack.Position.TOP, true);

            Pack pack = Pack.readMetaAndCreate(
                    info,
                    new PathPackResources.PathResourcesSupplier(resourcePath),
                    event.getPackType(),
                    config
            );

            if (pack != null) {
                event.addRepositorySource(consumer -> consumer.accept(pack));
            }
        } catch (Exception e) {
            LOGGER.error("Create Colony: Critical error loading pack '{}'", folderName, e);
        }
    }
}