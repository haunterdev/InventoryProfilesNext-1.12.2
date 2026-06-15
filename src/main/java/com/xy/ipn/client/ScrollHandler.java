package com.xy.ipn.client;

import com.xy.ipn.client.action.ClientScrollTransfer;
import com.xy.ipn.util.ContainerUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * CLIENT-SIDE — scroll wheel transfers items between container sections.
 *
 * Scroll up = move to container, scroll down = move to player inventory.
 * Hold Shift to move the full stack instead of one item.
 *
 * Register on MinecraftForge.EVENT_BUS.
 */
@SideOnly(Side.CLIENT)
public class ScrollHandler {

    @SubscribeEvent
    public void onMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (!(event.getGui() instanceof GuiContainer)) {
            return;
        }

        // Creative inventory uses the wheel to scroll its item list — don't hijack it
        if (event.getGui() instanceof net.minecraft.client.gui.inventory.GuiContainerCreative) {
            return;
        }

        // The survival player inventory has no real external container — its only
        // non-player slots are the crafting result + 2x2 crafting grid, which the
        // generic check below mistakes for a container. That let the wheel transfer
        // items while hovering the hotbar (the "items move with the scroll wheel in
        // the inventory hotbar" bug). Bail outright on the player's own screen.
        if (event.getGui() instanceof net.minecraft.client.gui.inventory.GuiInventory) {
            return;
        }

        GuiContainer gui = (GuiContainer) event.getGui();

        // Any other screen with no non-player slots: nowhere to transfer "to
        // container", and scroll-to-player just reshuffles the player's own slots.
        if (!hasNonPlayerSlots(gui)) {
            return;
        }

        int scroll = Mouse.getEventDWheel();
        if (scroll == 0) {
            return;
        }

        // Original IPN: holding Ctrl temporarily disables scroll transfer
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            return;
        }

        Slot slot = gui.getSlotUnderMouse();
        if (slot == null || !slot.getHasStack()) {
            return;
        }

        // scroll > 0 = up = to container, scroll < 0 = down = to player
        boolean toContainer = scroll > 0;
        boolean fullStack = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        ClientScrollTransfer.transfer(gui, slot.slotNumber, toContainer, fullStack);

        event.setCanceled(true);
    }

    /** True when the open container exposes at least one non-player slot section. */
    private static boolean hasNonPlayerSlots(GuiContainer gui) {
        for (Slot slot : gui.inventorySlots.inventorySlots) {
            if (!ContainerUtils.isPlayerSlot(slot)) {
                return true;
            }
        }
        return false;
    }
}
