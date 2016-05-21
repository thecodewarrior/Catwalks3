package catwalks.gui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketNodeSettingsUpdate;
import catwalks.node.EntityNodeBase;

public abstract class GuiNodeBase extends GuiScreen {

	protected NBTTagCompound tag;
	protected int id;
	
	public GuiNodeBase(int id, NBTTagCompound tag) {
		this.tag = tag;
		this.id = id;
	}
	
    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
    	return false;
    }
    
    @Override
    public void onGuiClosed() {
    	NetworkHandler.network.sendToServer(new PacketNodeSettingsUpdate(id, tag));
    	Entity plainEntity = Minecraft.getMinecraft().theWorld.getEntityByID(id);
    	if(!(plainEntity instanceof EntityNodeBase))
    		return;
    	EntityNodeBase entity = (EntityNodeBase) plainEntity;
    	entity.getNode().updateSettings(tag);
    }
}