package dev.stormforge.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/**
 * A sword that arcs lightning from whatever you hit to up to two other nearby creatures.
 * The arcs are visual-only bolts (no fire) that deal a flat 5 damage.
 */
public class StormBladeItem extends Item {
	private static final double CHAIN_RADIUS = 6.0;
	private static final int MAX_CHAINS = 2;
	private static final float CHAIN_DAMAGE = 5.0f;

	public StormBladeItem(Properties properties) {
		super(properties);
	}

	@Override
	public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		super.postHurtEnemy(stack, target, attacker);
		if (!(target.level() instanceof ServerLevel level)) {
			return;
		}

		Vec3 origin = target.position();
		List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class,
				new AABB(origin, origin).inflate(CHAIN_RADIUS),
				e -> e != target && e != attacker && e.isAlive() && !e.isAlliedTo(attacker))
			.stream()
			.sorted(Comparator.comparingDouble(e -> e.distanceToSqr(target)))
			.limit(MAX_CHAINS)
			.toList();

		for (LivingEntity victim : victims) {
			victim.hurtServer(level, level.damageSources().lightningBolt(), CHAIN_DAMAGE);

			LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
			if (bolt != null) {
				bolt.setPos(victim.getX(), victim.getY(), victim.getZ());
				bolt.setVisualOnly(true);
				level.addFreshEntity(bolt);
			}
			level.sendParticles(ParticleTypes.ELECTRIC_SPARK, victim.getX(), victim.getY() + 1, victim.getZ(),
				12, 0.3, 0.5, 0.3, 0.15);
		}

		if (!victims.isEmpty()) {
			level.playSound(null, target.getX(), target.getY(), target.getZ(),
				SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 0.8f, 1.4f);
		}
	}
}
