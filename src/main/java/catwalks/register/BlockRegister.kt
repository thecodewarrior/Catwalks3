package catwalks.register

import catwalks.CatwalksMod
import catwalks.Const
import catwalks.EnumCatwalkMaterial
import catwalks.block.BlockScaffolding
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry

object BlockRegister {

    var scaffolds: Array<BlockScaffolding> = Array<BlockScaffolding>(
            Math.ceil(EnumCatwalkMaterial.values().size / 16.0).toInt()
    ) {
        BlockScaffolding(it)
    }

    var dummyBlocks: Array<Block> = Array<Block>(8) {
        val b = Block(Material.ROCK)
        b.unlocalizedName = "catwalks.dummy." + it
        b.setRegistryName(ResourceLocation(CatwalksMod.MODID, "dummy_" + it))
        b.setCreativeTab(CatwalksMod.tab)
        if(Const.developmentEnvironment)
            GameRegistry.register(b)
        b
    }

    fun getScaffold(mat: EnumCatwalkMaterial): BlockScaffolding {
        return scaffolds[mat.ordinal shr 4]
    }
}
