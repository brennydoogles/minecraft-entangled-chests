package com.brendondugan.entangledchests.client;

import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3fc;

/**
 * Like vanilla {@code ChestSpecialRenderer} (renders the 3D chest model as an item
 * icon) but drawn via {@code submitModelPart} so it honors the enchantment-glint
 * flag — vanilla's chest renderer ignores it, so a vanilla chest item can never
 * glint. Registered under {@code entangledchests:entangled_chest} in the special
 * model registry and referenced from the chest's item model.
 */
public class EntangledChestSpecialRenderer implements NoDataSpecialModelRenderer {

	private final MaterialSet materials;
	private final ChestModel model;
	private final Material material;
	private final float openness;

	public EntangledChestSpecialRenderer(MaterialSet materials, ChestModel model, Material material, float openness) {
		this.materials = materials;
		this.model = model;
		this.material = material;
		this.openness = openness;
	}

	@Override
	public void submit(ItemDisplayContext displayContext, PoseStack poseStack, SubmitNodeCollector collector,
			int light, int overlay, boolean hasFoil, int outlineColor) {
		this.model.setupAnim(this.openness);
		collector.submitModelPart(
				this.model.root(),
				poseStack,
				this.material.renderType(RenderTypes::entitySolid),
				light,
				overlay,
				this.materials.get(this.material),
				false,
				hasFoil,
				-1,
				null,
				outlineColor);
	}

	@Override
	public void getExtents(Consumer<Vector3fc> consumer) {
		PoseStack poseStack = new PoseStack();
		this.model.setupAnim(this.openness);
		this.model.root().getExtentsForGui(poseStack, consumer);
	}

	public record Unbaked(Identifier texture, float openness) implements SpecialModelRenderer.Unbaked {
		public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
				Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness)
		).apply(instance, Unbaked::new));

		@Override
		public MapCodec<Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext context) {
			ChestModel chestModel = new ChestModel(context.entityModelSet().bakeLayer(ModelLayers.CHEST));
			Material material = Sheets.CHEST_MAPPER.apply(this.texture);
			return new EntangledChestSpecialRenderer(context.materials(), chestModel, material, this.openness);
		}
	}
}
