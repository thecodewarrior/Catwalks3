package catwalks.util;

import java.util.BitSet;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
}
