package it.nonlodico34.ironbatteries.block.monitor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

import it.nonlodico34.ironbatteries.bigleagues.BigIntHelper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

public class MonitorRenderer implements BlockEntityRenderer<MonitorBlockEntity> {

    private final Map<MonitorBlockEntity, EnergyTracker> trackerMap = new WeakHashMap<>();
    private final float FONT_SCALE = 0.011f;
    private final float PADDING = 7.0f;

    public MonitorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(MonitorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!be.isMultiblockMaster()) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        List<LineConfig> lines = new ArrayList<>();

        if (!be.isConnected()) {
            lines.add(new LineConfig("Disconnected", TextAlign.CENTER, 0xFF0000));
        } else {
            BigInteger capacity = be.getCapacityFE();
            BigInteger stored = be.getStoredFE();
            float percent = capacity.compareTo(BigInteger.ZERO) > 0
                    ? new BigDecimal(stored).multiply(new BigDecimal(100)).divide(new BigDecimal(capacity), 2, RoundingMode.HALF_UP).floatValue()
                    : 0f;

            EnergyTracker tracker = trackerMap.computeIfAbsent(be, k -> new EnergyTracker());

            if (Minecraft.getInstance().level != null) {
                long currentTick = Minecraft.getInstance().level.getGameTime();
                tracker.update(stored, currentTick);
            }
            BigInteger averageRate = tracker.getAverageRate();

            String energyText;
            if (be.hasInfiniteBattery()) {
                energyText = BigIntHelper.formatBigInteger(stored, 2, true) + " FE";
            } else {
                energyText = BigIntHelper.formatBigInteger(stored, 0, true) + " / " + BigIntHelper.formatBigInteger(capacity, 0, true) + " FE";
            }
            lines.add(new LineConfig(energyText, TextAlign.CENTER, 0xFFFFFF));

            String percentText = be.hasInfiniteBattery()
                    ? "Infinite Battery"
                    : "Fill: " + (int) percent + "%";
            lines.add(new LineConfig(percentText, TextAlign.CENTER, 0xFFFFFF));

            int charLimit = 30;
            int filledChars = be.hasInfiniteBattery() ? charLimit : (int) ((percent * charLimit) / 100);
            int emptyChars = charLimit - filledChars;

            List<TextSegment> barSegments = new ArrayList<>();
            barSegments.add(new TextSegment("[", 0xFFFFFF));

            if (filledChars > 0) {
                if (be.hasInfiniteBattery() && Minecraft.getInstance().level != null) {
                    float gameTime = Minecraft.getInstance().level.getGameTime() + partialTick;

                    for (int i = 0; i < filledChars; i++) {
                        double angle = (i * 0.4) - (gameTime * 0.15);
                        int waveColor = getInterpolatedColor(angle);
                        barSegments.add(new TextSegment("|", waveColor));
                    }
                } else {
                    int fillColor = getBarColor(percent / 100.0f);
                    barSegments.add(new TextSegment("|".repeat(filledChars), fillColor));
                }
            }

            if (emptyChars > 0) {
                barSegments.add(new TextSegment("|".repeat(emptyChars), 0x555555));
            }
            barSegments.add(new TextSegment("]", 0xFFFFFF));
            lines.add(new LineConfig(barSegments, TextAlign.CENTER));

            lines.add(new LineConfig("", TextAlign.CENTER, 0xFFFFFF));
            lines.add(new LineConfig("Rate:", TextAlign.CENTER, 0xFFFFFF));

            List<TextSegment> rateSegments = new ArrayList<>();
            String formattedRate = BigIntHelper.formatBigInteger(averageRate.abs(), 2, true);
            if (averageRate.compareTo(BigInteger.ZERO) > 0) {
                rateSegments.add(new TextSegment("+" + formattedRate + " FE/t", 0x55FF55));
            } else if (averageRate.compareTo(BigInteger.ZERO) < 0) {
                rateSegments.add(new TextSegment("-" + formattedRate + " FE/t", 0xFF5555));
            } else {
                rateSegments.add(new TextSegment(formattedRate + " FE/t", 0x888888));
            }
            lines.add(new LineConfig(rateSegments, TextAlign.CENTER));
        }

        int size = be.getMultiblockSize();
        Direction facing = be.getBlockState().getValue(MonitorBlock.FACING);
        Direction rightDir = getScreenRightDirection(facing);

        poseStack.pushPose();

        float halfSpan = (size - 1) * 0.5f;
        float centerX = 0.5f + (rightDir.getStepX() * halfSpan);
        float centerY = size * 0.5f;
        float centerZ = 0.5f + (rightDir.getStepZ() * halfSpan);
        poseStack.translate(centerX, centerY, centerZ);

        switch (facing) {
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case SOUTH -> poseStack.mulPose(Axis.YP.rotationDegrees(0));
            case WEST  -> poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            case EAST  -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }

        poseStack.translate(0.0, 0.0, 0.502);

        float textScale = FONT_SCALE * size;
        poseStack.scale(textScale, -textScale, 1.0f);

        drawLinesBlock(poseStack, bufferSource, lines, 0f, 0f, 2, font);

        poseStack.popPose();
    }

    private int getInterpolatedColor(double angle) {
        float wave = (float) (Math.sin(angle) + 1.0) / 2.0f;
        int r = (int) (231 + (wave * (255 - 231)));
        int g = (int) (18 + (wave * (143 - 18)));
        int b = 255;
        return (r << 16) | (g << 8) | b;
    }

    @Override
    public AABB getRenderBoundingBox(MonitorBlockEntity be) {
        int size = Math.max(1, be.getMultiblockSize());
        BlockPos master = be.getMasterPos();
        Direction facing = be.getBlockState().getValue(MonitorBlock.FACING);
        Direction rightDir = getScreenRightDirection(facing);

        BlockPos farCorner = master.relative(rightDir, size - 1).above(size - 1);
        return AABB.encapsulatingFullBlocks(master, farCorner).inflate(1.0D);
    }

    private Direction getScreenRightDirection(Direction facing) {
        return switch (facing) {
            case NORTH -> Direction.WEST;
            case SOUTH -> Direction.EAST;
            case EAST -> Direction.NORTH;
            case WEST -> Direction.SOUTH;
            default -> Direction.WEST;
        };
    }

    private int getBarColor(float ratio) {
        ratio = Math.max(0.0F, Math.min(1.0F, ratio));
        int r, g;
        if (ratio < 0.5F) {
            r = 255;
            g = (int) (ratio * 2.0F * 255);
        } else {
            r = (int) ((1.0F - (ratio - 0.5F) * 2.0F) * 255);
            g = 255;
        }
        return (r << 16) | (g << 8);
    }

    private void drawLinesBlock(PoseStack poseStack, MultiBufferSource bufferSource, List<LineConfig> lines, float x, float y, int lineSpacing, Font font) {
        if (lines == null || lines.isEmpty()) return;

        int lineHeight = font.lineHeight;
        int totalHeight = (lines.size() * lineHeight) + ((lines.size() - 1) * lineSpacing);
        float startY = y - (totalHeight / 2f);

        for (int i = 0; i < lines.size(); i++) {
            LineConfig line = lines.get(i);
            float currentY = startY + (i * (lineHeight + lineSpacing));
            float currentX = getAlignedX(line, x, font);

            for (TextSegment segment : line.segments()) {
                if (segment.text().isEmpty()) continue;

                font.drawInBatch(
                        segment.text(), currentX, currentY,
                        segment.color(),
                        false,
                        poseStack.last().pose(),
                        bufferSource,
                        Font.DisplayMode.NORMAL,
                        0,
                        LightTexture.FULL_BRIGHT
                );
                currentX += font.width(segment.text());
            }
        }
    }

    private float getAlignedX(LineConfig line, float baseX, Font font) {
        int textWidth = 0;
        for (TextSegment segment : line.segments()) {
            textWidth += font.width(segment.text());
        }

        float halfScreenWidth = 0.5f / FONT_SCALE;
        float padding = PADDING;

        return switch (line.alignment()) {
            case CENTER -> baseX - (textWidth / 2f);
            case RIGHT  -> halfScreenWidth - padding - textWidth;
            case LEFT   -> -halfScreenWidth + padding;
        };
    }

    private enum TextAlign {
        CENTER, RIGHT, LEFT
    }

    private record TextSegment(String text, int color) {}

    private record LineConfig(List<TextSegment> segments, TextAlign alignment) {
        public LineConfig(String text, TextAlign alignment, int color) {
            this(List.of(new TextSegment(text, color)), alignment);
        }
    }

    private static class EnergyTracker {
        private BigInteger lastEnergy = null;
        private long lastTick = -1;
        private final Queue<BigInteger> history = new LinkedList<>();
        private BigInteger historySum = BigInteger.ZERO;
        private static final int MAX_TICKS = 20;

        public void update(BigInteger currentEnergy, long currentTick) {
            if (currentTick == lastTick) return;

            if (lastEnergy != null && lastTick != -1) {
                long elapsedTicks = currentTick - lastTick;
                BigInteger totalDelta = currentEnergy.subtract(lastEnergy);

                if (elapsedTicks >= MAX_TICKS) {
                    history.clear();
                    BigInteger avg = totalDelta.divide(BigInteger.valueOf(elapsedTicks));
                    for (int i = 0; i < MAX_TICKS; i++) {
                        history.add(avg);
                    }
                    historySum = avg.multiply(BigInteger.valueOf(MAX_TICKS));
                } else {
                    BigInteger deltaPerTick = totalDelta.divide(BigInteger.valueOf(elapsedTicks));
                    int remainder = totalDelta.remainder(BigInteger.valueOf(elapsedTicks)).intValue();

                    for (int i = 0; i < (int) elapsedTicks; i++) {
                        BigInteger d = deltaPerTick;
                        if (remainder > 0 && i < remainder) {
                            d = d.add(BigInteger.ONE);
                        } else if (remainder < 0 && i < -remainder) {
                            d = d.subtract(BigInteger.ONE);
                        }

                        history.add(d);
                        historySum = historySum.add(d);

                        if (history.size() > MAX_TICKS) {
                            BigInteger oldest = history.poll();
                            if (oldest != null) {
                                historySum = historySum.subtract(oldest);
                            }
                        }
                    }
                }
            }
            lastEnergy = currentEnergy;
            lastTick = currentTick;
        }

        public BigInteger getAverageRate() {
            if (history.isEmpty()) {
                return BigInteger.ZERO;
            }

            List<BigInteger> list = (List<BigInteger>) history;
            int firstNonZero = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).signum() != 0) {
                    firstNonZero = i;
                    break;
                }
            }

            if (firstNonZero == -1) {
                return BigInteger.ZERO;
            }

            int lastNonZero = -1;
            for (int i = list.size() - 1; i >= firstNonZero; i--) {
                if (list.get(i).signum() != 0) {
                    lastNonZero = i;
                    break;
                }
            }

            int effectiveSize = lastNonZero - firstNonZero + 1;
            return historySum.divide(BigInteger.valueOf(effectiveSize));
        }
    }
}
