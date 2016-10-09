package catwalks.part.data;

import net.minecraft.util.EnumFacing;

import java.util.EnumMap;

/**
 * Created by TheCodeWarrior
 */
public class CatwalkRenderData implements Comparable<CatwalkRenderData> {
	
	public EnumMap<EnumFacing, CatwalkSideRenderData> sides = new EnumMap<EnumFacing, CatwalkSideRenderData>(EnumFacing.class);
	
	public EnumFacing.Axis bottom;
	public boolean bottomNorth, bottomSouth, bottomEast, bottomWest;
	public boolean bottomNE, bottomNW, bottomSE, bottomSW;
	public EnumCatwalkCornerType corner_ne, corner_nw, corner_se, corner_sw;
	
	public static class CatwalkSideRenderData {
		public EnumCatwalkEndRenderType left, right;
		
		public enum EnumCatwalkEndRenderType {
			END, MERGE, CONNECT, INNER_CORNER, OUTER_CORNER_180, OUTER_CORNER
		}
	}
	
	@Override
	public int compareTo(CatwalkRenderData o) {
		return 0;
	}
	
	public enum EnumCatwalkCornerType {
		CORNER, CORNER_180, INNER_CORNER
	}
}
