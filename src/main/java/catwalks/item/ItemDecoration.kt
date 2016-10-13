package catwalks.item

import net.minecraft.client.resources.I18n
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ItemDecoration(name: String) : ItemBase(name) {

    init {
        setMaxStackSize(1)
        setMaxDamage(255)
        unlocalizedName = "decoration." + name
    }

    override fun addInformation(stack: ItemStack?, playerIn: EntityPlayer?, tooltip: MutableList<String>?, advanced: Boolean) {
        tooltip!!.add(I18n.format("item.decoration.uses", stack!!.maxDamage - stack.itemDamage))
        tooltip.add(I18n.format("item.decoration.combine"))
        tooltip.add(I18n.format("item.decoration.split"))
        super.addInformation(stack, playerIn, tooltip, advanced)
    }

    override fun onItemUse(stack: ItemStack?, playerIn: EntityPlayer?, worldIn: World?, pos: BlockPos?,
                           hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        if (stack!!.itemDamage == this.getMaxDamage(stack) && !playerIn!!.isSneaking || stack.itemDamage == 0 && playerIn!!.isSneaking && !playerIn.capabilities.isCreativeMode) {
            return EnumActionResult.FAIL
        }

        val state = worldIn!!.getBlockState(pos!!)

        // TODO
        //		if(state.getBlock() instanceof IDecoratable) {
        //			if( ((IDecoratable) state.getBlock()).putDecoration(worldIn, pos, name, !playerIn.isSneaking()) ) {
        //				stack.damageItem(playerIn.isSneaking() ? -1 : 1, playerIn);
        //				return EnumActionResult.SUCCESS;
        //			}
        //		}

        return EnumActionResult.PASS
    }

    fun damageItem(stack: ItemStack, amount: Int, entityIn: EntityLivingBase) {
        if (entityIn is EntityPlayer && entityIn.capabilities.isCreativeMode) {
            return
        }
        stack.itemDamage = stack.itemDamage - amount
    }

    override fun hasCustomEntity(stack: ItemStack?): Boolean {
        return true
    }

    override fun createEntity(world: World?, location: Entity?, itemstack: ItemStack?): Entity {
        val e = EntityItemDecoration(world, location!!.posX, location.posY, location.posZ, itemstack)
        val tag = NBTTagCompound()
        location.writeToNBT(tag)
        e.readFromNBT(tag)
        return e
    }

}
