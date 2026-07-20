package com.brendondugan.entangledchests.saveddata;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * World-level source of truth for entangled chests: maps each chest's unique id
 * to its location and optional name.
 *
 * <p>An entangled bundle is "keyed" only while its chest id is present here. When
 * a chest is destroyed its id is removed, which is what makes bundles keyed to it
 * become unkeyed — even for offline players, since the check happens the next time
 * their bundle ticks.
 *
 * <p>Stored globally (on the overworld's data storage) so it is independent of
 * which chunks/dimensions are loaded.
 */
public class EntangledChestRegistry extends SavedData {

	public record Entry(ResourceKey<Level> dimension, BlockPos pos, Optional<Component> name) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(Entry::dimension),
				BlockPos.CODEC.fieldOf("pos").forGetter(Entry::pos),
				ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(Entry::name)
		).apply(instance, Entry::new));
	}

	private static final Codec<EntangledChestRegistry> CODEC = Codec
			.unboundedMap(UUIDUtil.STRING_CODEC, Entry.CODEC)
			.xmap(EntangledChestRegistry::new, registry -> registry.entries);

	public static final SavedDataType<EntangledChestRegistry> TYPE = new SavedDataType<>(
			"entangledchests_chests", EntangledChestRegistry::new, CODEC, DataFixTypes.LEVEL);

	private final Map<UUID, Entry> entries;

	public EntangledChestRegistry() {
		this.entries = new HashMap<>();
	}

	private EntangledChestRegistry(Map<UUID, Entry> entries) {
		this.entries = new HashMap<>(entries);
	}

	public static EntangledChestRegistry get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(TYPE);
	}

	public void register(UUID id, ResourceKey<Level> dimension, BlockPos pos, Optional<Component> name) {
		entries.put(id, new Entry(dimension, pos.immutable(), name));
		setDirty();
	}

	public void unregister(UUID id) {
		if (entries.remove(id) != null) {
			setDirty();
		}
	}

	public Optional<Entry> lookup(UUID id) {
		return Optional.ofNullable(entries.get(id));
	}
}
