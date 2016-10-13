package catwalks.item

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class ItemCatwalkTool : ItemBase("tool") {

    init {
        unlocalizedName = "catwalktool"
        setMaxStackSize(1)
    }

    override fun doesSneakBypassUse(stack: ItemStack?, world: IBlockAccess?, pos: BlockPos?, player: EntityPlayer?): Boolean {
        return true
    }

}
