package dev.stormforge;

import dev.stormforge.effect.StaticChargeEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public final class ModEffects {
	public static final Holder<MobEffect> STATIC_CHARGE = Registry.registerForHolder(
		BuiltInRegistries.MOB_EFFECT,
		Identifier.fromNamespaceAndPath(StormforgeMod.MOD_ID, "static_charge"),
		(MobEffect) new StaticChargeEffect());

	private ModEffects() {}

	public static void register() {
		// Touching the class registers the static fields.
	}
}
