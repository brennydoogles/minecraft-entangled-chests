package com.brendondugan.entangledchests.platform.neoforge;

import java.util.function.BiFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * NeoForge implementation of
 * {@link com.brendondugan.entangledchests.platform.PlatformHelper}. NeoForge's
 * access transformer exposes {@code BlockEntityType}'s constructor (and its
 * {@code BlockEntitySupplier} interface), so no builder is needed here.
 */
public final class PlatformHelperImpl {

	private PlatformHelperImpl() {
	}

	public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(
			BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
		return new BlockEntityType<>(factory::apply, blocks);
	}
}
