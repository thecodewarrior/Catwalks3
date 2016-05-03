package catwalks.movement.capability;

import catwalks.Const;
import catwalks.movement.capability.ICWEntityData.CWEntityData;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CWEntityDataProvider implements ICapabilityProvider {

	CWEntityData entityData;
	
	public CWEntityDataProvider() {
		entityData = new CWEntityData();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == Const.CW_ENTITY_DATA_CAPABILITY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return hasCapability(capability, facing) ? (T) entityData : null;
	}

}
