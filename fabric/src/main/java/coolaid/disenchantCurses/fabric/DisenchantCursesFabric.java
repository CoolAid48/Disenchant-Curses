package coolaid.disenchantCurses.fabric;

import coolaid.disenchantCurses.DisenchantCurses;
import net.fabricmc.api.ModInitializer;

public final class DisenchantCursesFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        DisenchantCurses.init();
    }
}
