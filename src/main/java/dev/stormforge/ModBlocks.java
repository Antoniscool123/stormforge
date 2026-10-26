package dev.stormforge;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Function;

public final class ModBlocks {
	/** Crystal-veined stone that glows faintly and drops Storm Shards. */
	public static final Block STORM_ORE = register("storm_ore",
		props -> new DropExperienceBlock(UniformInt.of(2, 5), props),
		BlockBehaviour.Properties.of()
			.strength(3.0f, 3.0f)
			.requiresCorrectToolForDrops()
			.sound(SoundType.STONE)
			.lightLevel(state -> 4));

	/** Nine Storm Shards compressed. Glows brightly. */
	public static final Block STORM_BLOCK = register("storm_block",
		Block::new,
		BlockBehaviour.Properties.of()
			.strength(5.0f, 6.0f)
			.requiresCorrectToolForDrops()
			.sound(SoundType.AMETHYST)
			.lightLevel(state -> 9));

	private ModBlocks() {}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory,
			BlockBehaviour.Properties properties) {
		Identifier id = Identifier.fromNamespaceAndPath(StormforgeMod.MOD_ID, name);
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
		Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties.setId(blockKey)));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
		Registry.register(BuiltInRegistries.ITEM, itemKey,
			new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
		return block;
	}

	public static void register() {
		// Spawn Storm Ore in every overworld biome. The feature itself lives in data/stormforge/worldgen.
		BiomeModifications.addFeature(
			BiomeSelectors.foundInOverworld(),
			GenerationStep.Decoration.UNDERGROUND_ORES,
			ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(StormforgeMod.MOD_ID, "storm_ore")));
	}
}
