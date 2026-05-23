package it.nonlodico34.ironbatteries.block.atmosphericcondenser;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.nonlodico34.ironbatteries.IronBatteries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LightningRodBlock;

public class AtmosphericCondenserRenderer implements BlockEntityRenderer<AtmosphericCondenserBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;
    private static final ModelResourceLocation RING_MODEL_LOCATION = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(IronBatteries.MODID, "block/ring"), "standalone");

    public AtmosphericCondenserRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(AtmosphericCondenserBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float gameTime = be.getLevel() != null ? be.getLevel().getGameTime() + partialTick : 0;
        BlockState blockState = be.getBlockState();

        BakedModel ringModel = Minecraft.getInstance().getModelManager().getModel(RING_MODEL_LOCATION);

        if (ringModel != Minecraft.getInstance().getModelManager().getMissingModel()) {
            Direction facing = blockState.getValue(LightningRodBlock.FACING);
            float distance = 0.15f;
            float[] yOffsets = {-distance, 0.0f, distance};
            for (int i = 0; i < yOffsets.length; i++) {
                yOffsets[i] += 0.45f;
            }

            for (int i = 0; i < yOffsets.length; i++) {
                poseStack.pushPose();

                poseStack.translate(0.5, 0.5, 0.5);

                switch (facing) {
                    case DOWN:
                        poseStack.mulPose(Axis.XP.rotationDegrees(180));
                        break;
                    case NORTH:
                        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                        break;
                    case SOUTH:
                        poseStack.mulPose(Axis.XP.rotationDegrees(90));
                        break;
                    case WEST:
                        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                        break;
                    case EAST:
                        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
                        break;
                    case UP:
                    default:
                        break;
                }

                poseStack.translate(0, yOffsets[i], 0);

                float ringBob = (float) Math.sin(gameTime * 0.1f + i * (Math.PI / 2)) * 0.02f;
                poseStack.translate(0, ringBob, 0);

                float speedMult = 1.0f;
                if (i == 0) {
                    speedMult = 2.0f;
                } else if (i == 1) {
                    speedMult = -1.5f;
                } else if (i == 2) {
                    speedMult = 1.0f;
                }

                float ringRotation = gameTime * speedMult;
                poseStack.mulPose(Axis.YP.rotationDegrees(ringRotation));

                poseStack.translate(-0.5, -0.5, -0.5);

                VertexConsumer vc = bufferSource.getBuffer(RenderType.cutout());
                blockRenderer.getModelRenderer().renderModel(
                        poseStack.last(),
                        vc,
                        blockState,
                        ringModel,
                        1.0f, 1.0f, 1.0f,
                        packedLight,
                        packedOverlay
                );

                poseStack.popPose();
            }
        }
    }
}
