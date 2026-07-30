package com.brendondugan.entangledchests.client;

import com.brendondugan.entangledchests.EntangledChests;
import com.brendondugan.entangledchests.block.EntangledChestBlock;
import com.brendondugan.entangledchests.block.EntangledChestBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Renders the entangled chest with our own editable texture
 * ({@code textures/entity/chest/entangled.png}, auto-stitched into the vanilla
 * chest atlas), reusing the vanilla chest model and lid animation. The placed block
 * always glints (drawn as a second entity-glint {@code submitModel} pass) so it is
 * visibly distinct from a vanilla chest — see {@link EntangledChestSpecialRenderer}.
 */
public class EntangledChestRenderer implements BlockEntityRenderer<EntangledChestBlockEntity, ChestRenderState> {

	private static final SpriteId SPRITE = Sheets.CHEST_MAPPER.apply(EntangledChests.id("entangled"));

	private final SpriteGetter sprites;
	private final ChestModel model;

	public EntangledChestRenderer(BlockEntityRendererProvider.Context context) {
		this.sprites = context.sprites();
		this.model = new ChestModel(context.bakeLayer(ChestRenderer.LAYERS.select(ChestType.SINGLE)));
	}

	@Override
	public ChestRenderState createRenderState() {
		return new ChestRenderState();
	}

	@Override
	public void extractRenderState(EntangledChestBlockEntity chest, ChestRenderState state, float partialTick,
			Vec3 offset, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
		BlockEntityRenderer.super.extractRenderState(chest, state, partialTick, offset, crumbling);
		state.type = ChestType.SINGLE;
		state.facing = chest.getBlockState().getValue(EntangledChestBlock.FACING);
		state.open = chest.getOpenNess(partialTick);
	}

	@Override
	public void submit(ChestRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		poseStack.pushPose();
		poseStack.translate(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
		poseStack.translate(-0.5F, -0.5F, -0.5F);

		float open = 1.0F - state.open;
		open = 1.0F - open * open * open;

		// Base pass (SpriteId submitModel — identical on 26.1 and 26.2).
		collector.order(0).submitModel(this.model, open, poseStack, state.lightCoords, OverlayTexture.NO_OVERLAY, -1,
				SPRITE, this.sprites, 0, state.breakProgress);
		// Glint pass — the placed entangled chest always glints so it's visibly distinct
		// from a vanilla chest. See EntangledChestSpecialRenderer for the two-pass rationale.
		// The GLINT pipeline uses depth-compare EQUAL, so the glint MUST be perfectly coplanar
		// with the base (any offset makes it fail the depth test and vanish); we therefore
		// submit the identical model/pose as the base.
		collector.order(1).submitModel(this.model, open, poseStack, RenderTypes.entityGlint(), state.lightCoords,
				OverlayTexture.NO_OVERLAY, 0, state.breakProgress);

		poseStack.popPose();
	}
}
