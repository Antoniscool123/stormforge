package dev.stormforge.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Right-click to launch yourself in the direction you are looking. Cancels fall damage. */
public class GaleFeatherItem extends Item {
	public GaleFeatherItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand) {
		Vec3 look = user.getLookAngle();
		user.setDeltaMovement(look.scale(1.9).add(0, 0.25, 0));
		user.hurtMarked = true;
		user.resetFallDistance();

		if (level instanceof ServerLevel serverLevel && user instanceof ServerPlayer player) {
			ItemStack stack = user.getItemInHand(hand);
			serverLevel.sendParticles(ParticleTypes.CLOUD, user.getX(), user.getY() + 0.2, user.getZ(),
				30, 0.3, 0.1, 0.3, 0.08);
			serverLevel.playSound(null, user.getX(), user.getY(), user.getZ(),
				SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 1.2f, 1.4f);
			stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
			user.getCooldowns().addCooldown(stack, 40);
		}
		return InteractionResult.SUCCESS;
	}
}
