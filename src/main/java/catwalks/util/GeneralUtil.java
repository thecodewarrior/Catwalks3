package catwalks.util;

import java.util.BitSet;
import java.util.Random;

import catwalks.block.BlockCatwalkBase.Tri;
import catwalks.block.ICatwalkConnect;
import catwalks.shade.ccl.vec.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;

public class GeneralUtil {
	private static final Random RANDOM = new Random();
	
	public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack)
    {
        float f = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f1 = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (stack.stackSize > 0)
        {
            int i = RANDOM.nextInt(21) + 10;

            if (i > stack.stackSize)
            {
                i = stack.stackSize;
            }

            stack.stackSize -= i;
            EntityItem entityitem = new EntityItem(worldIn, x + (double)f, y + (double)f1, z + (double)f2, new ItemStack(stack.getItem(), i, stack.getMetadata()));

            if (stack.hasTagCompound())
            {
                entityitem.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
            }

            float f3 = 0.05F;
            entityitem.motionX = RANDOM.nextGaussian() * (double)f3;
            entityitem.motionY = RANDOM.nextGaussian() * (double)f3 + 0.20000000298023224D;
            entityitem.motionZ = RANDOM.nextGaussian() * (double)f3;
            worldIn.spawnEntityInWorld(entityitem);
        }
    }
	
	public static BitSet getSet(int value, int offset) {
		BitSet bits = new BitSet();
		int index = offset;
		while (value != 0L) {
			if (value % 2L != 0) {
				bits.set(index);
			}
			++index;
			value = value >>> 1;
		}
		return bits;
	}

	public static int getNum(BitSet bits) {
		int value = 0;
		for (int i = 0; i < bits.length(); ++i) {
			value += bits.get(i) ? (1 << i) : 0;
		}
		return value;
	}
	
	public static final float SMALL_NUM = 0.00000001f;
	
	public static Vector3 intersectRayTri( Vector3 start, Vector3 end, Tri tri ) {		
		Vector3 intersect;
	    Vector3    u, v, n;              // triangle vectors
	    Vector3    dir, w0, w;           // ray vectors
	    double     r, a, b;              // params to calc ray-plane intersect

	    // get triangle edge vectors and plane normal
	    u = tri.v2.copy().sub(tri.v1);
	    v = tri.v3.copy().sub(tri.v1);
	    n = u.crossProduct(v);              // cross product
	    if (n.equals( Vector3.zero ))             // triangle is degenerate
	        return null;                   // do not deal with this case

	    dir = end.copy().sub(start);             // ray direction vector
	    start.sub(dir.copy().normalize().multiply(0.25));
	    dir = end.copy().sub(start);             // ray direction vector
	    w0 = start.copy().sub(tri.v1);
	    a = -n.copy().dotProduct(w0);
	    b =  n.copy().dotProduct(dir);
	    if (Math.abs(b) < SMALL_NUM) {     // ray is  parallel to triangle plane
	        if (a == 0)                 // ray lies in triangle plane
	            return null;
	        else
	        	return null;              // ray disjoint from plane
	    }

	    // get intersect point of ray with triangle plane
	    r = a / b;
	    if (r < 0.0)                    // ray goes away from triangle
	        return null;                   // => no intersect
	    if (r > 1.0)                    // ray doesn't reach triangle
	    	return null;                   // => no intersect

	    intersect = start.copy().add(dir.copy().multiply(r));            // intersect point of ray and plane

	    
	    float angles = 0;

        Vector3 v1 = new Vector3(intersect.x - tri.v1.x, intersect.y - tri.v1.y, intersect.z - tri.v1.z).normalize();
        Vector3 v2 = new Vector3(intersect.x - tri.v2.x, intersect.y - tri.v2.y, intersect.z - tri.v2.z).normalize();
        Vector3 v3 = new Vector3(intersect.x - tri.v3.x, intersect.y - tri.v3.y, intersect.z - tri.v3.z).normalize();

        angles += Math.acos(v1.copy().dotProduct(v2));
        angles += Math.acos(v2.copy().dotProduct(v3));
        angles += Math.acos(v3.copy().dotProduct(v1));

        if(Math.abs(angles - 2*Math.PI) > 0.005)
        	return null;
	    
	    return intersect;                       // I is in T
	}
	
	public static void updateSurroundingCatwalkBlocks(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		boolean isCW = false;
		if(state.getBlock() instanceof ICatwalkConnect) {
			isCW = true;
		}
		for (EnumFacing direction : EnumFacing.HORIZONTALS) {
			if(world.getBlockState(pos.offset(direction)).getBlock() instanceof ICatwalkConnect) {
				(  (ICatwalkConnect)world.getBlockState(pos.offset(direction)).getBlock()  ).updateSide(world, pos.offset(direction), direction.getOpposite());
			}
			if(isCW) {
				(  (ICatwalkConnect)state.getBlock()  ).updateSide(world, pos, direction);
			}
		}
	}
	
	public static int getRotation(EnumFacing from, EnumFacing to) {
		if(from.getAxis() == Axis.Y || to.getAxis() == Axis.Y) {
			return 0;
		}
		return to.getHorizontalIndex() - from.getHorizontalIndex();
	}
	
	public static EnumFacing rotateFacing(int rotation, EnumFacing dir) {
		if(dir.getAxis() == Axis.Y) {
			return dir;
		}
		return EnumFacing.HORIZONTALS[Math.abs( (dir.getHorizontalIndex() + rotation )%EnumFacing.HORIZONTALS.length )];
	}
	
	public static EnumFacing derotateFacing(int rotation, EnumFacing dir) {
		if(dir.getAxis() == Axis.Y) {
			return dir;
		}
		return EnumFacing.HORIZONTALS[Math.abs( (dir.getHorizontalIndex() - rotation )%EnumFacing.HORIZONTALS.length )];
	}
}
