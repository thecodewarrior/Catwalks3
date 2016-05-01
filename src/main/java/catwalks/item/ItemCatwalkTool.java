package catwalks.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemCatwalkTool extends ItemBase {

	public ItemCatwalkTool() {
		super("tool");
		setUnlocalizedName("catwalktool");
	}
	
	@Override
	public boolean doesSneakBypassUse(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}
	
}
