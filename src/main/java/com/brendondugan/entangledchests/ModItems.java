package com.brendondugan.entangledchests;

import java.util.function.Function;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BundleContents;

/**
 * Registers the mod's items and adds them to a creative tab.
 *
 * <p>For now every item is a plain {@link Item}; the actual behaviour (entangled
 * inventories, linked bundles, etc.) will be layered on in a later pass.
 */
public final class ModItems {

	public static final Item ENTANGLEMENT_CRYSTAL = register(
			"entanglement_crystal",
			Item::new,
			new Item.Properties().component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));

	// The entangled chest is a block; its item is registered in ModBlocks.

	// A real bundle: non-stacking, with the default (empty) bundle contents so the
	// inherited bundle insertion logic engages. Keyed bundles drain into their chest.
	// The glint is not forced via a component here — EntangledBundleItem#isFoil shows
	// it only while the bundle is keyed.
	public static final Item ENTANGLED_BUNDLE = register(
			"entangled_bundle",
			EntangledBundleItem::new,
			new Item.Properties().stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));

	private ModItems() {
	}

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(
				Registries.ITEM,
				Identifier.fromNamespaceAndPath(EntangledChests.MOD_ID, name));
		Item item = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	/**
	 * Referencing this method from the mod initializer forces this class to load,
	 * which runs the static registrations above.
	 */
	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
			entries.accept(ENTANGLEMENT_CRYSTAL);
			entries.accept(ModBlocks.ENTANGLED_CHEST_ITEM);
			entries.accept(ENTANGLED_BUNDLE);
		});
	}
}
