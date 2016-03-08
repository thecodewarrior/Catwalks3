package catwalks.util;

import catwalks.block.BlockCatwalkBase.Face;
import catwalks.shade.ccl.raytracer.ExtendedMOP;
import catwalks.shade.ccl.vec.BlockCoord;
import catwalks.shade.ccl.vec.Vector3;
import net.minecraft.entity.Entity;

public class ExtendedFlatHighlightMOP extends ExtendedMOP {

	public Face quad;
	
	public ExtendedFlatHighlightMOP(Face quad, Vector3 hit, int side, BlockCoord pos, Object data, double dist) {
		super(hit, side, pos, data, dist);
		this.quad = quad;
	}
	
	public ExtendedFlatHighlightMOP(Face quad, Entity entity, Vector3 hit, Object data, double dist) {
		super(entity, hit, data, dist);
		this.quad = quad;
	}

	
}
