package catwalks.gui.widget;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;

import mcjty.lib.gui.events.ButtonEvent;
import mcjty.lib.gui.widgets.AbstractWidget;

public class QuickSelectorRing<P extends QuickSelectorRing> extends AbstractWidget<P> {

	protected List<ButtonEvent> buttonEvents = null;
	protected List<Slot> slots = null;
	protected GuiContainer guiContainer = null;
	protected int slotID = -1;
	
	protected double initMouseX;
	protected double initMouseY;
	
	public QuickSelectorRing(Minecraft mc, GuiContainer gui) {
		super(mc, gui);
		guiContainer = gui;
		initMouseX = Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth;
	    initMouseY = mc.currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1;
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
	
	public void drawForeground(int x, int y) {
		if(slots == null || !visible)
			return;
		
        RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableDepth();
		
		double mouseX = Mouse.getEventX() * mc.currentScreen.width / mc.displayWidth;
	    double mouseY = mc.currentScreen.height - Mouse.getEventY() * mc.currentScreen.height / mc.displayHeight - 1;
	    
	    if(
	    	mouseX == initMouseX && Double.isFinite(initMouseX) &&
	    	mouseY == initMouseY && Double.isFinite(initMouseY)
	    	) { // mouse position isn't correct until the mouse moves, this checks for that
	    	mouseX = x + bounds.x; // cancel out with the -= later
	    	mouseY = y + bounds.y;
	    } else { // the mouse moved
	    	initMouseX = Double.POSITIVE_INFINITY;
	    	initMouseY = Double.POSITIVE_INFINITY;
	    }
	    
		int SLOTSIZE = 16;
        int xx = bounds.x;
        int yy = bounds.y;
        
        mouseX -= x + bounds.x;
        mouseY -= y + bounds.y;
        
        List<ItemStack> stacks = new ArrayList<>();
        List<Integer> slotIDs = new ArrayList<>();
        
        for (int i = 0; i < slots.size(); i++) {
        	Slot slot = slots.get(i);
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
			slotIDs.add(i);
		}
        
        if(stacks.size() == 0)
        	return;
        
        double angle = 0;
        double rotPer = 360/stacks.size();
        
        boolean isFarEnough = mouseX*mouseX + mouseY*mouseY > 16*16;
        slotID = -1;
        
		mc.getRenderItem().zLevel += 110;
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack stack = stacks.get(i);
			double theta = Math.toRadians(angle);

			double cs = Math.cos(theta);
			double sn = Math.sin(theta);
			
			int radius = 32;
			
			boolean showName = false;
			if(isFarEnough) {
				double mouseAngle = Math.toDegrees(Math.atan2(mouseY, mouseX) - Math.atan2(-cs, sn));
				
				if(Math.abs(mouseAngle) < rotPer/2) {
					radius = 48;
					slotID = slotIDs.get(i);
					showName = true;
				}
			}
			
			int rotX = xx + (int)(radius * sn);
			int rotY = yy + (int)(-radius * cs);
			
			angle += rotPer;
			mc.getRenderItem().renderItemAndEffectIntoGUI(this.mc.thePlayer, stack, rotX - SLOTSIZE/2, rotY - SLOTSIZE/2);
			if(showName) {
				String name = I18n.format(stack.getUnlocalizedName());
				int centering = xx - (mc.fontRendererObj.getStringWidth(name)/2);
				mc.fontRendererObj.drawString(name, centering, -9, 0xFFFFFF);
			}
		}
		mc.getRenderItem().zLevel -= 110;
		RenderHelper.disableStandardItemLighting();
	}
	
	public int getSelectedSlot() {
		return slotID;
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
