package com.brendondugan.entangledchests.client;

import com.brendondugan.entangledchests.ModBlockEntities;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

/**
 * Loader-agnostic client setup, invoked by each platform's client initializer.
 *
 * <p>Note: registering the chest's custom "special" item model (the one that honors
 * the enchantment glint) is <em>not</em> done here. Vanilla's
 * {@code SpecialModelRenderers.ID_MAPPER} is private — Fabric only exposes it via
 * its transitive access wideners, while NeoForge provides a dedicated
 * {@code RegisterSpecialModelRendererEvent}. Each platform module therefore does
 * that registration its own way.
 */
public final class EntangledChestsClient {

	private EntangledChestsClient() {
	}

	public static void init() {
		// Must be deferred: this runs at mod-construction time, and on NeoForge the
		// deferred registry object does not exist yet (Fabric registers eagerly, so
		// calling .get() directly here would only break on NeoForge).
		ModBlockEntities.ENTANGLED_CHEST.listen(
				type -> BlockEntityRendererRegistry.register(type, EntangledChestRenderer::new));
	}
}
