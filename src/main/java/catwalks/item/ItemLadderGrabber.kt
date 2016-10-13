package catwalks.item

import catwalks.Conf
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.world.World

class ItemLadderGrabber : ItemBase("ladderGrabber") {

    init {
        setMaxStackSize(1)
    }

    override fun addInformation(stack: ItemStack?, playerIn: EntityPlayer?, tooltip: MutableList<String>?, advanced: Boolean) {
        if (Conf.shouldHaveLaddeyGrabbey)
            tooltip!!.add(I18n.format(unlocalizedName + ".info"))
        super.addInformation(stack, playerIn, tooltip, advanced)
    }

    override fun getUnlocalizedName(): String {
        if (Conf.shouldHaveLaddeyGrabbey)
            return super.getUnlocalizedName() + ".troll"
        return super.getUnlocalizedName()
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return false
    }

    override fun hasEffect(stack: ItemStack): Boolean {
        return stack.metadata != 0
    }

    override fun onItemRightClick(itemStackIn: ItemStack, worldIn: World?, playerIn: EntityPlayer?,
                                  hand: EnumHand?): ActionResult<ItemStack> {
        if (playerIn!!.isSneaking) {
            val stack = itemStackIn.copy()
            stack.itemDamage = if (stack.metadata == 0) 1 else 0
        }
        return ActionResult(EnumActionResult.PASS, itemStackIn)
    }

    override val customRenderVariants: Array<String>?
        get() = arrayOf("", "")
}
