package catwalks.util;


import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class AABB extends AxisAlignedBB {

	public AABB(AxisAlignedBB otherBB) {
		super(otherBB.minX, otherBB.minY, otherBB.minZ, otherBB.maxX, otherBB.maxY, otherBB.maxZ);
	}
	
	public AABB(double x1, double y1, double z1, double x2, double y2, double z2)
    {
		super(x1, y1, z1, x2, y2, z2);
    }

    public AABB(BlockPos pos)
    {
        super(pos);
    }

    public AABB(BlockPos pos1, BlockPos pos2)
    {
        super(pos1, pos2);
    }
    
    public AABB(Vec3d vec1, Vec3d vec2) {
    	super(vec1.xCoord, vec1.yCoord, vec1.zCoord, vec2.xCoord, vec2.yCoord, vec2.zCoord);
    }
	
	/**
	 * Offset side of an AxisAlignedBB
	 * @param side Side to change
	 * @param amount amount to offset, positive expands the AABB
	 */
	public AABB expand(EnumFacing side, double amount) {
		
		double minX = this.minX;
		double minY = this.minY;
		double minZ = this.minZ;
		
		double maxX = this.maxX;
		double maxY = this.maxY;
		double maxZ = this.maxZ;

		
		switch (side) {
		case UP:
			maxY += amount;
		case DOWN:
			minY -= amount;
		case NORTH:
			minZ -= amount;
		case SOUTH:
			maxZ += amount;
		case EAST:
			minX -= amount;
		case WEST:
			maxX += amount;
		default:
		}
		
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public AABB rotate(int rotation) {
		
		double minX = this.minX;
		double minY = this.minY;
		double minZ = this.minZ;
		
		double maxX = this.maxX;
		double maxY = this.maxY;
		double maxZ = this.maxZ;
		
		Vec3d center = new Vec3d( (minX+maxX)/2.0, (minY+maxY)/2.0, (minZ+maxZ)/2.0 );
		
		// min
		Vec3d vec = new Vec3d( minX, minY, minZ ).subtract(center);
		vec = GeneralUtil.rotateVector(rotation, vec);
		vec = vec.add(center);
		
		minX = vec.xCoord;
		minY = vec.yCoord;
		minZ = vec.zCoord;
		
		// max
		vec = new Vec3d( maxX, maxY, maxZ ).subtract(center);
		vec = GeneralUtil.rotateVector(rotation, vec);
		vec = vec.add(center);
		
		maxX = vec.xCoord;
		maxY = vec.yCoord;
		maxZ = vec.zCoord;
		
		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	//#########################################################################
	//############### Reimplementing with correct return types ################
	//#########################################################################
	
	@Override
    public AABB setMaxY(double y2)
    {
        return new AABB(this.minX, this.minY, this.minZ, this.maxX, y2, this.maxZ);
    }

    /**
     * Adds a coordinate to the bounding box, extending it if the point lies outside the current ranges.
     */
    @Override
    public AABB addCoord(double x, double y, double z)
    {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D)
        {
            d0 += x;
        }
        else if (x > 0.0D)
        {
            d3 += x;
        }

        if (y < 0.0D)
        {
            d1 += y;
        }
        else if (y > 0.0D)
        {
            d4 += y;
        }

        if (z < 0.0D)
        {
            d2 += z;
        }
        else if (z > 0.0D)
        {
            d5 += z;
        }

        return new AABB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Creates a new bounding box that has been expanded. If negative values are used, it will shrink.
     */
    @Override
    public AABB expand(double x, double y, double z)
    {
        double d0 = this.minX - x;
        double d1 = this.minY - y;
        double d2 = this.minZ - z;
        double d3 = this.maxX + x;
        double d4 = this.maxY + y;
        double d5 = this.maxZ + z;
        return new AABB(d0, d1, d2, d3, d4, d5);
    }

    @Override
    public AABB expandXyz(double value)
    {
        return this.expand(value, value, value);
    }

    @Override
    public AABB union(AxisAlignedBB other)
    {
        double d0 = Math.min(this.minX, other.minX);
        double d1 = Math.min(this.minY, other.minY);
        double d2 = Math.min(this.minZ, other.minZ);
        double d3 = Math.max(this.maxX, other.maxX);
        double d4 = Math.max(this.maxY, other.maxY);
        double d5 = Math.max(this.maxZ, other.maxZ);
        return new AABB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Offsets the current bounding box by the specified amount.
     */
    @Override
    public AABB offset(double x, double y, double z)
    {
        return new AABB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    @Override
    public AABB offset(BlockPos pos)
    {
        return new AABB(this.minX + (double)pos.getX(), this.minY + (double)pos.getY(), this.minZ + (double)pos.getZ(), this.maxX + (double)pos.getX(), this.maxY + (double)pos.getY(), this.maxZ + (double)pos.getZ());
    }
    
    @Override
    public AABB contract(double value)
    {
        return this.expandXyz(-value);
    }
}
