package dev.stormforge;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class ModComponents {
	/** The saved position of a Rift Anchor (dimension + block position). */
	public static final DataComponentType<GlobalPos> ANCHOR = Registry.register(
		BuiltInRegistries.DATA_COMPONENT_TYPE,
		Identifier.fromNamespaceAndPath(StormforgeMod.MOD_ID, "anchor"),
		DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).build()
	);

	private ModComponents() {}

	public static void register() {
		// Touching the class registers the static fields.
	}
}
