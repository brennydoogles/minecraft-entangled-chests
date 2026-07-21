package com.brendondugan.entangledchests.fabric;

import com.brendondugan.entangledchests.EntangledChests;
import com.brendondugan.entangledchests.client.EntangledChestSpecialRenderer;
import com.brendondugan.entangledchests.client.EntangledChestsClient;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

public final class EntangledChestsFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntangledChestsClient.init();

		// Custom "special" item model for the chest that honors the enchantment glint.
		// ID_MAPPER is private in vanilla; Fabric API's transitive access wideners open it.
		SpecialModelRenderers.ID_MAPPER.put(
				EntangledChests.id("entangled_chest"), EntangledChestSpecialRenderer.Unbaked.MAP_CODEC);
	}
}
