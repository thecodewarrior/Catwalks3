package catwalks.util;

import catwalks.block.BlockCatwalkBase.Quad;
import catwalks.shade.ccl.raytracer.ExtendedMOP;
import catwalks.shade.ccl.vec.BlockCoord;
import catwalks.shade.ccl.vec.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public class ExtendedFlatHighlightMOP extends ExtendedMOP {

	public Quad quad;
	
	public ExtendedFlatHighlightMOP(Quad quad, Vector3 hit, int side, BlockCoord pos, Object data, double dist) {
		super(hit, side, pos, data, dist);
		this.quad = quad;
	}
	
	public ExtendedFlatHighlightMOP(Quad quad, Entity entity, Vector3 hit, Object data, double dist) {
		super(entity, hit, data, dist);
		this.quad = quad;
	}

	
}
