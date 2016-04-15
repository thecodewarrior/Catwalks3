package catwalks.block.extended;

import net.minecraft.util.EnumFacing;

public class CubeEdge {

	public EnumFacing dir1;
	public EnumFacing dir2;
	
	public CubeEdge(EnumFacing a, EnumFacing b) {
		if(a.getAxis() == b.getAxis())
			throw new IllegalArgumentException("Both CubeEdge aruments can't have the same axis");
		dir1 = a;
		dir2 = b;
	}
}
