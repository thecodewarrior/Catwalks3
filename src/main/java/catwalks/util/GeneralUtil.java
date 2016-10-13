package catwalks.util;

import catwalks.Const;
import com.google.common.collect.ImmutableList;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.MultipartHelper;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

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

	public static Vec3d getDesiredMoveVector(EntityLivingBase entity) {
		
		float f = MathHelper.cos(-entity.rotationYawHead * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-entity.rotationYawHead * 0.017453292F - (float)Math.PI);
        Vec3d look = new Vec3d(-f1, 0, -f);
		
		Vec3d forwardVec = new Vec3d(look.xCoord, 0, look.zCoord).normalize().scale(entity.moveForward);
		Vec3d straifVec = new Vec3d(look.zCoord, 0, -look.xCoord).normalize().scale(entity.moveStrafing);
		
		return forwardVec.add(straifVec).normalize();
	}
	
	public static int getRotation(EnumFacing from, EnumFacing to) {
		if(from == null || to == null)
			return 0;
		if(from.getAxis() == Axis.Y || to.getAxis() == Axis.Y) {
			return 0;
		}
		return to.getHorizontalIndex() - from.getHorizontalIndex();
	}
	
	public static Vec3d rotateVectorCenter(int rotation, Vec3d vec) {
		return rotateVector(rotation, vec.add(Const.VEC_ANTICENTER)).add(Const.VEC_CENTER);
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
}
