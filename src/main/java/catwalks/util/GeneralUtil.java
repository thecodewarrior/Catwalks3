package catwalks.util;

import java.util.*;
import java.util.function.Predicate;

import catwalks.part.PartCatwalk;
import com.google.common.collect.ImmutableList;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
import net.minecraftforge.common.property.IExtendedBlockState;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import catwalks.Const;
import catwalks.block.ICatwalkConnect;
import catwalks.block.extended.CubeEdge;
import catwalks.block.extended.ITileStateProvider;
import catwalks.shade.ccl.vec.Cuboid6;
import catwalks.shade.ccl.vec.Vector3;

public class GeneralUtil {
	private static final Random RANDOM = new Random();
	
	public static <T extends IMultipart> List<T> getParts(Class<T> clazz, IBlockAccess world, BlockPos pos, boolean exact) {
		IMultipartContainer container = MultipartHelper.getPartContainer(world, pos);
		if (container != null) {
			List<T> list = new ArrayList<T>();
			Collection<? extends IMultipart> parts = container.getParts();
			for(IMultipart part : parts) {
				if(exact) {
					if(clazz == part.getClass()) {
						list.add( (T) part );
					}
				} else {
					if(clazz.isAssignableFrom(part.getClass())) {
						list.add( (T) part );
					}
				}
			}
			return list;
		}
		return ImmutableList.of();
	}
	
	public static <T extends IMultipart> T getPart(Class<T> clazz, IBlockAccess world, BlockPos pos, boolean exact) {
		List<T> list = getParts(clazz, world, pos, exact);
		if(list.size() == 0)
			return null;
		return list.get(0);
	}
	
	public static <T extends IMultipart> List<T> getParts(Class<T> clazz, IBlockAccess world, BlockPos pos) {
		return getParts(clazz, world, pos, false);
	}
	
	public static <T extends IMultipart> T getPart(Class<T> clazz, IBlockAccess world, BlockPos pos) {
		return getPart(clazz, world, pos, false);
	}
	
	public static boolean isHolding(EntityPlayer player, Predicate<ItemStack> test) {
		if(player.getHeldItemMainhand() != null && test.test(player.getHeldItemMainhand()))
			return true;
		if(player.getHeldItemOffhand() != null && test.test(player.getHeldItemOffhand()))
			return true;
		return false;
	}
	
	public static ItemStack getHeld(EntityPlayer player, Predicate<ItemStack> test) {
		if(player.getHeldItemMainhand() != null && test.test(player.getHeldItemMainhand()))
			return player.getHeldItemMainhand();
		if(player.getHeldItemOffhand() != null && test.test(player.getHeldItemOffhand()))
			return player.getHeldItemOffhand();
		return null;
	}
	
	public static Vec3d snapToGrid(Vec3d in, double gridSize) {
		return new Vec3d(
				snapToNearestMultiple(in.xCoord, gridSize),
				snapToNearestMultiple(in.yCoord, gridSize),
				snapToNearestMultiple(in.zCoord, gridSize)
			);
	}
	
	public static double snapToNearestMultiple(double in, double multiple) {
		return multiple*(Math.round(in/multiple));
	}
	
	public static IExtendedBlockState getTileState(IBlockAccess worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		
		if(!( state.getBlock() instanceof ITileStateProvider )) {
			if(worldIn instanceof World)
				Logs.error("%s is not a ITileStateProvider!!! [ located at %s ]", state.getBlock().getClass().getName(), getWorldPosLogInfo((World)worldIn, pos));
			else
				Logs.error("%s is not a ITileStateProvider!!! [ located at %d %d %d ]", state.getBlock().getClass().getName(), pos.getX(), pos.getY(), pos.getZ());
			return null;
		}
		
		return ( (ITileStateProvider)state.getBlock() ).getTileState(state, worldIn, pos);
	}

	public static void markForUpdate(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 8);
	}
	
	public static String getWorldPosLogInfo(World world, BlockPos pos) {
		return String.format("(%d, %d, %d) in dim %s (%d)", pos.getX(), pos.getY(), pos.getZ(), world.provider.getDimensionType().getName(), world.provider.getDimension());
	}
	
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
	
	// private so it doesn't conflict with the method when autocompleting.
	private static final double APPROX_EQ_ACC = 1/10_000f;
	
	public static boolean approxEq(double a, double b) {
		return
				a <= b+APPROX_EQ_ACC &&
				a >= b-APPROX_EQ_ACC;
	}
	
	public static AxisAlignedBB getAABB(Vec3d point1, Vec3d point2) {
		return new AxisAlignedBB(
				Math.min(point1.xCoord, point2.xCoord),
				Math.min(point1.yCoord, point2.yCoord),
				Math.min(point1.zCoord, point2.zCoord),
				
				Math.max(point1.xCoord, point2.xCoord),
				Math.max(point1.yCoord, point2.yCoord),
				Math.max(point1.zCoord, point2.zCoord)
			);
	}
	
	public static double getAABBSide(AxisAlignedBB aabb, EnumFacing side) {
		switch (side) {
		case UP:
			return aabb.maxY;
		case DOWN:
			return aabb.minY;
		case NORTH:
			return aabb.minZ;
		case SOUTH:
			return aabb.maxZ;
		case WEST:
			return aabb.minX;
		case EAST:
			return aabb.maxX;
		default:
			return 0;
		}
	}
	
	public static Vec3d simulateEntityMove(Entity entity, Vec3d movement) {
		
		double x = movement.xCoord, y = movement.yCoord, z = movement.zCoord;
		
		List<AxisAlignedBB> collisionBoxes = entity.worldObj.getCollisionBoxes(entity, entity.getEntityBoundingBox().addCoord(x, y, z));
        AxisAlignedBB entityBox = entity.getEntityBoundingBox();
        int i = 0;

        for (int j = collisionBoxes.size(); i < j; ++i)
        {
            y = ((AxisAlignedBB)collisionBoxes.get(i)).calculateYOffset(entityBox, y);
        }

        entityBox = entityBox.offset(0.0D, y, 0.0D);
        int j4 = 0;

        for (int k = collisionBoxes.size(); j4 < k; ++j4)
        {
            x = ((AxisAlignedBB)collisionBoxes.get(j4)).calculateXOffset(entityBox, x);
        }

        entityBox = entityBox.offset(x, 0.0D, 0.0D);
        j4 = 0;

        for (int k4 = collisionBoxes.size(); j4 < k4; ++j4)
        {
            z = ((AxisAlignedBB)collisionBoxes.get(j4)).calculateZOffset(entityBox, z);
        }

        entityBox = entityBox.offset(0.0D, 0.0D, z);
        
        return new Vec3d(x, y, z);
	}
	
	public static boolean checkEdge(EnumFacing a, EnumFacing b, CubeEdge edge) {
		if(a == edge.dir1 && b == edge.dir2)
			return true;
		if(a == edge.dir2 && b == edge.dir1)
			return true;
		return false;
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
	

	public static Vec3d getDesiredMoveVector(EntityLivingBase entity) {
		
		float f = MathHelper.cos(-entity.rotationYawHead * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-entity.rotationYawHead * 0.017453292F - (float)Math.PI);
        Vec3d look = new Vec3d(-f1, 0, -f);
		
		Vec3d forwardVec = new Vec3d(look.xCoord, 0, look.zCoord).normalize().scale(entity.moveForward);
		Vec3d straifVec = new Vec3d(look.zCoord, 0, -look.xCoord).normalize().scale(entity.moveStrafing);
		
		return forwardVec.add(straifVec).normalize();
	}
	
	public static final float SMALL_NUM = 0.00000001f;
	
	public static Vector3 intersectRayTri( Vector3 start, Vector3 end, Vector3 p1, Vector3 p2, Vector3 p3) {
		Vector3 intersect;
	    Vector3    u, v, n;              // triangle vectors
	    Vector3    dir, w0, w;           // ray vectors
	    double     r, a, b;              // params to calc ray-plane intersect

	    // get triangle edge vectors and plane normal
	    u = p2.copy().sub(p1);
	    v = p3.copy().sub(p1);
	    n = u.crossProduct(v);              // cross product
	    if (n.equals( Vector3.zero ))             // triangle is degenerate
	        return null;                   // do not deal with this case

	    dir = end.copy().sub(start);             // ray direction vector
	    start.sub(dir.copy().normalize().multiply(0.25));
	    dir = end.copy().sub(start);             // ray direction vector
	    w0 = start.copy();
	    w0.sub(p1);
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

        Vector3 v1 = new Vector3(intersect.x - p1.x, intersect.y - p1.y, intersect.z - p1.z).normalize();
        Vector3 v2 = new Vector3(intersect.x - p2.x, intersect.y - p2.y, intersect.z - p2.z).normalize();
        Vector3 v3 = new Vector3(intersect.x - p3.x, intersect.y - p3.y, intersect.z - p3.z).normalize();

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
		for (EnumFacing direction : EnumFacing.VALUES) {
			if(world.getBlockState(pos.offset(direction)).getBlock() instanceof ICatwalkConnect) {
				(  (ICatwalkConnect)world.getBlockState(pos.offset(direction)).getBlock()  ).updateSide(world, pos.offset(direction), direction.getOpposite());
			}
			if(isCW) {
				(  (ICatwalkConnect)state.getBlock()  ).updateSide(world, pos, direction);
			}
		}
	}
	
	public static int getRotation(EnumFacing from, EnumFacing to) {
		if(from == null || to == null)
			return 0;
		if(from.getAxis() == Axis.Y || to.getAxis() == Axis.Y) {
			return 0;
		}
		return to.getHorizontalIndex() - from.getHorizontalIndex();
	}
	
	public static EnumFacing rotateFacing(int rotation, EnumFacing dir) {
		if(dir == null)
			return null;
		if(dir.getAxis() == Axis.Y) {
			return dir;
		}
		int i = (dir.getHorizontalIndex() + rotation ) % EnumFacing.HORIZONTALS.length;
		if( i < 0 )
			i += EnumFacing.HORIZONTALS.length;
		return EnumFacing.HORIZONTALS[i];
	}
	
	public static EnumFacing derotateFacing(int rotation, EnumFacing dir) {
		return rotateFacing(-rotation, dir);
	}
	
	public static Vec3d rotateVectorCenter(int rotation, Vec3d vec) {
		return rotateVector(rotation, vec.add(Const.VEC_ANTICENTER)).add(Const.VEC_CENTER);
	}
	
	public static void rotateVectorCenter(int rotation, Vector3 vec) {
		vec.add(Vector3.anticenter);
		rotateVector(rotation, vec);
		vec.add(Vector3.center);
	}
	
	public static Vec3d rotateVector(int rotation, Vec3d vec) {
		Vec3d out;
		int i = rotation % EnumFacing.HORIZONTALS.length;
		if(i < 0)
			i = 4+i;
		switch (i) {
		case 0:
			out = new Vec3d(vec.xCoord, vec.yCoord, vec.zCoord);
			break;
		case 1:
			out = new Vec3d(-vec.zCoord, vec.yCoord, vec.xCoord);
			break;
		case 2:
			out = new Vec3d(-vec.xCoord, vec.yCoord, -vec.zCoord);
			break;
		case 3:
			out = new Vec3d(vec.zCoord, vec.yCoord, -vec.xCoord);
			break;
		default:
			out = vec;
			break;
		}
		
		return out;
	}
	
	public static void rotateVector(int rotation, Vector3 vec) {
		int i = rotation % EnumFacing.HORIZONTALS.length;
		if(i < 0)
			i = 4+i;
		double tmp = 0;
		switch (i) {
		case 0:
			break;
		case 1:
			tmp = vec.x;
			vec.x = -vec.z;
			vec.z = tmp;
			break;
		case 2:
			vec.x = -vec.x;
			vec.z = -vec.z;
			break;
		case 3:
			tmp = vec.x;
			vec.x = vec.z;
			vec.z = -tmp;
			break;
		default:
			break;
		}
	}
	
	public static void rotateCuboid(int rotation, Cuboid6 cub) {
		int i = rotation % EnumFacing.HORIZONTALS.length;
		
		rotateVector(rotation, cub.min);
		rotateVector(rotation, cub.max);
		cub.resolve();
	}
	
	public static void rotateCuboidCenter(int rotation, Cuboid6 cub) {
		int i = rotation % EnumFacing.HORIZONTALS.length;
		
		rotateVectorCenter(rotation, cub.min);
		rotateVectorCenter(rotation, cub.max);
		cub.resolve();
	}

}
