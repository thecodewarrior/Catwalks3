package catwalks.block

import catwalks.Const
import catwalks.EnumCatwalkMaterial
import catwalks.part.PartScaffold
import catwalks.part.data.ScaffoldRenderData
import catwalks.register.ItemRegister
import catwalks.util.GeneralUtil
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState

class BlockScaffolding(val page: Int) : BlockBase(Material.IRON, "scaffold" + page) {

    init {
        setHardness(0.1f)
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult?, world: World, pos: BlockPos, player: EntityPlayer?): ItemStack {
        return ItemStack(ItemRegister.scaffold, 1, state.getValue(Const.MATERIAL).ordinal)
    }

    override fun damageDropped(state: IBlockState): Int {
        return state.getValue(Const.MATERIAL).ordinal
    }

    override fun createBlockState(): BlockStateContainer {
        return ExtendedBlockState(this, arrayOf(Const.MATERIAL), arrayOf(Const.SCAFFOLD_RENDER_DATA))
    }

    override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
        if(state !is IExtendedBlockState)
            return state

        val arr = BooleanArray(EnumFacing.values().size) { true }

        for(dir in EnumFacing.values()) {
            val poff = pos.offset(dir)
            if(world.isSideSolid(poff, dir.opposite, false)) {
                arr[dir.ordinal] = false
            }
            if(arr[dir.ordinal]) {
                val adjacent = world.getBlockState(poff)
                val block = adjacent.block
                if(block is BlockScaffolding) {
                    if(adjacent.getValue(Const.MATERIAL) == state.getValue(Const.MATERIAL)) {
                        arr[dir.ordinal] = false
                    }
                }
            }
            if(arr[dir.ordinal]) {
                val part = GeneralUtil.getPart(PartScaffold::class.java, world, poff)
                if(part != null && part.catwalkMaterial == state.getValue(Const.MATERIAL)) {
                    arr[dir.ordinal] = false
                }
            }
        }

        return state.withProperty(Const.SCAFFOLD_RENDER_DATA, ScaffoldRenderData(arr))
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var i = (page shl 4) or meta
        if(i >= EnumCatwalkMaterial.values().size)
            i = 0
        var mat = EnumCatwalkMaterial.values()[i]
        if(mat !in Const.MATERIAL.allowedValues)
            mat = EnumCatwalkMaterial.CUSTOM_0
        return defaultState.withProperty(Const.MATERIAL, mat)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(Const.MATERIAL).ordinal and 15
    }

    override fun getBlockLayer(): BlockRenderLayer {
        return BlockRenderLayer.CUTOUT
    }

    override fun isOpaqueCube(state: IBlockState?): Boolean {
        return false
    }

    override fun isFullCube(state: IBlockState?): Boolean {
        return false
    }

    companion object {

        fun spawnAsEntityNoPickupDelayExactPos(worldIn: World, pos: Vec3d, stack: ItemStack) {
            if (!worldIn.isRemote && worldIn.gameRules.getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots)
            // do not drop items while restoring blockstates, prevents item dupe
            {
                if (Block.captureDrops.get()) {
                    Block.capturedDrops.get().add(stack)
                    return
                }
                val entityitem = EntityItem(worldIn, pos.xCoord, pos.yCoord, pos.zCoord, stack)
                entityitem.setPickupDelay(0)
                worldIn.spawnEntityInWorld(entityitem)
            }
        }
    }

    //	@Override
    //	public boolean shouldApplyClimbing(World world, BlockPos pos, EntityLivingBase entity) {
    //		return ( entity.moveForward != 0 || entity.moveStrafing != 0 );// && world.getBlockState(pos).getValue(Const.MATERIAL_META) == EnumCatwalkMaterial.WOOD;
    //	}
    //
    //	@Override
    //	public boolean shouldApplyFalling(World world, BlockPos pos, EntityLivingBase entity) {
    //		return true;//world.getBlockState(pos).getValue(Const.MATERIAL_META) == EnumCatwalkMaterial.WOOD;
    //	}
    //
    //	@Override
    //	public double climbSpeed(World world, BlockPos pos, EntityLivingBase entity) {
    //		return 1;
    //	}
    //
    //	@Override
    //	public double fallSpeed(World world, BlockPos pos, EntityLivingBase entity) {
    //		return 1;
    //	}
    //
    //	@Override
    //	public double horizontalSpeed(World world, BlockPos pos, EntityLivingBase entity) {
    //		return Double.POSITIVE_INFINITY;
    //	}
}
