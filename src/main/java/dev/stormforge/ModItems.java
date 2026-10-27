package dev.stormforge;

import dev.stormforge.item.GaleFeatherItem;
import dev.stormforge.item.RiftAnchorItem;
import dev.stormforge.item.StormBladeItem;
import dev.stormforge.item.StormStaffItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class ModItems {
	/** Every item (including block items) in registration order; the Stormforge creative tab shows these. */
	public static final List<Item> ALL = new ArrayList<>();

	// ---- Materials -----------------------------------------------------------------------------
	public static final Item STORM_SHARD = register("storm_shard", Item::new, new Item.Properties());
	public static final Item TEMPEST_INGOT = register("tempest_ingot", Item::new,
		new Item.Properties().fireResistant());

	// ---- Relics --------------------------------------------------------------------------------
	public static final Item STORMCALLER_STAFF = register("stormcaller_staff", StormStaffItem::new,
		new Item.Properties().stacksTo(1).durability(128));
	public static final Item RIFT_ANCHOR = register("rift_anchor", RiftAnchorItem::new,
		new Item.Properties().stacksTo(1).durability(64));
	public static final Item GALE_FEATHER = register("gale_feather", GaleFeatherItem::new,
		new Item.Properties().stacksTo(1).durability(96));

	// ---- Storm Blade (diamond tier, repaired with shards) ---------------------------------------
	public static final TagKey<Item> STORM_REPAIR = TagKey.create(Registries.ITEM, id("storm_repair"));
	public static final ToolMaterial STORM_MATERIAL = new ToolMaterial(
		BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1800, 8.0f, 3.0f, 18, STORM_REPAIR);
	public static final Item STORM_BLADE = register("storm_blade", StormBladeItem::new,
		new Item.Properties().sword(STORM_MATERIAL, 3.0f, -2.4f).fireResistant());

	// ---- Tempest tools and armor (netherite tier, repaired with Tempest Ingots) ------------------
	public static final TagKey<Item> TEMPEST_REPAIR = TagKey.create(Registries.ITEM, id("tempest_repair"));
	public static final ToolMaterial TEMPEST_TOOL = new ToolMaterial(
		BlockTags.INCORRECT_FOR_NETHERITE_TOOL, 2400, 9.0f, 4.0f, 20, TEMPEST_REPAIR);

	public static final Item TEMPEST_PICKAXE = register("tempest_pickaxe", Item::new,
		new Item.Properties().pickaxe(TEMPEST_TOOL, 1.0f, -2.8f).fireResistant());
	public static final Item TEMPEST_AXE = register("tempest_axe", AxeItem::new,
		new Item.Properties().axe(TEMPEST_TOOL, 5.0f, -3.0f).fireResistant());
	public static final Item TEMPEST_SHOVEL = register("tempest_shovel", ShovelItem::new,
		new Item.Properties().shovel(TEMPEST_TOOL, 1.5f, -3.0f).fireResistant());
	public static final Item TEMPEST_HOE = register("tempest_hoe", HoeItem::new,
		new Item.Properties().hoe(TEMPEST_TOOL, -4.0f, 0.0f).fireResistant());

	public static final ResourceKey<EquipmentAsset> TEMPEST_ASSET =
		ResourceKey.create(EquipmentAssets.ROOT_ID, id("tempest"));
	public static final ArmorMaterial TEMPEST_ARMOR = new ArmorMaterial(
		42,
		Map.of(ArmorType.BOOTS, 3, ArmorType.LEGGINGS, 6, ArmorType.CHESTPLATE, 8, ArmorType.HELMET, 3),
		20, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, TEMPEST_REPAIR, TEMPEST_ASSET);

	public static final Item TEMPEST_HELMET = register("tempest_helmet", Item::new,
		new Item.Properties().humanoidArmor(TEMPEST_ARMOR, ArmorType.HELMET).fireResistant());
	public static final Item TEMPEST_CHESTPLATE = register("tempest_chestplate", Item::new,
		new Item.Properties().humanoidArmor(TEMPEST_ARMOR, ArmorType.CHESTPLATE).fireResistant());
	public static final Item TEMPEST_LEGGINGS = register("tempest_leggings", Item::new,
		new Item.Properties().humanoidArmor(TEMPEST_ARMOR, ArmorType.LEGGINGS).fireResistant());
	public static final Item TEMPEST_BOOTS = register("tempest_boots", Item::new,
		new Item.Properties().humanoidArmor(TEMPEST_ARMOR, ArmorType.BOOTS).fireResistant());

	// ---- Consumables ---------------------------------------------------------------------------
	public static final Item THUNDER_APPLE = register("thunder_apple", Item::new,
		new Item.Properties().rarity(Rarity.RARE).food(
			new FoodProperties.Builder().nutrition(6).saturationModifier(1.2f).alwaysEdible().build(),
			Consumable.builder()
				.consumeSeconds(1.6f)
				.animation(ItemUseAnimation.EAT)
				.sound(SoundEvents.GENERIC_EAT)
				.hasConsumeParticles(true)
				.onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
					new MobEffectInstance(MobEffects.SPEED, 1200, 1),
					new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1),
					new MobEffectInstance(MobEffects.RESISTANCE, 600, 0))))
				.build()));

	public static final Item STORM_VIAL = register("storm_vial", Item::new,
		new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON).component(DataComponents.CONSUMABLE,
			Consumable.builder()
				.consumeSeconds(1.2f)
				.animation(ItemUseAnimation.DRINK)
				.sound(SoundEvents.GENERIC_DRINK)
				.onConsume(new ApplyStatusEffectsConsumeEffect(List.of(
					new MobEffectInstance(MobEffects.SPEED, 4800, 1),
					new MobEffectInstance(MobEffects.JUMP_BOOST, 4800, 1))))
				.build()));

	private ModItems() {}

	public static Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(StormforgeMod.MOD_ID, name);
	}

	/** Records an item so it shows up in the Stormforge creative tab. */
	public static <T extends Item> T track(T item) {
		ALL.add(item);
		return item;
	}

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id(name));
		return track(Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key))));
	}

	public static void register() {
		// Touching the class registers the static fields.
	}
}
