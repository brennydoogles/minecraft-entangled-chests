package com.brendondugan.entangledchests.block;

import com.brendondugan.entangledchests.EntangledBundleItem;
import com.brendondugan.entangledchests.ModBlockEntities;
import com.brendondugan.entangledchests.ModComponents;
import com.brendondugan.entangledchests.component.EntangledKey;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/**
 * A single-chest-style block backed by {@link EntangledChestBlockEntity}. Rendered
 * by a custom block-entity renderer (the block model itself is invisible).
 */
public class EntangledChestBlock extends BaseEntityBlock {

	public static final MapCodec<EntangledChestBlock> CODEC = simpleCodec(EntangledChestBlock::new);
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

	public EntangledChestBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<EntangledChestBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EntangledChestBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide()
				? createTickerHelper(type, ModBlockEntities.ENTANGLED_CHEST.get(), EntangledChestBlockEntity::lidAnimateTick)
				: null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (level instanceof ServerLevel serverLevel
				&& level.getBlockEntity(pos) instanceof EntangledChestBlockEntity chest
				&& chest.getChestId() == null) {
			chest.initialize(serverLevel, stack.get(DataComponents.CUSTOM_NAME));
		}
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		// Right-clicking the chest with an unkeyed bundle keys it to this chest.
		if (stack.getItem() instanceof EntangledBundleItem && !stack.has(ModComponents.ENTANGLED_KEY.get())) {
			if (level instanceof ServerLevel
					&& level.getBlockEntity(pos) instanceof EntangledChestBlockEntity chest
					&& chest.getChestId() != null) {
				String name = chest.getCustomName() != null ? chest.getCustomName().getString() : null;
				stack.set(ModComponents.ENTANGLED_KEY.get(),
						new EntangledKey(chest.getChestId(), java.util.Optional.ofNullable(name)));
				level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.7F, 1.0F);
			}
			return InteractionResult.SUCCESS;
		}
		// Otherwise fall through to useWithoutItem, which opens the GUI.
		return super.useItemOn(stack, state, level, pos, player, hand, hit);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level instanceof ServerLevel && level.getBlockEntity(pos) instanceof EntangledChestBlockEntity chest) {
			player.openMenu(chest);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof EntangledChestBlockEntity chest) {
			chest.recheckOpen();
		}
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
	}
}
