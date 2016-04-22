package catwalks.movement;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class CatwalkEntityProperties implements IExtendedEntityProperties {

	public int jumpTimer = 0;
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		// Save data to NBT
		// compound.setInteger("someKey", someField);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		// Load data from NBT
		// someField = compound.getInteger("someKey");
	}

	@Override
	public void init(Entity entity, World world) {
		// anything you want to do when this is initialized
	}

}
