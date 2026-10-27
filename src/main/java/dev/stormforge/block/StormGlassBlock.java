package dev.stormforge.block;

import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Glass-like block (TransparentBlock's constructor is protected). */
public class StormGlassBlock extends TransparentBlock {
	public StormGlassBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}
}
