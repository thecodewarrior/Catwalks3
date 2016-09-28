package catwalks.part.data;

import net.minecraft.util.EnumFacing;

import java.util.EnumMap;

/**
 * Created by TheCodeWarrior
 */
public class CatwalkRenderData implements Comparable<CatwalkRenderData> {
	
	public EnumMap<EnumFacing, CatwalkSideRenderData> sides = new EnumMap<EnumFacing, CatwalkSideRenderData>(EnumFacing.class);
	
	public boolean bottom;
	public boolean corner_ne, corner_nw, corner_se, corner_sw;
	
	public static class CatwalkSideRenderData {
		public EnumCatwalkEndRenderType left, right;
		
		public enum EnumCatwalkEndRenderType {
			END, MERGE, CORNER, CONNECT
		}
	}
	
	@Override
	public int compareTo(CatwalkRenderData o) {
		return 0;
	}
}
