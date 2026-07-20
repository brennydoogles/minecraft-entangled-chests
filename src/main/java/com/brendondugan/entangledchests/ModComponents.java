package com.brendondugan.entangledchests;

import java.util.function.UnaryOperator;

import com.brendondugan.entangledchests.component.EntangledKey;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Registers the mod's custom {@link DataComponentType data components}.
 */
public final class ModComponents {

	public static final DataComponentType<EntangledKey> ENTANGLED_KEY = register(
			"entangled_key",
			builder -> builder
					.persistent(EntangledKey.CODEC)
					.networkSynchronized(EntangledKey.STREAM_CODEC));

	private ModComponents() {
	}

	private static DataComponentType<EntangledKey> register(
			String name, UnaryOperator<DataComponentType.Builder<EntangledKey>> operator) {
		return Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				EntangledChests.id(name),
				operator.apply(DataComponentType.builder()).build());
	}

	/** Forces class-load so the static registrations above run. */
	public static void initialize() {
	}
}
