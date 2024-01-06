package com.mlntcandy.netask.cntmoneyminer.blocks.miner;

import com.mlntcandy.netask.cntmoneyminer.CNTMoneyMiner;
import com.mlntcandy.netask.cntmoneyminer.utils.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class MinerScreen extends Screen {
    private Button claimButton;
    private Button increaseSuPerRPMButton;
    private Button decreaseSuPerRPMButton;

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(CNTMoneyMiner.MODID, "textures/gui/miner.png");

    private final BlockPos pos;
    private MinerBlockEntity blockEntity;

    private final int imageWidth = 176;
    private final int imageHeight = 166;

    private int guiLeft;
    private int guiTop;

    protected MinerScreen(BlockPos pos) {
        super(Lang.translate("gui.miner.title").component());
        this.pos = pos;
    }

    @Override
    protected void init() {
        super.init();

        this.guiLeft = (this.width - this.imageWidth) / 2;
        this.guiTop = (this.height - this.imageHeight) / 2;

        if (minecraft == null) return;

        Level level = minecraft.level;
        if (level == null) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MinerBlockEntity)) return;

        blockEntity = (MinerBlockEntity) be;

        claimButton = addRenderableWidget(
                Button.builder(
                        Lang.translate("gui.miner.claim").component(),
                        this::handleClaimButton
                )
                        .bounds(guiLeft + 12, guiTop + 105, imageWidth - 24, 20)
                        .build()
        );

        decreaseSuPerRPMButton = addRenderableWidget(
                Button.builder(
                        Lang.text("-").component(),
                        this::handleSuPerRPMDecrease
                )
                        .bounds(guiLeft + 12, guiTop + 90, 12, 12)
                        .build()
        );

        increaseSuPerRPMButton = addRenderableWidget(
                Button.builder(
                                Lang.text("+").component(),
                                this::handleSuPerRPMIncrease
                        )
                        .bounds(guiLeft + imageWidth - 12 - 12, guiTop + 90, 12, 12)
                        .build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        graphics.blit(BACKGROUND, guiLeft, guiTop, 0, 0, imageWidth, imageHeight);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(font, Lang.translate("gui.miner.title").component(), width / 2, guiTop + 5, 0xFFFFFF);
        graphics.drawString(font, blockEntity.getDisplayOwner().component(), guiLeft + 31, guiTop + 24, 0x00FF00);

        graphics.drawString(font, blockEntity.getDisplayPower().component(), guiLeft + 31, guiTop + 41, 0x00FF00);
        graphics.drawString(font, blockEntity.getDisplayMined().component(), guiLeft + 31, guiTop + 58, 0x00FF00);
        graphics.drawString(font, Lang.text("$1337.42").component(), guiLeft + 31, guiTop + 75, 0x00FF00);
        graphics.drawCenteredString(font, blockEntity.getDisplaySuPerRPM().component(), width / 2, guiTop + 92, 0xFFFFFF);

        increaseSuPerRPMButton.active = !maxSuPerRPMReached();
        decreaseSuPerRPMButton.active = !minSuPerRPMReached();
    }

    private void handleClaimButton(Button btn) {

    }

    private void handleSuPerRPMIncrease(Button btn) {
        int newValue = blockEntity.suPerRPM * 2;
        if (newValue > MinerBlockEntity.MAX_SU_RPM) return;
        blockEntity.suPerRPM = newValue;
    }

    private void handleSuPerRPMDecrease(Button btn) {
        int newValue = blockEntity.suPerRPM / 2;
        if (newValue < MinerBlockEntity.MIN_SU_RPM) return;
        blockEntity.suPerRPM = newValue;
    }

    private boolean minSuPerRPMReached() {
        return blockEntity.suPerRPM <= MinerBlockEntity.MIN_SU_RPM;
    }

    private boolean maxSuPerRPMReached() {
        return blockEntity.suPerRPM >= MinerBlockEntity.MAX_SU_RPM;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
