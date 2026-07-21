package com.brendondugan.entangledchests.component;

import java.util.Optional;
import java.util.UUID;

import com.brendondugan.entangledchests.saveddata.EntangledChestRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * The data component stored on an entangled bundle that links it to an entangled
 * chest.
 *
 * @param chestId    the unique id of the keyed chest (also its key in the
 *                   {@link EntangledChestRegistry})
 * @param cachedName the chest's display name, if it was renamed on an anvil; kept
 *                   fresh server-side so the client tooltip can render it without
 *                   access to server state. Empty means "show the short id".
 */
public record EntangledKey(UUID chestId, Optional<String> cachedName) {

	public static final Codec<EntangledKey> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			UUIDUtil.CODEC.fieldOf("chest_id").forGetter(EntangledKey::chestId),
			Codec.STRING.optionalFieldOf("cached_name").forGetter(EntangledKey::cachedName)
	).apply(instance, EntangledKey::new));

	public static final StreamCodec<ByteBuf, EntangledKey> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, EntangledKey::chestId,
			ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), EntangledKey::cachedName,
			EntangledKey::new);

	/** Short, human-friendly form of the chest id, e.g. {@code a1b2c3d4}. */
	public String shortId() {
		return chestId.toString().substring(0, 8);
	}
}
