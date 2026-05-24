package it.nonlodico34.ironbatteries.block.batteryslot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

public class BatterySlotRenderer implements BlockEntityRenderer<BatterySlotBlockEntity> {

    public BatterySlotRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BatterySlotBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction facing = be.getBlockState().getValue(BatterySlotBlock.FACING);

        for (int i = 0; i < 2; i++) {
            ItemStack stack = be.getItemHandler().getStackInSlot(i);

            if (!stack.isEmpty()) {
                poseStack.pushPose();

                poseStack.translate(0.5f, 0.5f, 0.5f);
                float fRot = switch (facing) {
                    case SOUTH -> 180f;
                    case WEST -> 90f;
                    case EAST -> 270f;
                    default -> 0f;
                };
                poseStack.mulPose(Axis.YP.rotationDegrees(fRot + 180f));
                float xOffset = (i == 0) ? -0.225f : 0.225f;
                poseStack.translate(xOffset, 0, 0);

                poseStack.scale(0.6f, 0.6f, 0.6f);

                if(be.isConnectedToNetwork()) {
                    float gameTime = be.getLevel() != null ? be.getLevel().getGameTime() + partialTick : 0;

                    float cycleDuration = 40.0f;
                    float progress = (gameTime % cycleDuration) / cycleDuration;

                    float easedProgress = progress < 0.5
                            ? 4 * progress * progress * progress
                            : 1 - (float) Math.pow(-2 * progress + 2, 3) / 2;

                    float rotation = easedProgress * 360.0f;
                    poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
                }

                Minecraft.getInstance().getItemRenderer().renderStatic(
                        stack,
                        ItemDisplayContext.FIXED,
                        packedLight,
                        packedOverlay,
                        poseStack,
                        bufferSource,
                        be.getLevel(),
                        0
                );

                poseStack.popPose();
            }
        }
    }

    @Override
    public boolean shouldRenderOffScreen(BatterySlotBlockEntity be) {
        return false;
    }
}
