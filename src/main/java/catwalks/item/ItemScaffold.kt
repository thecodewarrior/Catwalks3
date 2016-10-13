package catwalks.item

import catwalks.EnumCatwalkMaterial
import catwalks.part.PartScaffold
import mcmultipart.multipart.IMultipart
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class ItemScaffold(name: String) : ItemMultiPartBase(name) {

    init {
        setHasSubtypes(true)
        maxDamage = 0
    }

    override fun getSubItems(itemIn: Item, tab: CreativeTabs?, subItems: MutableList<ItemStack>) {
        for (mat in EnumCatwalkMaterial.values()) {
            if (mat.show())
                subItems.add(ItemStack(itemIn, 1, mat.ordinal))
        }
    }

    override fun getUnlocalizedName(stack: ItemStack?): String {
        return super.getUnlocalizedName(stack) + "." + EnumCatwalkMaterial.values()[stack!!.itemDamage].getName().toLowerCase()
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    override fun getMetadata(damage: Int): Int {
        return damage
    }

    override fun createPart(world: World, pos: BlockPos, side: EnumFacing, hit: Vec3d, stack: ItemStack, player: EntityPlayer): IMultipart {
        val p = PartScaffold()
        p.catwalkMaterial = EnumCatwalkMaterial.values()[stack.metadata]
        return p
    }
}
