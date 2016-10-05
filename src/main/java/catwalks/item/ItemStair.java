package catwalks.item;

import catwalks.part.PartStair;
import mcmultipart.multipart.IMultipart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by TheCodeWarrior
 */
public class ItemStair extends ItemMultiPartBase {
	public ItemStair(String name) {
		super(name);
	}
	
	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
		return new PartStair();
	}
}
