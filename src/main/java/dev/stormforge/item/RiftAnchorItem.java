package dev.stormforge.item;

import dev.stormforge.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
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

/**
 * Sneak + right-click: plant the anchor at your feet.
 * Right-click: tear a rift and snap back to the anchor (same dimension only).
 */
public class RiftAnchorItem extends Item {
	public RiftAnchorItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand) {
		if (!(level instanceof ServerLevel serverLevel) || !(user instanceof ServerPlayer player)) {
			return InteractionResult.SUCCESS;
		}
		ItemStack stack = user.getItemInHand(hand);

		if (user.isShiftKeyDown()) {
			stack.set(ModComponents.ANCHOR, GlobalPos.of(level.dimension(), user.blockPosition()));
			serverLevel.playSound(null, user.getX(), user.getY(), user.getZ(),
				SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.5f);
			serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, user.getX(), user.getY() + 1, user.getZ(),
				40, 0.4, 0.8, 0.4, 0.1);
			player.sendOverlayMessage(Component.literal("Rift anchor set."));
			return InteractionResult.SUCCESS;
		}

		GlobalPos anchor = stack.get(ModComponents.ANCHOR);
		if (anchor == null) {
			player.sendOverlayMessage(Component.literal("No anchor set. Sneak + right-click to plant one."));
			return InteractionResult.FAIL;
		}
		if (!anchor.dimension().equals(level.dimension())) {
			player.sendOverlayMessage(Component.literal("The anchor is in another dimension."));
			return InteractionResult.FAIL;
		}

		BlockPos dest = anchor.pos();
		serverLevel.sendParticles(ParticleTypes.PORTAL, user.getX(), user.getY() + 1, user.getZ(),
			60, 0.4, 0.8, 0.4, 0.5);
		serverLevel.playSound(null, user.getX(), user.getY(), user.getZ(),
			SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 0.8f);

		player.teleportTo(dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5);
		player.resetFallDistance();

		serverLevel.sendParticles(ParticleTypes.PORTAL, dest.getX() + 0.5, dest.getY() + 1, dest.getZ() + 0.5,
			60, 0.4, 0.8, 0.4, 0.5);
		serverLevel.playSound(null, dest.getX() + 0.5, dest.getY(), dest.getZ() + 0.5,
			SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);

		stack.hurtAndBreak(1, player, hand);
		user.getCooldowns().addCooldown(stack, 100);
		return InteractionResult.SUCCESS;
	}
}
