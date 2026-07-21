package com.brendondugan.entangledchests.client;

import com.brendondugan.entangledchests.EntangledChests;
import com.brendondugan.entangledchests.block.EntangledChestBlock;
import com.brendondugan.entangledchests.block.EntangledChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Renders the entangled chest with our own editable texture
 * ({@code textures/entity/chest/entangled.png}, auto-stitched into the vanilla
 * chest atlas), reusing the vanilla chest model and lid animation.
 */
public class EntangledChestRenderer implements BlockEntityRenderer<EntangledChestBlockEntity, ChestRenderState> {

	private static final Material MATERIAL = new Material(Sheets.CHEST_SHEET, EntangledChests.id("entity/chest/entangled"));

	private final MaterialSet materials;
	private final ChestModel model;

	public EntangledChestRenderer(BlockEntityRendererProvider.Context context) {
		this.materials = context.materials();
		this.model = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
	}

	@Override
	public ChestRenderState createRenderState() {
		return new ChestRenderState();
	}

	@Override
	public void extractRenderState(EntangledChestBlockEntity chest, ChestRenderState state, float partialTick,
			Vec3 offset, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
		BlockEntityRenderer.super.extractRenderState(chest, state, partialTick, offset, crumbling);
		BlockState blockState = chest.getBlockState();
		state.type = ChestType.SINGLE;
		state.angle = blockState.getValue(EntangledChestBlock.FACING).toYRot();
		state.open = chest.getOpenNess(partialTick);
	}

	@Override
	public void submit(ChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(-state.angle));
		poseStack.translate(-0.5F, -0.5F, -0.5F);

		float openness = 1.0F - state.open;
		openness = 1.0F - openness * openness * openness;

		RenderType renderType = MATERIAL.renderType(RenderTypes::entityCutout);
		TextureAtlasSprite sprite = this.materials.get(MATERIAL);
		// Render via submitModelPart (rather than submitModel) so we can pass the foil
		// flag and give the placed chest the same permanent enchantment glint as its item.
		this.model.setupAnim(openness);
		collector.submitModelPart(this.model.root(), poseStack, renderType, state.lightCoords,
				OverlayTexture.NO_OVERLAY, sprite, false, true, -1, state.breakProgress, 0);

		poseStack.popPose();
	}
}
