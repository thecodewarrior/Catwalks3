package catwalks.block.extended;

import net.minecraft.util.EnumFacing;

public enum EnumCubeEdge {

	NORTHEAST(EnumFacing.NORTH, EnumFacing.EAST),
	NORTHWEST(EnumFacing.NORTH, EnumFacing.WEST),
	SOUTHEAST(EnumFacing.SOUTH, EnumFacing.EAST),
	SOUTHWEST(EnumFacing.SOUTH, EnumFacing.WEST),
	NORTHUP  (EnumFacing.NORTH, EnumFacing.UP  ),
	SOUTHUP  (EnumFacing.SOUTH, EnumFacing.UP  ),
	EASTUP   (EnumFacing.EAST,  EnumFacing.UP  ),
	WESTUP   (EnumFacing.WEST,  EnumFacing.UP  ),
	NORTHDOWN(EnumFacing.NORTH, EnumFacing.DOWN),
	SOUTHDOWN(EnumFacing.SOUTH, EnumFacing.DOWN),
	EASTDOWN (EnumFacing.EAST,  EnumFacing.DOWN),
	WESTDOWN (EnumFacing.WEST,  EnumFacing.DOWN);
	
	private EnumFacing dir1, dir2;
	public EnumFacing getDir1() { return dir1; }
	public EnumFacing getDir2() { return dir2; }
	
	EnumCubeEdge(EnumFacing dir1, EnumFacing dir2) {
		this.dir1 = dir1; this.dir2 = dir2;
	}
}
