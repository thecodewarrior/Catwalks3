package catwalks.shade.ccl.vec;

import net.minecraft.util.math.Vec3d;

public class SwapYZ extends VariableTransformation
{
    public SwapYZ()
    {
        super(new Matrix4(
                1, 0, 0, 0, 
                0, 0, 1, 0,
                0, 1, 0, 0,
                0, 0, 0, 1));
    }
    
    @Override
    public void apply(Vector3 vec)
    {
        double vz = vec.z;
        vec.z = vec.y;
        vec.y = vz;
    }
    
    @Override
	public Vec3d apply(Vec3d vec) {
		return new Vec3d(vec.xCoord, vec.zCoord, vec.yCoord);
	}
    
    @Override
    public Transformation inverse()
    {
        return this;
    }
}
