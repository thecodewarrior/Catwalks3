package catwalks.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import catwalks.raytrace.RayTraceUtil.IRenderableFace;

public class CustomFaceRayTraceResult extends RayTraceResult {
	
	public IRenderableFace quad;
	public BlockPos offset;
	
	public CustomFaceRayTraceResult(Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn, BlockPos offset)
    {
        super(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
        this.offset = offset;
    }

    public CustomFaceRayTraceResult(Vec3d hitVecIn, EnumFacing sideHitIn)
    {
        super(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, BlockPos.ORIGIN);
    }
	
	public CustomFaceRayTraceResult face(IRenderableFace quad) {
		this.quad = quad;
		return this;
	}

	
}
