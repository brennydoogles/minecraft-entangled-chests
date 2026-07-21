package com.brendondugan.entangledchests;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.brendondugan.entangledchests.component.EntangledKey;
import com.brendondugan.entangledchests.saveddata.EntangledChestRegistry;
import com.brendondugan.entangledchests.util.EntangledTransfer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/**
 * A real bundle (so item insertion works exactly like vanilla, and stays in sync
 * between client and server via the {@code BUNDLE_CONTENTS} component) with an
 * entanglement twist:
 *
 * <ul>
 *   <li><b>Unkeyed</b>: behaves as an ordinary bundle — add and remove freely.</li>
 *   <li><b>Keyed</b>: items added drain into the keyed chest every tick, and can
 *       no longer be pulled back out. If the chest is full they buffer in the
 *       bundle (up to its normal capacity) until space frees up.</li>
 * </ul>
 *
 * Storing items in the bundle's own component is what fixes insertion: the client
 * can predict it without needing access to the remote chest, so no desync.
 */
public class EntangledBundleItem extends BundleItem {

	public EntangledBundleItem(Properties properties) {
		super(properties);
	}

	private static boolean isKeyed(ItemStack stack) {
		return stack.has(ModComponents.ENTANGLED_KEY.get());
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		// Show the enchantment glint only once the bundle is keyed to a chest.
		return isKeyed(stack) || super.isFoil(stack);
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack bundle, Slot slot, ClickAction action, Player player) {
		// Keyed bundles can't be poured out (right-click onto an empty slot).
		if (isKeyed(bundle) && action == ClickAction.SECONDARY && slot.getItem().isEmpty()) {
			return true;
		}
		return super.overrideStackedOnOther(bundle, slot, action, player);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack bundle, ItemStack carried, Slot slot, ClickAction action,
			Player player, SlotAccess access) {
		// Keyed bundles can't have one item pulled out (right-click with empty hand).
		if (isKeyed(bundle) && action == ClickAction.SECONDARY && carried.isEmpty()) {
			return true;
		}
		return super.overrideOtherStackedOnMe(bundle, carried, slot, action, player, access);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		// Keyed bundles can't be emptied onto the ground by hand.
		if (isKeyed(player.getItemInHand(hand))) {
			return InteractionResult.PASS;
		}
		return super.use(level, player, hand);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		EntangledKey key = stack.get(ModComponents.ENTANGLED_KEY.get());
		if (key == null) {
			return; // unkeyed: an ordinary bundle, nothing to do
		}

		Optional<EntangledChestRegistry.Entry> entry = EntangledChestRegistry.get(level.getServer()).lookup(key.chestId());
		if (entry.isEmpty()) {
			// Keyed chest is gone: unkey. Any buffered contents remain and become a
			// normal (removable) bundle again, so nothing is lost.
			stack.remove(ModComponents.ENTANGLED_KEY.get());
			return;
		}

		Optional<String> currentName = entry.get().name().map(Component::getString);
		if (!currentName.equals(key.cachedName())) {
			stack.set(ModComponents.ENTANGLED_KEY.get(), new EntangledKey(key.chestId(), currentName));
		}

		drainToChest(stack, key, level.getServer());
	}

	/** Moves as much of the bundle's buffered contents into the keyed chest as fits. */
	private static void drainToChest(ItemStack bundle, EntangledKey key, MinecraftServer server) {
		BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
		if (contents == null || contents.isEmpty()) {
			return;
		}
		List<ItemStack> leftovers = new ArrayList<>();
		boolean changed = false;
		for (ItemStack item : contents.itemsCopy()) {
			int before = item.getCount();
			EntangledTransfer.deposit(server, key.chestId(), item); // mutates item
			if (item.getCount() != before) {
				changed = true;
			}
			if (!item.isEmpty()) {
				leftovers.add(item);
			}
		}
		if (changed) {
			bundle.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(leftovers));
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
			Consumer<Component> adder, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, adder, flag);
		EntangledKey key = stack.get(ModComponents.ENTANGLED_KEY.get());
		if (key == null) {
			adder.accept(Component.translatable("tooltip.entangledchests.unkeyed").withStyle(ChatFormatting.GRAY));
		} else {
			String label = key.cachedName().filter(s -> !s.isBlank()).orElse("#" + key.shortId());
			adder.accept(Component.translatable("tooltip.entangledchests.keyed_to", label).withStyle(ChatFormatting.AQUA));
		}
	}
}
