package dev.stormforge.effect;

import dev.stormforge.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Static Charge: zaps the afflicted creature once a second. A level II+ charge also jumps to nearby
 * creatures as a weaker charge, which cannot spread any further (so it can never loop forever).
 */
public class StaticChargeEffect extends MobEffect {
	private static final double ARC_RADIUS = 3.0;

	public StaticChargeEffect() {
		super(MobEffectCategory.HARMFUL, 0x6FD3FF);
	}

	@Override
	public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
		entity.hurtServer(level, level.damageSources().lightningBolt(), 1.0f + amplifier);

		if (amplifier >= 1) {
			for (LivingEntity other : level.getEntitiesOfClass(LivingEntity.class,
					entity.getBoundingBox().inflate(ARC_RADIUS),
					e -> e != entity && e.isAlive() && !(e instanceof Player) && !e.hasEffect(ModEffects.STATIC_CHARGE))) {
				other.addEffect(new MobEffectInstance(ModEffects.STATIC_CHARGE, 60, amplifier - 1));
			}
		}
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return duration % 20 == 0;
	}
}
