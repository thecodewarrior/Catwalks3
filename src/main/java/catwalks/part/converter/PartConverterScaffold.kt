package catwalks.part.converter

import catwalks.Const
import catwalks.part.PartScaffold
import catwalks.register.BlockRegister
import mcmultipart.multipart.IMultipart
import mcmultipart.multipart.IMultipartContainer
import mcmultipart.multipart.IPartConverter
import mcmultipart.multipart.IReversePartConverter
import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import java.util.*

/**
 * Created by TheCodeWarrior
 */
class PartConverterScaffold : IPartConverter, IReversePartConverter {

    internal var collection: Collection<Block> = Arrays.asList<Block>(*BlockRegister.scaffolds)

    override fun getConvertableBlocks(): Collection<Block> {
        return collection
    }

    override fun convertBlock(world: IBlockAccess, pos: BlockPos, simulated: Boolean): Collection<IMultipart> {
        val state = world.getBlockState(pos)
        //		BlockScaffolding block = state.getBlock();
        val part = PartScaffold()
        part.catwalkMaterial = state.getValue(Const.MATERIAL)
        return listOf<PartScaffold>(part)
    }

    override fun convertToBlock(container: IMultipartContainer): Boolean {
        val parts = container.parts
        if (parts.size == 1) {
            val firstPart = parts.iterator().next()
            if (firstPart is PartScaffold) {
                val mat = firstPart.catwalkMaterial
                container.removePart(firstPart)
                val old = container.worldIn.getBlockState(container.posIn)
                val new = BlockRegister.getScaffold(mat).defaultState.withProperty(Const.MATERIAL, mat)
                container.worldIn.setBlockState(container.posIn, new)
//                container.worldIn.notifyBlockUpdate(container.posIn, old, new, 3)
                return true
            }
        }
        return false
    }
}
