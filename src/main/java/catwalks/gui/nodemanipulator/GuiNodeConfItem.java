package catwalks.gui.nodemanipulator;

import java.awt.Rectangle;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import catwalks.CatwalksMod;
import catwalks.Const;
import catwalks.network.NetworkHandler;
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
        
        ToggleButton w = new ToggleButton(mc, this);
        w.setLayoutHint(new PositionalHint(43, 12)).setDesiredWidth(19).setDesiredHeight(8);
        
        toplevel.addChild(w);
        
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }


	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		if(window != null)
			drawWindow();
	}

}
