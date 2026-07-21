package com.brendondugan.entangledchests.neoforge;

import com.brendondugan.entangledchests.EntangledChests;
import com.brendondugan.entangledchests.client.EntangledChestSpecialRenderer;
import com.brendondugan.entangledchests.client.EntangledChestsClient;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@Mod(value = EntangledChests.MOD_ID, dist = Dist.CLIENT)
public final class EntangledChestsNeoForgeClient {

	public EntangledChestsNeoForgeClient(IEventBus modEventBus) {
		EntangledChestsClient.init();
		modEventBus.addListener(EntangledChestsNeoForgeClient::onRegisterSpecialModelRenderers);
	}

	/**
	 * NeoForge's counterpart to Fabric's {@code SpecialModelRenderers.ID_MAPPER} —
	 * registers the chest's glint-aware special item model.
	 */
	private static void onRegisterSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
		event.register(EntangledChests.id("entangled_chest"), EntangledChestSpecialRenderer.Unbaked.MAP_CODEC);
	}
}
