package catwalks.movement;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class CatwalkEntityProperties implements IExtendedEntityProperties {

	public int jumpTimer;
	public double multiplier;
	public boolean isInList = false;
	public boolean highSpeedLadder = false;
	public double lastStepX;
	public double lastStepY;
	public double lastStepZ;
	public boolean isSlidingDownLadder = false;
	public double lastPosX;
	public double lastPosY;
	public double lastPosZ;
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {}

	@Override
	public void loadNBTData(NBTTagCompound compound) {}

	@Override
	public void init(Entity entity, World world) {
		jumpTimer = 0;
		multiplier = 0;
		lastStepX = entity.posX;
		lastStepY = entity.posY;
		lastStepZ = entity.posZ;
		lastPosX = entity.posX;
		lastPosY = entity.posY;
		lastPosZ = entity.posZ;
	}

}
