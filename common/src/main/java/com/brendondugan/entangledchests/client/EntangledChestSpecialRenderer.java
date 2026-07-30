package com.brendondugan.entangledchests.client;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.joml.Vector3fc;

/**
 * Renders the entangled chest as a 3D item icon with our editable texture,
 * mirroring vanilla {@code ChestSpecialRenderer}. Registered under
 * {@code entangledchests:entangled_chest} in the special model registry and
 * referenced from the chest's item model.
 *
 * <p>The enchantment glint is drawn as a second {@code submitModel} pass with the
 * entity-glint render type (mirroring vanilla {@code TridentSpecialRenderer}), since
 * 26.2 removed the foil flag from {@code submitModelPart}. This two-pass approach
 * uses only overloads that are identical on 26.1 and 26.2, so one build glints on both.
 */
public class EntangledChestSpecialRenderer implements NoDataSpecialModelRenderer {

	private final SpriteGetter sprites;
	private final ChestModel model;
	private final SpriteId sprite;
	private final float openness;

	public EntangledChestSpecialRenderer(SpriteGetter sprites, ChestModel model, SpriteId sprite, float openness) {
		this.sprites = sprites;
		this.model = model;
		this.sprite = sprite;
		this.openness = openness;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil,
			int outlineColor) {
		// Base pass. Uses the SpriteId submitModel overload, which is byte-identical on
		// 26.1 and 26.2 (so one build spans both).
		collector.order(0).submitModel(this.model, this.openness, poseStack, light, overlay, -1, this.sprite,
				this.sprites, outlineColor, null);
		// Glint pass: a second submitModel with the entity-glint render type. 26.2 removed
		// the foil flag from submitModelPart, so this two-pass approach (mirroring vanilla
		// TridentSpecialRenderer) is how a 3D model glints on both versions.
		if (hasFoil) {
			collector.order(1).submitModel(this.model, this.openness, poseStack, RenderTypes.entityGlint(), light,
					overlay, outlineColor, null);
		}
	}

	@Override
	public void getExtents(Consumer<Vector3fc> consumer) {
		PoseStack poseStack = new PoseStack();
		this.model.setupAnim(this.openness);
		this.model.root().getExtentsForGui(poseStack, consumer);
	}

	public record Unbaked(Identifier texture, float openness) implements NoDataSpecialModelRenderer.Unbaked {
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
				Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness)
		).apply(instance, Unbaked::new));

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public EntangledChestSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
			ChestModel chestModel = new ChestModel(context.entityModelSet().bakeLayer(ChestRenderer.LAYERS.select(ChestType.SINGLE)));
			SpriteId spriteId = Sheets.CHEST_MAPPER.apply(this.texture);
			return new EntangledChestSpecialRenderer(context.sprites(), chestModel, spriteId, this.openness);
		}
	}
}
