package com.serjog.createcolony;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // Feature toggles
    public static final ModConfigSpec.BooleanValue ENABLE_CLIPBOARD;
    public static final ModConfigSpec.BooleanValue ENABLE_BLUEPRINT_SUPPORT;
    public static final ModConfigSpec.BooleanValue ENABLE_BUILDER_AI;

    // Debug settings
    public static final ModConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING;

    static {
        BUILDER.push("features");
        ENABLE_CLIPBOARD = BUILDER
                .comment("Enable clipboard integration for viewing builder resources")
                .define("enableClipboard", true);
        ENABLE_BLUEPRINT_SUPPORT = BUILDER
                .comment("Enable saving Create blocks in MineColonies blueprints")
                .define("enableBlueprintSupport", true);
        ENABLE_BUILDER_AI = BUILDER
                .comment("Enable Create-aware builder AI extensions")
                .define("enableBuilderAI", true);
        BUILDER.pop();

        BUILDER.push("debug");
        ENABLE_DEBUG_LOGGING = BUILDER
                .comment("Enable debug logging (verbose output)")
                .define("enableDebugLogging", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static boolean isClipboardEnabled() {
        return ENABLE_CLIPBOARD.get();
    }

    public static boolean isBlueprintSupportEnabled() {
        return ENABLE_BLUEPRINT_SUPPORT.get();
    }

    public static boolean isBuilderAIEnabled() {
        return ENABLE_BUILDER_AI.get();
    }

    public static boolean isDebugLoggingEnabled() {
        return ENABLE_DEBUG_LOGGING.get();
    }
}
