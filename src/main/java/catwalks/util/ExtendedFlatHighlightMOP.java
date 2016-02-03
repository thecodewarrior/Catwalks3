package catwalks.util;

import codechicken.lib.raytracer.ExtendedMOP;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import net.minecraft.util.EnumFacing;

public class ExtendedFlatHighlightMOP extends ExtendedMOP {

	public double left, right, top, bottom, sideDistance;
	public EnumFacing side;
	
	public ExtendedFlatHighlightMOP(ExtendedMOP mop) {
		super(new Vector3(mop.hitVec), mop.sideHit.ordinal(), new BlockCoord(mop.getBlockPos()), mop.hitInfo, mop.dist);
	}

}
