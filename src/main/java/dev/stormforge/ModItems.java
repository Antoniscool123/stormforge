package dev.stormforge;

import dev.stormforge.item.GaleFeatherItem;
import dev.stormforge.item.RiftAnchorItem;
import dev.stormforge.item.StormStaffItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public final class ModItems {
	public static final Item STORM_SHARD = register("storm_shard", Item::new, new Item.Properties());

	public static final Item STORMCALLER_STAFF = register("stormcaller_staff", StormStaffItem::new,
		new Item.Properties().stacksTo(1).durability(128));

	public static final Item RIFT_ANCHOR = register("rift_anchor", RiftAnchorItem::new,
		new Item.Properties().stacksTo(1).durability(64));

	public static final Item GALE_FEATHER = register("gale_feather", GaleFeatherItem::new,
		new Item.Properties().stacksTo(1).durability(96));

	private ModItems() {}

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
			Identifier.fromNamespaceAndPath(StormforgeMod.MOD_ID, name));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
	}

	public static void register() {
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(e -> e.accept(STORM_SHARD));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(e -> {
			e.accept(STORMCALLER_STAFF);
			e.accept(RIFT_ANCHOR);
			e.accept(GALE_FEATHER);
		});
	}
}
