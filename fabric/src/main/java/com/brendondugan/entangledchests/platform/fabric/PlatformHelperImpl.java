package com.brendondugan.entangledchests.platform.fabric;

import java.util.function.BiFunction;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fabric implementation of
 * {@link com.brendondugan.entangledchests.platform.PlatformHelper}.
 */
public final class PlatformHelperImpl {

	private PlatformHelperImpl() {
	}

	public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(
			BiFunction<BlockPos, BlockState, T> factory, Block... blocks) {
		return FabricBlockEntityTypeBuilder.create(factory::apply, blocks).build();
	}
}
