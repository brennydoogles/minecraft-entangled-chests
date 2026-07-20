package com.brendondugan.entangledchests;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntangledChests implements ModInitializer {
	public static final String MOD_ID = "entangledchests";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Convenience for building {@code entangledchests:<path>} identifiers. */
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		//
		// Order matters: the block is referenced by the block-entity type, and
		// the block entity seeds pre-keyed bundles (items), so register in
		// dependency order.
		ModComponents.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModItems.initialize();

		LOGGER.info("Entangled Chests initialized.");
	}
}
