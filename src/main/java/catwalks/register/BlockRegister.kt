package catwalks.register

import catwalks.EnumCatwalkMaterial
import catwalks.block.BlockScaffolding

object BlockRegister {

    var scaffolds: Array<BlockScaffolding> = Array<BlockScaffolding>(
            Math.ceil(EnumCatwalkMaterial.values().size / 16.0).toInt()
    ) {
        BlockScaffolding(it)
    }

    fun getScaffold(mat: EnumCatwalkMaterial): BlockScaffolding {
        return scaffolds[mat.ordinal shr 4]
    }
}
