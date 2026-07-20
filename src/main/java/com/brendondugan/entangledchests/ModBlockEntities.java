package com.brendondugan.entangledchests;

import com.brendondugan.entangledchests.block.EntangledChestBlockEntity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Registers the mod's block-entity types.
 */
public final class ModBlockEntities {

	public static final BlockEntityType<EntangledChestBlockEntity> ENTANGLED_CHEST = Registry.register(
			BuiltInRegistries.BLOCK_ENTITY_TYPE,
			EntangledChests.id("entangled_chest"),
			FabricBlockEntityTypeBuilder.create(EntangledChestBlockEntity::new, ModBlocks.ENTANGLED_CHEST).build());

	private ModBlockEntities() {
	}

	/** Forces class-load so the static registrations above run. */
	public static void initialize() {
	}
}
