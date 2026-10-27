package dev.stormforge;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StormforgeMod implements ModInitializer {
	public static final String MOD_ID = "stormforge";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModComponents.register();
		ModEffects.register();
		ModBlocks.register();
		ModItems.register();
		ModTabs.register();
		TempestSetBonus.register();
		LOGGER.info("Stormforge loaded. The sky is listening.");
	}
}
