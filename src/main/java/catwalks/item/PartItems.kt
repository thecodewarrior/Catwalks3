package catwalks.item

import catwalks.EnumCatwalkMaterial
import catwalks.part.PartCatwalk
import catwalks.part.PartScaffold
import mcmultipart.multipart.IMultipart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * Created by TheCodeWarrior
 */
class ItemScaffold(name: String) : ItemMultiPartCatwalkMaterialBase(name) {
    override fun createPart(world: World, pos: BlockPos, side: EnumFacing, hit: Vec3d, stack: ItemStack, player: EntityPlayer): IMultipart {
        val p = PartScaffold()
        p.catwalkMaterial = EnumCatwalkMaterial.values()[stack.metadata]
        return p
    }
}

class ItemCatwalk(name: String) : ItemMultiPartCatwalkMaterialBase(name) {
    override fun createPart(world: World, pos: BlockPos, side: EnumFacing, hit: Vec3d, stack: ItemStack, player: EntityPlayer): IMultipart {
        val p = PartCatwalk()
        p.catwalkMaterial = EnumCatwalkMaterial.values()[stack.metadata]
        return p
    }
}

//class ItemLadder (name: String) : ItemMultiPartCatwalkMaterialBase(name) {
//    override fun createPart(world: World, pos: BlockPos, side: EnumFacing, hit: Vec3d, stack: ItemStack, player: EntityPlayer): IMultipart {
//        val p = PartLadder()
//        p.catwalkMaterial = EnumCatwalkMaterial.values()[stack.metadata]
//        return p
//    }
//}
