package catwalks.item

import catwalks.CatwalksMod
import catwalks.register.ItemRegister
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

import java.text.MessageFormat

open class ItemBase(var name: String) : Item(), IItemBase {

    init {
        unlocalizedName = name
        GameRegistry.registerItem(this, name)
        ItemRegister.renderRegisterItems.add(this)
        creativeTab = CatwalksMod.tab
    }

    fun getInformationArguments(stack: ItemStack?, player: EntityPlayer?): Array<Any?> {
        return arrayOfNulls(0)
    }

    override fun addInformation(stack: ItemStack?, playerIn: EntityPlayer?, tooltip: MutableList<String>?, advanced: Boolean) {
        var i = 0
        val unloc = unlocalizedName + ".info."
        val arguments = getInformationArguments(stack, playerIn)

        while (true) {

            if (!I18n.hasKey(unloc + i))
                break

            val translated = I18n.format(unloc + i)
            if (translated.length == 0)
                break

            tooltip!!.add(MessageFormat.format(translated, *arguments))
            i += 1
        }

        super.addInformation(stack, playerIn, tooltip, advanced)
    }

    override val customRenderVariants: Array<String>?
        get() = null
}
