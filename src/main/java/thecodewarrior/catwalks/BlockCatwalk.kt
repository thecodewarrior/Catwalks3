package thecodewarrior.catwalks

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.kotlin.*
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
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
        val PROPERTY_MATERIAL: PropertyEnum<CatwalkMaterial> = PropertyEnum.create("material", CatwalkMaterial::class.java)

        val boundingBoxes: Map<EnumFacing, Pair<AxisAlignedBB, AxisAlignedBB>>

        init {
            fun p(pixels: Int) = pixels/16.0
            fun aabb(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int) =
                    AxisAlignedBB(p(minX), p(minY), p(minZ), p(maxX), p(maxY), p(maxZ))
            boundingBoxes = enumMapOf(
                    EnumFacing.DOWN  to ( aabb( 0, 0,  0, 16,  0, 16) to aabb( 4, 0,  4, 12, 0, 12) ),
                    EnumFacing.NORTH to ( aabb( 0, 0,  0, 16, 16,  0) to aabb( 0, 0,  0, 16, 8,  0) ),
                    EnumFacing.SOUTH to ( aabb( 0, 0, 16, 16, 16, 16) to aabb( 0, 0, 16, 16, 8, 16) ),
                    EnumFacing.WEST  to ( aabb( 0, 0,  0,  0, 16, 16) to aabb( 0, 0,  0,  0, 8, 16) ),
                    EnumFacing.EAST  to ( aabb(16, 0,  0, 16, 16, 16) to aabb(16, 0,  0, 16, 8, 16) )
            )
        }
    }

    init {
        setHardness(1f)
        setResistance(5f)
        setSoundType(SoundType.METAL)
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if(playerIn.getHeldItem(hand).item != Const.I_BLOWTORCH) return false
        val tile = worldIn.getTileEntity(pos) as? TileCatwalk ?: return false
        tile.sides[facing] = !(tile.sides[facing] ?: true)
        worldIn.notifyBlockUpdate(pos, state, state, 2) // no block update, just re-render
        return true
    }

    override fun collisionRayTrace(blockState: IBlockState, worldIn: World, pos: BlockPos, start: Vec3d, end: Vec3d): RayTraceResult? {
        val tile = worldIn.getTileEntity(pos) as? TileCatwalk ?: return null
        var result = boundingBoxes.map {
            val has = tile.sides[it.key] ?: false
            val bb = if(has) it.value.first else it.value.second
            (it.key to bb) to rayTrace(pos, start, end, bb)
        }.filter { it.second != null }.minBy {
            (start-it.second!!.hitVec).lengthSquared()
        } ?: return null

        result = result.first to RayTraceResult(result.second!!.typeOfHit, result.second!!.hitVec, result.first.first, result.second!!.blockPos)

        ClientRunnable.run {
            this.bounds = result.first.second
        }
        return result.second
    }

    var bounds = FULL_BLOCK_AABB
    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?): AxisAlignedBB {
        return bounds
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, p_185477_7_: Boolean) {
        val tile = worldIn.getTileEntity(pos) as? TileCatwalk ?: return
        boundingBoxes.filter { tile.sides[it.key] ?: false }.values.forEach {
            val bb = it.first.offset(pos)
            if(bb.intersects(entityBox))
                collidingBoxes.add(bb)
        }
    }

    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        val tile = worldIn.getTileEntity(pos) as? TileCatwalk
        if(tile != null) {
            if(stack != null) {
                val mat = stack.nbt["material"]?.fromNBT<CatwalkMaterial>()
                if(mat != null) {
                    tile.material = mat
                }
            }
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

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
        val stack = ItemStack(this, 1)
        val mat = state[PROPERTY_MATERIAL]
        stack.nbt["material"] = mat.toNBT()
        drops.add(stack)
    }

    override fun createItemForm(): ItemBlock? {
        return ItemBlockCatwalk(this)
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TileCatwalk()
    }

    override fun createBlockState(): BlockStateContainer {
        return ExtendedBlockState(this, arrayOf(PROPERTY_MATERIAL), arrayOf(PROPERTY_CATWALK_STATE))
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        val tile = world.getTileEntitySafely(pos) as? TileCatwalk
        var s = super.getExtendedState(state, world, pos) as IExtendedBlockState
        if(tile != null) {
            s = s.withProperty(PROPERTY_CATWALK_STATE, tile.getCatwalkState())
        }
        return s
    }

    override fun getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState {
        val tile = worldIn.getTileEntitySafely(pos) as? TileCatwalk
        var s = super.getActualState(state, worldIn, pos)
        if(tile != null) {
            s = s.withProperty(PROPERTY_MATERIAL, tile.material)
        }
        return s
    }

    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT_MIPPED
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState
    }

    override fun getMetaFromState(state: IBlockState?): Int {
        return 0
    }

    override fun isOpaqueCube(state: IBlockState?): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState?): Boolean {
        return false
    }
}
