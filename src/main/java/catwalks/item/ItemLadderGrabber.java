package catwalks.item;

import java.util.List;

import catwalks.Conf;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLadderGrabber extends ItemBase {
	
	public ItemLadderGrabber() {
		super("ladderGrabber");
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if(Conf.shouldHaveLaddeyGrabbey)
			tooltip.add(I18n.format(getUnlocalizedName() + ".info"));
		super.addInformation(stack, playerIn, tooltip, advanced);
	}
	
	@Override
	public String getUnlocalizedName() {
		if(Conf.shouldHaveLaddeyGrabbey)
			return super.getUnlocalizedName() + ".troll";
		return super.getUnlocalizedName();
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return false;
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.getMetadata() != 0;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		if(playerIn.isSneaking()) {
			ItemStack stack = itemStackIn.copy();
			stack.setItemDamage(stack.getMetadata() == 0 ? 1 : 0);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
	}
	
	@Override
	public String[] getCustomRenderVariants() {
		return new String[] { "", "" };
	}
}
