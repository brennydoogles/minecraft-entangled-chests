package com.brendondugan.entangledchests.fabric;

import com.brendondugan.entangledchests.EntangledChests;

import net.fabricmc.api.ModInitializer;

public final class EntangledChestsFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		EntangledChests.init();
	}
}
