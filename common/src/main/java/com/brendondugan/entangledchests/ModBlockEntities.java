package com.brendondugan.entangledchests;

import com.brendondugan.entangledchests.block.EntangledChestBlockEntity;
import com.brendondugan.entangledchests.platform.PlatformHelper;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Registers the mod's block-entity types.
 */
public final class ModBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
			DeferredRegister.create(EntangledChests.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

	public static final RegistrySupplier<BlockEntityType<EntangledChestBlockEntity>> ENTANGLED_CHEST =
			BLOCK_ENTITIES.register("entangled_chest", () -> PlatformHelper.createBlockEntityType(
					EntangledChestBlockEntity::new, ModBlocks.ENTANGLED_CHEST.get()));

	private ModBlockEntities() {
	}

	public static void register() {
		BLOCK_ENTITIES.register();
	}
}
