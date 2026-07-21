package com.brendondugan.entangledchests.platform;

import java.util.function.BiFunction;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Small loader-specific escape hatches. Architectury swaps these for the
 * {@code PlatformHelperImpl} of the platform being built.
 */
public final class PlatformHelper {

	private PlatformHelper() {
	}

	/**
	 * Builds a {@link BlockEntityType}. Vanilla 1.21.11 has no accessible
	 * constructor or builder for it (its {@code BlockEntitySupplier} is
	 * package-private), so each loader supplies its own route: Fabric via
	 * {@code FabricBlockEntityTypeBuilder}, NeoForge via the constructor its access
	 * transformer exposes.
	 */
	@ExpectPlatform
	public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(
			BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
		throw new AssertionError();
	}
}
