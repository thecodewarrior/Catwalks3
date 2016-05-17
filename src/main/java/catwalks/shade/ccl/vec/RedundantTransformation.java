package catwalks.shade.ccl.vec;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RedundantTransformation extends Transformation
{
    @Override
    public void apply(Vector3 vec){}

    @Override
    public Vec3d apply(Vec3d vec){return vec;}
    
    @Override
    public void apply(Matrix4 mat){}
    
    @Override
    public void applyN(Vector3 normal){}
    
    @Override
    public Transformation at(Vector3 point)
    {
        return this;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void glApply(){}
    
    @Override
    public Transformation inverse()
    {
        return this;
    }
    
    @Override
    public Transformation merge(Transformation next) {
        return next;
    }
    
    @Override
    public boolean isRedundant() {
        return true;
    }
    
    @Override
    public String toString()
    {
        return "Nothing()";
    }
}
