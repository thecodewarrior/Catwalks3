package catwalks.item

import catwalks.EnumCatwalkMaterial
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by TheCodeWarrior
 */
abstract class ItemMultiPartCatwalkMaterialBase (name: String) : ItemMultiPartBase(name) {

    init {
        setHasSubtypes(true)
        maxDamage = 0
    }

    override fun getSubItems(itemIn: Item, tab: CreativeTabs?, subItems: MutableList<ItemStack>) {
        for (mat in EnumCatwalkMaterial.values()) {
            if (mat.STATUS.shouldShow)
                subItems.add(ItemStack(itemIn, 1, mat.ordinal))
        }
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        if(stack.itemDamage < 0 || stack.itemDamage >= EnumCatwalkMaterial.values().size)
            return super.getUnlocalizedName(stack) + ".invalid_meta"
        val mat = EnumCatwalkMaterial.values()[stack.itemDamage]
        if(!mat.STATUS.shouldShow)
            return super.getUnlocalizedName(stack) + ".material_disabled"
        return super.getUnlocalizedName(stack) + "." + mat.GROUP.name.toLowerCase() + "." + mat.name.toLowerCase()
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    override fun getMetadata(damage: Int): Int {
        return damage
    }

    override val customRenderVariants: Array<String>?
        get() = EnumCatwalkMaterial.values().filter { it.STATUS.shouldRegister }.map { name + "#material=${it.GROUP.name.toLowerCase()}_${it.name.toLowerCase()}" }.toTypedArray()
}
