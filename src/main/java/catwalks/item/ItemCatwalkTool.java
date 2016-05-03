package catwalks.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class ItemCatwalkTool extends ItemBase {

	public ItemCatwalkTool() {
		super("tool");
		setUnlocalizedName("catwalktool");
	}
	
	@Override
	public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
		return true;
	}
	
}
