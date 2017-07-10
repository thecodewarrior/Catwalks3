package thecodewarrior.catwalks

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.kotlin.getTileEntitySafely
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import thecodewarrior.catwalks.model.CatwalkState

/**
 * TODO: Document file BlockCatwalk
 *
 * Created by TheCodeWarrior
 */
class BlockCatwalk : BlockModContainer("catwalk", Material.IRON) {
    companion object {
        val PROPERTY_CATWALK_STATE = UnlistedArbitraryProperty("state", CatwalkState::class.java)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = worldIn.getTileEntity(pos) as? TileCatwalk ?: return false
        tile.sides[facing] = !(tile.sides[facing] ?: true)
        worldIn.notifyBlockUpdate(pos, state, state, 2) // no block update, just re-render
        return true
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        val tile = worldIn.getTileEntity(pos) as? TileCatwalk
        if(tile != null) {
            EnumFacing.HORIZONTALS.forEach { dir ->
                val other = worldIn.getTileEntity(pos.offset(dir)) as? TileCatwalk
                if (other != null) {
                    other.sides[dir.opposite] = false
                    tile.sides[dir] = false
                }
            }
        }
    }

    override fun onBlockDestroyedByPlayer(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockDestroyedByPlayer(worldIn, pos, state)
        EnumFacing.HORIZONTALS.forEach { dir ->
            val other = worldIn.getTileEntity(pos.offset(dir)) as? TileCatwalk
            if(other != null) {
                other.sides[dir.opposite] = true
            }
        }
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TileCatwalk()
    }

    override fun createBlockState(): BlockStateContainer {
        return ExtendedBlockState(this, emptyArray(), arrayOf(PROPERTY_CATWALK_STATE))
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val tile = world.getTileEntitySafely(pos) as? TileCatwalk
        var s = super.getExtendedState(state, world, pos) as IExtendedBlockState
        if(tile != null) {
            s = s.withProperty(PROPERTY_CATWALK_STATE, tile.getCatwalkState())
        }
        return s
    }

    override fun isOpaqueCube(state: IBlockState?): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState?): Boolean {
        return false
    }
}
