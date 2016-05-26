package catwalks.gui.widget;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import catwalks.util.GeneralUtil;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.events.ButtonEvent;
import mcjty.lib.gui.widgets.AbstractWidget;

public class QuickSelectorRing<P extends QuickSelectorRing> extends AbstractWidget<P> {

	protected List<ButtonEvent> buttonEvents = null;
	protected List<Slot> slots = null;
	protected GuiContainer guiContainer = null;
	protected int selectedIndex = 0;
	
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
		if(slots == null)
			return;
		
        RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableDepth();
		
		double mouseX = Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth;
	    double mouseY = mc.currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1;
	    
		int SLOTSIZE = 16;
        int xx = x + bounds.x;
        int yy = y + bounds.y;
        
        mouseX -= xx;
        mouseY -= yy;
        
        List<ItemStack> stacks = constructStacks();
        
        double angle = 0;
        double rotPer = 360/stacks.size();
        
		for (ItemStack stack : stacks) {
			double theta = Math.toRadians(angle);

			double cs = Math.cos(theta);
			double sn = Math.sin(theta);
			
			double mouseAngle = Math.toDegrees(Math.atan2(mouseY, mouseX) - Math.atan2(-cs, sn));
			
			int radius = 32;
			
			if(Math.abs(mouseAngle) < rotPer/2) {
				radius = 48;
			}
			
			int rotX = xx + (int)(radius * sn);
			int rotY = yy + (int)(-radius * cs);
			
			angle += rotPer;
			mc.getRenderItem().renderItemAndEffectIntoGUI(this.mc.thePlayer, stack, rotX - SLOTSIZE/2, rotY - SLOTSIZE/2);
		}
		RenderHelper.disableStandardItemLighting();
	}
	
	public List<ItemStack> constructStacks() {
		List<ItemStack> stacks = new ArrayList<>();
        
        for (Slot slot : slots) {
			ItemStack stack = slot.getStack();
			if(stack == null)
				continue;
			boolean duplicate = false;
			for (ItemStack checkStack : stacks) {
				if(checkStack.isItemEqual(stack)) {
					duplicate = true;
					break;
				}
			}
			if(duplicate)
				continue;
			stacks.add(stack);
		}
        return stacks;
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
