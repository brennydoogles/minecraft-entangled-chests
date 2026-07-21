package com.brendondugan.entangledchests;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loader-agnostic entrypoint. Each platform module has a thin initializer that
 * simply calls {@link #init()}.
 */
public final class EntangledChests {

	public static final String MOD_ID = "entangledchests";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private EntangledChests() {
	}

	/** Convenience for building {@code entangledchests:<path>} identifiers. */
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void init() {
		// Order matters: blocks are referenced by the block-entity type and the
		// block item, so register in dependency order.
		ModComponents.register();
		ModBlocks.register();
		ModBlockEntities.register();
		ModItems.register();

		LOGGER.info("Entangled Chests initialized.");
	}
}
