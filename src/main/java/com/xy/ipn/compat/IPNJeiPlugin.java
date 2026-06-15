package com.xy.ipn.compat;

import com.xy.ipn.client.ButtonHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.minecraft.client.gui.inventory.GuiContainer;

import java.awt.Rectangle;
import java.util.List;

/**
 * JEI compatibility — reports the screen areas covered by IPN's sort/profile
 * buttons so JEI shifts its ingredient list off them instead of drawing on top.
 *
 * Soft dependency: this class references the JEI API (compile-only). It is only
 * ever loaded by JEI's own annotation scan, so when JEI is absent it stays
 * dormant and never touches the classloader.
 */
@JEIPlugin
public class IPNJeiPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.addAdvancedGuiHandlers(new ButtonAreaHandler());
    }

    /** Exposes IPN's button rectangles to JEI for every container screen. */
    private static final class ButtonAreaHandler implements IAdvancedGuiHandler<GuiContainer> {

        @Override
        public Class<GuiContainer> getGuiContainerClass() {
            return GuiContainer.class;
        }

        @Override
        public List<Rectangle> getGuiExtraAreas(GuiContainer guiContainer) {
            return ButtonHandler.getExclusionAreas();
        }
    }
}
