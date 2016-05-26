package catwalks.gui.nodemanipulator;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;
import java.util.function.BooleanSupplier;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import catwalks.CatwalksMod;
import catwalks.Const;
import catwalks.gui.inventory.SlotWrapper;
import catwalks.gui.widget.QuickSelectorRing;
import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketServerContainerCommand;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.PositionalLayout.PositionalHint;
import mcjty.lib.gui.widgets.ImageLabel;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.ToggleButton;
import mcjty.lib.network.Argument;

public class GuiNodeConfItem extends GenericGuiContainer<GenericTileEntity> {

	private static final ResourceLocation iconLocation = new ResourceLocation(Const.MODID, "textures/gui/nodeConf.png");
	
	private int selectedButton = 0;
	private int lockedSlotIndex = -1;
	private ToggleButton[] selectButtonList = new ToggleButton[5];
	private boolean showQuickSelect = false;
	private QuickSelectorRing ring;
	
	public GuiNodeConfItem(NodeConfItemContainer container) {
		super(CatwalksMod.INSTANCE, NetworkHandler.network, null, container, 0, "");
		xSize = 176;
		ySize = 133;
		
		selectedButton = container.index;
		lockedSlotIndex = container.selectedSlot;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
    public void initGui() {
        super.initGui();

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout());
        for (int i = 0; i < selectButtonList.length; i++) {
        	ToggleButton w = new ToggleButton(mc, this);
        	selectButtonList[i] = w;
        	final int index = i;
            w.setLayoutHint(new PositionalHint(43 + i*18, 12)).setDesiredWidth(19).setDesiredHeight(8);
            w.addButtonEvent((button) -> {
            	setSelected(index);
            });
            toplevel.addChild(w);
		}
        setSelected(selectedButton);
        
        if(lockedSlotIndex != -1) {
        	int lockedX = 7 + 18*lockedSlotIndex;
            int lockedY = 108;
            ImageLabel lockedSlot = new ImageLabel(mc, this);
            lockedSlot.setImage(iconLocation, 176, 0);
            lockedSlot.setBounds(new Rectangle(lockedX, lockedY, 18, 18));
            
            toplevel.addChild(lockedSlot);
        }
        
        this.ring = new QuickSelectorRing(mc, this);
        ring.addSlot(this.inventorySlots.getSlot(0));
        ring.addSlot(this.inventorySlots.getSlot(1));
        ring.addSlot(this.inventorySlots.getSlot(2));
        ring.addSlot(this.inventorySlots.getSlot(3));
        ring.addSlot(this.inventorySlots.getSlot(4));
        ring.setBounds(new Rectangle(0, 0, this.height/2, this.height/2));
        ring.setLayoutHint(new PositionalHint(xSize/2, ySize/2));
        
		ring.setVisible( GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem) );
        toplevel.addChild(ring);
        
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
        
        List<Slot> slots = inventorySlots.inventorySlots;
        
        for (int i = 0; i < slots.size(); i++) {
			slots.set(i, new SlotWrapperCanHover(slots.get(i), () -> !ring.isVisible()));
		}
    }
	
	private static class SlotWrapperCanHover extends SlotWrapper {

		BooleanSupplier supplier;
		
		public SlotWrapperCanHover(Slot s, BooleanSupplier canHover) {
			super(s);
			supplier = canHover;
		}
		
		@Override
		public boolean canBeHovered() {
			return super.canBeHovered() && supplier.getAsBoolean();
		}
		
		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			return super.canTakeStack(playerIn) && supplier.getAsBoolean();
		}
		
		@Override
		public boolean isItemValid(ItemStack stack) {
			return super.isItemValid(stack) && supplier.getAsBoolean();
		}
		
	}
	
	private void setSelected(int index) {
		for (ToggleButton other : selectButtonList) {
			other.setPressed(false);
		}
    	selectButtonList[index].setPressed(true);
    	
		if(index != selectedButton) {
			NetworkHandler.network.sendToServer(new PacketServerContainerCommand(Const.COMMAND_OPTIONS, new Argument("sel", index)));
		}
		
		selectedButton = index;
	}
	
	private void updateRingMouseOrKey() {
		if(ring.isVisible()) {
			ring.setVisible(GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && GameSettings.isKeyDown(mc.gameSettings.keyBindUseItem));
			if(!ring.isVisible() && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) { // only fire when the mouse is released
				int slot = ring.getSelectedSlot();
				if(slot >= 0) {
					setSelected(slot);
					mc.displayGuiScreen((GuiScreen)null);

		            if (mc.currentScreen == null)
		            {
		                mc.setIngameFocus();
		            }
				}
			}
		}
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		updateRingMouseOrKey();
		super.handleMouseInput();
	}
	
	public void handleKeyboardInput() throws IOException {
		updateRingMouseOrKey();
		super.handleKeyboardInput();
    }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ring.drawForeground(guiLeft, guiTop);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawWindow();
	}

}
