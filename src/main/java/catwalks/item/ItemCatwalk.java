package catwalks.item;

import catwalks.block.EnumCatwalkMaterial;
import catwalks.part.PartCatwalk;
import catwalks.part.PartScaffold;
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
public class ItemCatwalk extends ItemMultiPartBase {
	
	public ItemCatwalk(String name) {
		super(name);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	/**
	 * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
	 * placed as a Block (mostly used with ItemBlocks).
	 */
	public int getMetadata(int damage)
	{
		return damage;
	}
	
	@Override
	public IMultipart createPart(World world, BlockPos pos, EnumFacing side, Vec3d hit, ItemStack stack, EntityPlayer player) {
		PartCatwalk p = new PartCatwalk();
		p.setCatwalkMaterial(EnumCatwalkMaterial.values()[stack.getMetadata()]);
		return p;
	}
}
