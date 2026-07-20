package com.brendondugan.entangledchests;

import java.util.function.Function;

import com.brendondugan.entangledchests.block.EntangledChestBlock;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Registers the mod's blocks and their {@link BlockItem}s.
 */
public final class ModBlocks {

	public static final EntangledChestBlock ENTANGLED_CHEST = register(
			"entangled_chest",
			EntangledChestBlock::new,
			BlockBehaviour.Properties.of()
					.mapColor(MapColor.COLOR_PURPLE)
					.strength(2.5F)
					.sound(SoundType.WOOD)
					.noOcclusion());

	public static final Item ENTANGLED_CHEST_ITEM = registerItem("entangled_chest", ENTANGLED_CHEST);

	private ModBlocks() {
	}

	private static EntangledChestBlock register(String name, Function<BlockBehaviour.Properties, EntangledChestBlock> factory,
			BlockBehaviour.Properties properties) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, EntangledChests.id(name));
		EntangledChestBlock block = factory.apply(properties.setId(key));
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}

	private static Item registerItem(String name, Block block) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, EntangledChests.id(name));
		BlockItem item = new BlockItem(block, new Item.Properties()
				.setId(key)
				.useBlockDescriptionPrefix()
				.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	/** Forces class-load so the static registrations above run. */
	public static void initialize() {
	}
}
