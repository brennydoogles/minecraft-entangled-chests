package com.brendondugan.entangledchests;

import com.brendondugan.entangledchests.block.EntangledChestBlock;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * Registers the mod's blocks. The chest's {@code BlockItem} lives in
 * {@link ModItems} so it is registered after the block itself.
 */
public final class ModBlocks {

	public static final DeferredRegister<Block> BLOCKS =
			DeferredRegister.create(EntangledChests.MOD_ID, Registries.BLOCK);

	public static final RegistrySupplier<EntangledChestBlock> ENTANGLED_CHEST = BLOCKS.register(
			"entangled_chest",
			() -> new EntangledChestBlock(BlockBehaviour.Properties.of()
					.setId(blockKey("entangled_chest"))
					.mapColor(MapColor.COLOR_PURPLE)
					.strength(2.5F)
					.sound(SoundType.WOOD)
					.noOcclusion()));

	private ModBlocks() {
	}

	private static ResourceKey<Block> blockKey(String name) {
		return ResourceKey.create(Registries.BLOCK, EntangledChests.id(name));
	}

	public static void register() {
		BLOCKS.register();
	}
}
