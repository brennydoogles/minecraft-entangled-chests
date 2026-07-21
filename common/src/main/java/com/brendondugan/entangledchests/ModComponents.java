package com.brendondugan.entangledchests;

import com.brendondugan.entangledchests.component.EntangledKey;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

/**
 * Registers the mod's custom {@link DataComponentType data components}.
 */
public final class ModComponents {

	public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
			DeferredRegister.create(EntangledChests.MOD_ID, Registries.DATA_COMPONENT_TYPE);

	public static final RegistrySupplier<DataComponentType<EntangledKey>> ENTANGLED_KEY =
			COMPONENTS.register("entangled_key", () -> DataComponentType.<EntangledKey>builder()
					.persistent(EntangledKey.CODEC)
					.networkSynchronized(EntangledKey.STREAM_CODEC)
					.build());

	private ModComponents() {
	}

	public static void register() {
		COMPONENTS.register();
	}
}
