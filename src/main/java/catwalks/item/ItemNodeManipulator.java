package catwalks.item;

import catwalks.node.EntityNodeBase;
import catwalks.node.NodeUtil;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.node.NodeHit;
import catwalks.shade.ccl.raytracer.RayTracer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemNodeManipulator extends ItemBase {

	public ItemNodeManipulator() {
		super("nodeManipulator");
		setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if(playerIn.isSneaking()) {
			Vec3d look = playerIn.getLook(1);
			EntityNodeBase entity = new EntityNodeBase(worldIn, playerIn.posX+look.xCoord, playerIn.posY+playerIn.eyeHeight+look.yCoord, playerIn.posZ+look.zCoord);
			
			worldIn.spawnEntityInWorld(entity);
			
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}
		
		ITraceResult<NodeHit> result = NodeUtil.rayTraceNodes(worldIn, playerIn, RayTracer.getStartVec(playerIn), playerIn.getLook(1), 10);
		
		result = null;
		
		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}
}
