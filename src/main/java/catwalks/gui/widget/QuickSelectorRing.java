package catwalks.gui.widget;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;

import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ButtonEvent;
import mcjty.lib.gui.widgets.AbstractWidget;

public class QuickSelectorRing<P extends QuickSelectorRing> extends AbstractWidget<P> {

	protected List<ButtonEvent> buttonEvents = null;
	protected List<Slot> slots = null;
	protected GuiContainer guiContainer = null;
	
	public QuickSelectorRing(Minecraft mc, GuiContainer gui) {
		super(mc, gui);
		guiContainer = gui;
	}
	
	@Override
	public void mouseMove(int x, int y) {
		if(!visible)
			return;
		super.mouseMove(x, y);
	}
	
	public void addSlot(Slot slot) {
		if(slots == null)
			slots = new ArrayList<>();
		slots.add(slot);
	}
	
	@Override
	public void draw(Window window, int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableDepth();

		int SLOTSIZE = 16;
        int xx = x + bounds.x - SLOTSIZE/2;
        int yy = y + bounds.y - SLOTSIZE/2;
        int radius = 32;
        
		for (Slot slot : slots) {
			mc.getRenderItem().renderItemAndEffectIntoGUI(this.mc.thePlayer, slot.getStack(), xx, yy);
//			mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, slot.getStack(), i, j, s);
		}
		RenderHelper.disableStandardItemLighting();
	}
	
	public QuickSelectorRing addButtonEvent(ButtonEvent event) {
        if (buttonEvents == null) {
            buttonEvents = new ArrayList<ButtonEvent>();
        }
        buttonEvents.add(event);
        return this;
    }

    public void removeButtonEvent(ButtonEvent event) {
        if (buttonEvents != null) {
            buttonEvents.remove(event);
        }
    }

    private void fireButtonEvents() {
        if (buttonEvents != null) {
            for (ButtonEvent event : buttonEvents) {
                event.buttonClicked(this);
            }
        }
    }
}
