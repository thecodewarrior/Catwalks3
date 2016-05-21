package catwalks.gui.client;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

public class GuiNodeParticleEmitter extends GuiNodeBase {

	private GuiButton fireButton;

	public GuiNodeParticleEmitter(int id, NBTTagCompound tag) {
		super(id, tag);
		setGuiSize(300, 300);
	}

	@Override
	public void initGui() {
		fireButton = new GuiButton(0, this.width/2, this.height/2, tag.getBoolean("FIRE") ? "no flames" : "FIRE!");
		buttonList.add(fireButton);
		super.initGui();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		tag.setBoolean("FIRE", !tag.getBoolean("FIRE"));
		fireButton.displayString = tag.getBoolean("FIRE") ? "no flames" : "FIRE!";
	}

}
