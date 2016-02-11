package catwalks.shade.ccl.raytracer;

import catwalks.shade.ccl.vec.Cuboid6;

public class IndexedCuboid6 extends Cuboid6
{
    public Object data;
    
    public IndexedCuboid6(Object data, Cuboid6 cuboid)
    {
        super(cuboid);
        this.data = data;
    }
    
    @Override
    public Cuboid6 copy() {
    	return new IndexedCuboid6(data, this);
    }
}