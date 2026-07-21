package com.brendondugan.entangledchests.block;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.brendondugan.entangledchests.ModBlockEntities;
import com.brendondugan.entangledchests.ModComponents;
import com.brendondugan.entangledchests.ModItems;
import com.brendondugan.entangledchests.component.EntangledKey;
import com.brendondugan.entangledchests.saveddata.EntangledChestRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * The entangled chest's inventory + a unique id. Mirrors vanilla
 * {@link net.minecraft.world.level.block.entity.ChestBlockEntity} (single chest,
 * animated lid) but adds an identity and registers/unregisters itself in the
 * {@link EntangledChestRegistry}.
 */
public class EntangledChestBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {

	public static final int CONTAINER_SIZE = 27;
	private static final Component DEFAULT_NAME = Component.translatable("container.entangledchests.entangled_chest");

	private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
	private @Nullable UUID chestId;

	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			playSound(level, pos, SoundEvents.CHEST_OPEN);
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			playSound(level, pos, SoundEvents.CHEST_CLOSE);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
			level.blockEvent(pos, state.getBlock(), 1, newCount);
		}

		@Override
		public boolean isOwnContainer(Player player) {
			return player.containerMenu instanceof ChestMenu chestMenu
					&& chestMenu.getContainer() == EntangledChestBlockEntity.this;
		}
	};
	private final ChestLidController chestLidController = new ChestLidController();

	public EntangledChestBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ENTANGLED_CHEST.get(), pos, state);
	}

	private static void playSound(Level level, BlockPos pos, net.minecraft.sounds.SoundEvent sound) {
		level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
	}

	public @Nullable UUID getChestId() {
		return chestId;
	}

	/**
	 * Called once, when the chest is first placed: assign an id, register it, and
	 * seed the chest with a bundle pre-keyed to itself.
	 */
	public void initialize(ServerLevel level, @Nullable Component customName) {
		this.chestId = UUID.randomUUID();
		Optional<Component> name = Optional.ofNullable(customName);
		EntangledChestRegistry.get(level.getServer()).register(chestId, level.dimension(), worldPosition, name);

		ItemStack bundle = new ItemStack(ModItems.ENTANGLED_BUNDLE.get());
		bundle.set(ModComponents.ENTANGLED_KEY.get(), new EntangledKey(chestId, name.map(Component::getString)));
		this.setItem(0, bundle);
		this.setChanged();
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		super.preRemoveSideEffects(pos, state); // drops contents like a vanilla container
		if (level instanceof ServerLevel serverLevel && chestId != null) {
			EntangledChestRegistry.get(serverLevel.getServer()).unregister(chestId);
		}
	}

	// --- container ---------------------------------------------------------

	@Override
	public int getContainerSize() {
		return CONTAINER_SIZE;
	}

	@Override
	protected Component getDefaultName() {
		return DEFAULT_NAME;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> list) {
		this.items = list;
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return ChestMenu.threeRows(containerId, inventory, this);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, this.items);
		this.chestId = input.read("ChestId", UUIDUtil.CODEC).orElse(null);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, this.items);
		if (chestId != null) {
			output.store("ChestId", UUIDUtil.CODEC, chestId);
		}
	}

	// --- lid animation / opener tracking -----------------------------------

	public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, EntangledChestBlockEntity be) {
		be.chestLidController.tickLid();
	}

	@Override
	public boolean triggerEvent(int id, int param) {
		if (id == 1) {
			chestLidController.shouldBeOpen(param > 0);
			return true;
		}
		return super.triggerEvent(id, param);
	}

	@Override
	public void startOpen(ContainerUser user) {
		if (!remove && !user.getLivingEntity().isSpectator()) {
			openersCounter.incrementOpeners(user.getLivingEntity(), getLevel(), getBlockPos(), getBlockState(),
					user.getContainerInteractionRange());
		}
	}

	@Override
	public void stopOpen(ContainerUser user) {
		if (!remove && !user.getLivingEntity().isSpectator()) {
			openersCounter.decrementOpeners(user.getLivingEntity(), getLevel(), getBlockPos(), getBlockState());
		}
	}

	@Override
	public List<ContainerUser> getEntitiesWithContainerOpen() {
		return openersCounter.getEntitiesWithContainerOpen(getLevel(), getBlockPos());
	}

	@Override
	public float getOpenNess(float partialTick) {
		return chestLidController.getOpenness(partialTick);
	}

	public void recheckOpen() {
		if (!remove) {
			openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
		}
	}
}
