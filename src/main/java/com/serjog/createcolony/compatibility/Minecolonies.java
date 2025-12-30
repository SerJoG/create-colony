package com.serjog.createcolony.compatibility;

import com.serjog.createcolony.ColonyMain;
import net.neoforged.fml.ModList;

import java.util.function.Supplier;

public class Minecolonies {
    /**
     * executes the supplier only if MineColonies is currently loaded.
     * @param toRun A supplier that returns the Runnable to execute.
     *              (Using Supplier<Runnable> prevents classloading crashes if mod is missing)
     */
    public static void runIfInstalled(Supplier<Runnable> toRun) {
        if (ModList.get().isLoaded(ColonyMain.MODID)) {
            SafeRunner.run(toRun);
        }
    }

    public static class SafeRunner {
        private static void run(Supplier<Runnable> toRun) {
            toRun.get().run();
        }
    }
}
