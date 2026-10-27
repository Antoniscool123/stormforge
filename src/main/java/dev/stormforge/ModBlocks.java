package dev.stormforge;

import dev.stormforge.block.StormGlassBlock;
import dev.stormforge.block.StormStairBlock;
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
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Function;

public final class ModBlocks {
	// ---- Ore and storage --------------------------------------------------------------------
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

	// ---- Stormstone family ------------------------------------------------------------------
	public static final Block STORMSTONE = register("stormstone", Block::new, stone(2.0f, 6.0f));
	public static final Block STORMSTONE_STAIRS = register("stormstone_stairs",
		props -> new StormStairBlock(STORMSTONE.defaultBlockState(), props), BlockBehaviour.Properties.ofFullCopy(STORMSTONE));
	public static final Block STORMSTONE_SLAB = register("stormstone_slab",
		SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(STORMSTONE));
	public static final Block STORMSTONE_WALL = register("stormstone_wall",
		WallBlock::new, BlockBehaviour.Properties.ofFullCopy(STORMSTONE));
	public static final Block POLISHED_STORMSTONE = register("polished_stormstone", Block::new, stone(2.0f, 6.0f));

	// ---- Storm brick family -----------------------------------------------------------------
	public static final Block STORM_BRICKS = register("storm_bricks", Block::new, stone(2.0f, 6.0f));
	public static final Block STORM_BRICK_STAIRS = register("storm_brick_stairs",
		props -> new StormStairBlock(STORM_BRICKS.defaultBlockState(), props), BlockBehaviour.Properties.ofFullCopy(STORM_BRICKS));
	public static final Block STORM_BRICK_SLAB = register("storm_brick_slab",
		SlabBlock::new, BlockBehaviour.Properties.ofFullCopy(STORM_BRICKS));
	public static final Block STORM_BRICK_WALL = register("storm_brick_wall",
		WallBlock::new, BlockBehaviour.Properties.ofFullCopy(STORM_BRICKS));
	public static final Block CHISELED_STORM_BRICKS = register("chiseled_storm_bricks", Block::new, stone(2.0f, 6.0f));
	public static final Block STORM_PILLAR = register("storm_pillar", RotatedPillarBlock::new, stone(2.0f, 6.0f));

	// ---- Light and glass --------------------------------------------------------------------
	public static final Block STORM_GLASS = register("storm_glass", StormGlassBlock::new,
		BlockBehaviour.Properties.of()
			.strength(0.4f)
			.sound(SoundType.GLASS)
			.lightLevel(state -> 7)
			.noOcclusion()
			.isValidSpawn((state, level, pos, type) -> false)
			.isRedstoneConductor((state, level, pos) -> false)
			.isSuffocating((state, level, pos) -> false)
			.isViewBlocking((state, level, pos, box) -> false));

	public static final Block STORM_LAMP = register("storm_lamp", Block::new,
		BlockBehaviour.Properties.of()
			.strength(0.6f)
			.sound(SoundType.GLASS)
			.lightLevel(state -> 15));

	private ModBlocks() {}

	private static BlockBehaviour.Properties stone(float hardness, float resistance) {
		return BlockBehaviour.Properties.of()
			.strength(hardness, resistance)
			.requiresCorrectToolForDrops()
			.sound(SoundType.STONE);
	}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory,
			BlockBehaviour.Properties properties) {
		Identifier id = ModItems.id(name);
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
		Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties.setId(blockKey)));

		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
		ModItems.track(Registry.register(BuiltInRegistries.ITEM, itemKey,
			new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix())));
		return block;
	}

	public static void register() {
		// World generation features live in data/stormforge/worldgen; here we just add them to biomes.
		BiomeModifications.addFeature(
			BiomeSelectors.foundInOverworld(),
			GenerationStep.Decoration.UNDERGROUND_ORES,
			placed("storm_ore"));
		BiomeModifications.addFeature(
			BiomeSelectors.foundInOverworld(),
			GenerationStep.Decoration.UNDERGROUND_STRUCTURES,
			placed("storm_geode"));
	}

	private static ResourceKey<PlacedFeature> placed(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, ModItems.id(name));
	}
}
