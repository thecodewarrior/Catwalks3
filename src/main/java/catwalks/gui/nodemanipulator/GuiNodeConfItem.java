package catwalks.gui.nodemanipulator;

import java.awt.Rectangle;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import catwalks.CatwalksMod;
import catwalks.Const;
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
	
	private int initialIndex = 0;
	private int selectedSlot = -1;
	
	public GuiNodeConfItem(NodeConfItemContainer container) {
		super(CatwalksMod.INSTANCE, NetworkHandler.network, null, container, 0, "");
		
		initialIndex = container.index;
		selectedSlot = container.selectedSlot;
		xSize = 176;
		ySize = 133;
	}
	
	@Override
    public void initGui() {
        super.initGui();

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout());
        ToggleButton[] list = new ToggleButton[5];
        for (int i = 0; i < list.length; i++) {
        	ToggleButton w = new ToggleButton(mc, this);
        	list[i] = w;
        	final int index = i;
            w.setLayoutHint(new PositionalHint(43 + i*18, 12)).setDesiredWidth(19).setDesiredHeight(8);
            w.addButtonEvent((button) -> {
            	for (ToggleButton other : list) {
					other.setPressed(false);
				}
            	((ToggleButton)button).setPressed(true);
            	setSelected(index);
            });
            toplevel.addChild(w);
		}
        list[initialIndex].setPressed(true);
        
        if(selectedSlot != -1) {
        	int lockedX = 7 + 18*selectedSlot;
            int lockedY = 108;
            ImageLabel<ImageLabel> lockedSlot = new ImageLabel<ImageLabel>(mc, this);
            lockedSlot.setImage(iconLocation, 176, 0);
            lockedSlot.setBounds(new Rectangle(lockedX, lockedY, 18, 18));
            
            toplevel.addChild(lockedSlot);
        }
        
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }
	
	private void setSelected(int index) {
		NetworkHandler.network.sendToServer(new PacketServerContainerCommand(Const.COMMAND_OPTIONS, new Argument("sel", index)));
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if(window != null)
			drawWindow();
	}

}
