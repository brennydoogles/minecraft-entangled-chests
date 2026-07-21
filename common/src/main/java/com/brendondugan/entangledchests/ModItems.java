package com.brendondugan.entangledchests;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BundleContents;

/**
 * Registers the mod's items (including the chest's block item) and adds them to a
 * creative tab.
 */
public final class ModItems {

	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(EntangledChests.MOD_ID, Registries.ITEM);

	public static final RegistrySupplier<Item> ENTANGLEMENT_CRYSTAL = ITEMS.register(
			"entanglement_crystal",
			() -> new Item(new Item.Properties()
					.setId(itemKey("entanglement_crystal"))
					.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

	public static final RegistrySupplier<Item> ENTANGLED_CHEST = ITEMS.register(
			"entangled_chest",
			() -> new BlockItem(ModBlocks.ENTANGLED_CHEST.get(), new Item.Properties()
					.setId(itemKey("entangled_chest"))
					.useBlockDescriptionPrefix()
					.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

	// A real bundle: non-stacking, with the default (empty) bundle contents so the
	// inherited bundle insertion logic engages. Keyed bundles drain into their chest.
	// The glint is not forced via a component here — EntangledBundleItem#isFoil shows
	// it only while the bundle is keyed.
	public static final RegistrySupplier<Item> ENTANGLED_BUNDLE = ITEMS.register(
			"entangled_bundle",
			() -> new EntangledBundleItem(new Item.Properties()
					.setId(itemKey("entangled_bundle"))
					.stacksTo(1)
					.component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)));

	private ModItems() {
	}

	private static ResourceKey<Item> itemKey(String name) {
		return ResourceKey.create(Registries.ITEM, EntangledChests.id(name));
	}

	public static void register() {
		ITEMS.register();

		CreativeTabRegistry.appendBuiltin(
				BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.FUNCTIONAL_BLOCKS),
				ENTANGLEMENT_CRYSTAL, ENTANGLED_CHEST, ENTANGLED_BUNDLE);
	}
}
