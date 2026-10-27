package dev.stormforge;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModTabs {
	public static final CreativeModeTab MAIN = Registry.register(
		BuiltInRegistries.CREATIVE_MODE_TAB,
		Identifier.fromNamespaceAndPath(StormforgeMod.MOD_ID, "main"),
		CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
			.title(Component.translatable("itemGroup.stormforge.main"))
			.icon(() -> new ItemStack(ModItems.STORMCALLER_STAFF))
			.displayItems((parameters, output) -> ModItems.ALL.forEach(output::accept))
			.build());

	private ModTabs() {}

	public static void register() {
		// Touching the class registers the tab.
	}
}
