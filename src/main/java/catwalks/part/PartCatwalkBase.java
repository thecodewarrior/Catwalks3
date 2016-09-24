package catwalks.part;

import mcmultipart.multipart.INormallyOccludingPart;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.PartSlot;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by TheCodeWarrior
 */
public class PartCatwalkBase extends Multipart implements ISlottedPart, INormallyOccludingPart {
	
	
	
	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		
	}
	
	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return null;
	}
}
