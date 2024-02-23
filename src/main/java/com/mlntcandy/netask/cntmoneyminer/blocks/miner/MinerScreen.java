package com.mlntcandy.netask.cntmoneyminer.blocks.miner;

import com.mlntcandy.netask.cntmoneyminer.CNTMoneyMiner;
import com.mlntcandy.netask.cntmoneyminer.network.ModPackets;
import com.mlntcandy.netask.cntmoneyminer.network.packets.MinerInteractionC2SPacket;
import com.mlntcandy.netask.cntmoneyminer.network.packets.MinerInteractionType;
import com.mlntcandy.netask.cntmoneyminer.utils.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class MinerScreen extends Screen {
    private Button claimButton;
    private Button autoClaimToggle = null;

    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(CNTMoneyMiner.MODID, "textures/gui/miner.png");

    private final BlockPos pos;
    private MinerBlockEntity blockEntity;

    private final int imageWidth = 176;
    private final int imageHeight = 116;

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
                        .bounds(
                                guiLeft + 12,
                                guiTop + 90,
                                blockEntity.isAutoClaimable ? (imageWidth / 2 - 14) : imageWidth - 24,
                                20
                        )
                        .build()
        );
        if (blockEntity.isAutoClaimable) {
            autoClaimToggle = addRenderableWidget(
                    Button.builder(
                            Lang.translate("gui.miner.auto_claim_off").component(),
                            this::handleAutoClaimToggle
                    )
                            .bounds(
                                    guiLeft + (imageWidth / 2) + 2,
                                    guiTop + 90,
                                    (imageWidth / 2) - 14,
                                    20
                            )
                            .build()
                    );
        }
    }

    private void handleAutoClaimToggle(Button btn) {
        blockEntity.autoClaim = !blockEntity.autoClaim;
        blockEntity.ticksUntilAutoClaim = MinerBlockEntity.AUTO_CLAIM_TIMEOUT;
        sendInteractionPacket(
                blockEntity.autoClaim ?
                        MinerInteractionType.AUTO_CLAIM_SET_ON
                        : MinerInteractionType.AUTO_CLAIM_SET_OFF
        );
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        graphics.blit(BACKGROUND, guiLeft, guiTop, 0, blockEntity.isAutoClaimable ? imageHeight : 0, imageWidth, imageHeight);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(font, Lang.translate("gui.miner.title").component(), width / 2, guiTop + 5, 0xFFFFFF);
        graphics.drawString(font, blockEntity.getDisplayOwner().component(), guiLeft + 31, guiTop + 24, 0x00FF00);

        graphics.drawString(font, blockEntity.getDisplayPower().component(), guiLeft + 31, guiTop + 41, 0x00FF00);
        graphics.drawString(font, blockEntity.getDisplayMined().component(), guiLeft + 31, guiTop + 58, 0x00FF00);
        graphics.drawString(font, blockEntity.getDisplayMinedInMoney().component(), guiLeft + 31, guiTop + 75, 0x00FF00);

        claimButton.setMessage(
                blockEntity.canClaimManually() ?
                        Lang.translate("gui.miner.claim").component()
                        : Lang.translate("gui.miner.claim")
                            .space()
                            .text("[")
                            .add(
                                blockEntity.getDisplaySecondsUntilClaim()
                            )
                            .text("]")
                            .component()
        );
        claimButton.active = blockEntity.canClaimManually();

        if (autoClaimToggle != null) {
            autoClaimToggle.setMessage(
                    blockEntity.autoClaim ?
                            Lang.translate("gui.miner.auto_claim_on")
                                    .space()
                                    .text("[")
                                    .add(
                                            blockEntity.getDisplaySecondsUntilAutoClaim()
                                    )
                                    .text("]")
                                    .component()
                            : Lang.translate("gui.miner.auto_claim_off").component()
            );
        }
    }

    private void handleClaimButton(Button btn) {
        blockEntity.claimManually();
        sendInteractionPacket(MinerInteractionType.CLAIM_REQUEST);
    }

    private void sendInteractionPacket(MinerInteractionType type) {
        if (minecraft == null) return;
        Level level = minecraft.level;
        if (level == null) return;
        ModPackets.sendToServer(new MinerInteractionC2SPacket(type, pos));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
