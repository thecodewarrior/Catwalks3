package thecodewarrior.catwalks

import com.teamwizardry.librarianlib.features.base.ModCreativeTab
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge

/**
 * TODO: Document file Consts
 *
 * Created by TheCodeWarrior
 */
object Const {
    init { MinecraftForge.EVENT_BUS.register(this) }

    val TAB = object : ModCreativeTab() {
        init { this.registerDefaultTab() }
        override val iconStack: ItemStack
            get() = ItemStack(I_BLOWTORCH)
    }

    val B_CATWALK = BlockCatwalk()
    val I_BLOWTORCH = ItemBlowtorch()
}
