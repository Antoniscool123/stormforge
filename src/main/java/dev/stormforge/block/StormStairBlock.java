package dev.stormforge.block;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** StairBlock's constructor is protected in 26.3, so mods expose it through a subclass. */
public class StormStairBlock extends StairBlock {
	public StormStairBlock(BlockState base, BlockBehaviour.Properties properties) {
		super(base, properties);
	}
}
