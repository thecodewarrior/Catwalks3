package catwalks.util;

import catwalks.block.BlockCatwalkBase.Face;
import catwalks.shade.ccl.raytracer.ExtendedMOP;
import catwalks.shade.ccl.vec.BlockCoord;
import catwalks.shade.ccl.vec.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class CustomFaceRayTraceResult extends RayTraceResult {
	
	public Face quad;
	
	public CustomFaceRayTraceResult(Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn)
    {
        super(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
    }

    public CustomFaceRayTraceResult(Vec3d hitVecIn, EnumFacing sideHitIn)
    {
        super(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, BlockPos.ORIGIN);
    }
	
	public CustomFaceRayTraceResult face(Face quad) {
		this.quad = quad;
		return this;
	}

	
}
