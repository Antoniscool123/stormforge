package dev.stormforge;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Full Tempest armor ("Stormborn"):
 * - immune to lightning damage
 * - permanent Speed I
 * - Resistance I and Regeneration I while standing in rain or during a thunderstorm
 */
public final class TempestSetBonus {
	private TempestSetBonus() {}

	public static boolean wearsFullSet(ServerPlayer player) {
		return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.TEMPEST_HELMET)
			&& player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.TEMPEST_CHESTPLATE)
			&& player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.TEMPEST_LEGGINGS)
			&& player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.TEMPEST_BOOTS);
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 20 != 0) return;
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
				if (!wearsFullSet(player)) continue;
				player.addEffect(new MobEffectInstance(MobEffects.SPEED, 80, 0, true, false, true));
				ServerLevel level = player.level();
				if (level.isRainingAt(player.blockPosition()) || level.isThundering()) {
					player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 80, 0, true, false, true));
					player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, true, false, true));
				}
			}
		});

		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
			!(entity instanceof ServerPlayer player
				&& source.is(DamageTypeTags.IS_LIGHTNING)
				&& wearsFullSet(player)));
	}
}
