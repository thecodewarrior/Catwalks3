package thecodewarrior.catwalks

import com.teamwizardry.librarianlib.features.base.block.ItemModBlock
import com.teamwizardry.librarianlib.features.kotlin.fromNBT
import com.teamwizardry.librarianlib.features.kotlin.nbt
import com.teamwizardry.librarianlib.features.kotlin.toNBT
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraftforge.client.model.ModelLoader
import java.util.*

/**
 * TODO: Document file ItemBlockCatwalk
 *
 * Created by TheCodeWarrior
 */
class ItemBlockCatwalk(val block: BlockCatwalk) : ItemModBlock(block), ItemMeshDefinition {
    init {
        ClientRunnable.run {
            ModelLoader.registerItemVariants(this, *CatwalkMaterial.values().map { type ->
                val name = type.name.toLowerCase(Locale.ROOT)

                ModelResourceLocation("catwalks:catwalk_items", "material=$name")
            }.toTypedArray())
        }
    }
    override fun getUnlocalizedName(stack: ItemStack): String {
        return super.getUnlocalizedName(stack) + "." +
                (stack.nbt["material"]?.fromNBT<CatwalkMaterial>()?.getName() ?: "unknown")
    }

    override fun getSubItems(tab: CreativeTabs, subItems: NonNullList<ItemStack>) {
        if (isInCreativeTab(tab))
            CatwalkMaterial.values().forEach { mat ->
                val stack = ItemStack(this, 1)
                stack.nbt["material"] = mat.toNBT()
                subItems.add(stack)
            }
    }

    override val meshDefinition: ((ItemStack) -> ModelResourceLocation)?
        get() = this::getModelLocation

    override fun getModelLocation(stack: ItemStack): ModelResourceLocation {
        val type = stack.nbt["material"]?.fromNBT<CatwalkMaterial>() ?: CatwalkMaterial.CLASSIC

        val name = type.name.toLowerCase(Locale.ROOT)

        return ModelResourceLocation("catwalks:catwalk_items", "material=$name")
    }
}
