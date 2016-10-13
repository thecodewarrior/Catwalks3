package catwalks.item

import catwalks.part.PartStair
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
class ItemStair(name: String) : ItemMultiPartBase(name) {

    override fun createPart(world: World, pos: BlockPos, side: EnumFacing, hit: Vec3d, stack: ItemStack, player: EntityPlayer): IMultipart {
        return PartStair()
    }
}
