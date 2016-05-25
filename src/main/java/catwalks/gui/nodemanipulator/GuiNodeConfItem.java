package catwalks.gui.nodemanipulator;

import java.awt.Rectangle;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import catwalks.CatwalksMod;
import catwalks.Const;
import catwalks.network.NetworkHandler;
import catwalks.util.Logs;
import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.layout.PositionalLayout.PositionalHint;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.ToggleButton;

public class GuiNodeConfItem extends GenericGuiContainer<GenericTileEntity> {

	private static final ResourceLocation iconLocation = new ResourceLocation(Const.MODID, "textures/gui/nodeConf.png");
	
	public GuiNodeConfItem(Container container) {
		super(CatwalksMod.INSTANCE, NetworkHandler.network, null, container, 0, "");
		
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
            w.setLayoutHint(new PositionalHint(43 + i*18, 12)).setDesiredWidth(19).setDesiredHeight(8);
            w.addButtonEvent((button) -> {
            	for (ToggleButton other : list) {
					other.setPressed(false);
				}
            	((ToggleButton)button).setPressed(true);
            });
            toplevel.addChild(w);
		}
        
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if(window != null)
			drawWindow();
	}

}
