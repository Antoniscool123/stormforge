package dev.stormforge.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Right-click to call a barrage of lightning where you are looking.
 * Sneak + right-click to call a single, precise strike with a short cooldown.
 */
public class StormStaffItem extends Item {
	private static final double RANGE = 64.0;

	public StormStaffItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand) {
		HitResult hit = user.pick(RANGE, 0.0f, false);
		if (hit.getType() == HitResult.Type.MISS) {
			return InteractionResult.FAIL;
		}

		if (level instanceof ServerLevel serverLevel && user instanceof ServerPlayer player) {
			ItemStack stack = user.getItemInHand(hand);
			Vec3 target = hit.getLocation();
			boolean precise = user.isShiftKeyDown();
			int bolts = precise ? 1 : 5;

			for (int i = 0; i < bolts; i++) {
				// Extra bolts form a ring around the central strike.
				double angle = (Math.PI * 2.0 / Math.max(1, bolts - 1)) * i;
				double radius = (precise || i == 0) ? 0.0 : 3.5;
				Vec3 pos = target.add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);

				LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.TRIGGERED);
				if (bolt == null) continue;
				bolt.setPos(pos.x, pos.y, pos.z);
				bolt.setCause(player);
				serverLevel.addFreshEntity(bolt);
			}

			// Shockwave: shove everything near the epicentre.
			for (LivingEntity e : serverLevel.getEntitiesOfClass(LivingEntity.class,
					new AABB(target, target).inflate(5), t -> t != user)) {
				Vec3 push = e.position().subtract(target).normalize().scale(0.9).add(0, 0.5, 0);
				e.setDeltaMovement(e.getDeltaMovement().add(push));
				e.syncVelocity = true;
			}

			serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.x, target.y + 0.5, target.z,
				60, 2.0, 1.0, 2.0, 0.2);
			serverLevel.playSound(null, user.getX(), user.getY(), user.getZ(),
				SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.0f, 1.0f);

			stack.hurtAndBreak(precise ? 1 : 3, player, hand);
			user.getCooldowns().addCooldown(stack, precise ? 20 : 80);
		}
		return InteractionResult.SUCCESS;
	}
}
