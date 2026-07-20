package com.brendondugan.entangledchests.client;

import com.brendondugan.entangledchests.EntangledChests;
import com.brendondugan.entangledchests.ModBlockEntities;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

public class EntangledChestsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRenderers.register(ModBlockEntities.ENTANGLED_CHEST, EntangledChestRenderer::new);

		// Custom "special" item model for the chest that honors the enchantment glint.
		SpecialModelRenderers.ID_MAPPER.put(
				EntangledChests.id("entangled_chest"), EntangledChestSpecialRenderer.Unbaked.MAP_CODEC);
	}
}
