package com.brendondugan.entangledchests.util;

import java.util.Optional;
import java.util.UUID;

import com.brendondugan.entangledchests.block.EntangledChestBlockEntity;
import com.brendondugan.entangledchests.saveddata.EntangledChestRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Routes items from an entangled bundle into its keyed chest.
 */
public final class EntangledTransfer {

	private EntangledTransfer() {
	}

	/**
	 * Inserts as much of {@code stack} as possible into the chest keyed by
	 * {@code chestId}, mutating {@code stack} in place.
	 *
	 * @return the number of items actually moved (0 if the chest is gone or full)
	 */
	public static int deposit(MinecraftServer server, UUID chestId, ItemStack stack) {
		EntangledChestBlockEntity chest = resolve(server, chestId);
		if (chest == null) {
			return 0;
		}
		int before = stack.getCount();
		insertInto(chest, stack);
		int moved = before - stack.getCount();
		if (moved > 0) {
			chest.setChanged();
		}
		return moved;
	}

	/**
	 * Resolves the live chest block entity for an id, healing the registry if the
	 * entry is stale (the chest was removed without going through the normal break
	 * path, e.g. by a fill command).
	 */
	private static @Nullable EntangledChestBlockEntity resolve(MinecraftServer server, UUID chestId) {
		EntangledChestRegistry registry = EntangledChestRegistry.get(server);
		Optional<EntangledChestRegistry.Entry> entry = registry.lookup(chestId);
		if (entry.isEmpty()) {
			return null;
		}
		ServerLevel level = server.getLevel(entry.get().dimension());
		if (level == null) {
			return null;
		}
		BlockPos pos = entry.get().pos();
		// getBlockEntity loads the chunk on the server if needed.
		if (level.getBlockEntity(pos) instanceof EntangledChestBlockEntity chest
				&& chestId.equals(chest.getChestId())) {
			return chest;
		}
		registry.unregister(chestId);
		return null;
	}

	private static void insertInto(Container container, ItemStack stack) {
		int size = container.getContainerSize();
		// First top up existing matching stacks.
		for (int i = 0; i < size && !stack.isEmpty(); i++) {
			ItemStack slot = container.getItem(i);
			if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
				int max = Math.min(container.getMaxStackSize(), slot.getMaxStackSize());
				int room = max - slot.getCount();
				if (room > 0) {
					int moved = Math.min(room, stack.getCount());
					slot.grow(moved);
					stack.shrink(moved);
				}
			}
		}
		// Then fill empty slots.
		for (int i = 0; i < size && !stack.isEmpty(); i++) {
			if (container.getItem(i).isEmpty()) {
				int max = Math.min(container.getMaxStackSize(), stack.getMaxStackSize());
				int moved = Math.min(max, stack.getCount());
				container.setItem(i, stack.copyWithCount(moved));
				stack.shrink(moved);
			}
		}
	}
}
