package catwalks.block

import catwalks.CatwalksMod
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.registry.GameRegistry

import java.util.function.Function

open class BlockBase(material: Material, name: String) : Block(material) {


    init {
        unlocalizedName = name
        setRegistryName(name)
        setCreativeTab(CatwalksMod.tab)
        initPreRegister()
        GameRegistry.register(this)
    }

    @SuppressWarnings("unchecked")
    constructor(materialIn: Material, name: String, item: Function<Block, ItemBlock>?) : this(materialIn, name) {
        if (item == null) {
            GameRegistry.register(ItemBlock(this).setRegistryName(registryName))
        } else {
            GameRegistry.register(item.apply(this).setRegistryName(registryName))
        }
    }

    fun initPreRegister() {
    }

}
