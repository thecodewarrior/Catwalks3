package catwalks.block

import catwalks.Conf
import catwalks.Const
import catwalks.EnumCatwalkMaterial
import catwalks.register.ItemRegister
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class BlockScaffolding(val page: Int) : BlockBase(Material.IRON, "scaffold" + page) {

    init {
        setHardness(0.1f)
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult?, world: World, pos: BlockPos, player: EntityPlayer?): ItemStack {
        return ItemStack(ItemRegister.scaffold, 1, state.getValue(Const.MATERIAL).ordinal)
    }

    override fun getPlayerRelativeBlockHardness(state: IBlockState, playerIn: EntityPlayer, worldIn: World, pos: BlockPos): Float {

        if (playerIn.inventory.getCurrentItem() != null) {
            val item = playerIn.inventory.getCurrentItem()!!.item

            if (item === Item.getItemFromBlock(this) && playerIn.isSneaking) {
                return super.getPlayerRelativeBlockHardness(state, playerIn, worldIn, pos)
            }

            if (item === ItemRegister.tool) {
                return 1f
            }
        }
        //		if(state.getValue(Const.MATERIAL_META) == EnumCatwalkMaterial.WOOD) {
        //			return 1;
        //		}
        return super.getPlayerRelativeBlockHardness(state, playerIn, worldIn, pos)
    }

    override fun dropBlockAsItemWithChance(worldIn: World, pos: BlockPos, state: IBlockState, chance: Float, fortune: Int) {
        var chance = chance
        if (this.harvesters.get() == null) {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune)
            return
        }

        if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots)
        // do not drop items while restoring blockstates, prevents item dupe
        {
            val items = getDrops(worldIn, pos, state, fortune)
            chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, chance, false, harvesters.get())

            for (item in items) {
                if (worldIn.rand.nextFloat() <= chance) {
                    spawnAsEntityNoPickupDelayExactPos(worldIn, this.harvesters.get().positionVector.addVector(0.0, 0.25, 0.0), item)
                }
            }
        }
    }

    override fun damageDropped(state: IBlockState?): Int {
        return getMetaFromState(state)
    }

    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
        return true
    }

    init { /* state stuff */
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, Const.MATERIAL)
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        return defaultState.withProperty(Const.MATERIAL, EnumCatwalkMaterial.values()[page shl 4 and meta])
    }

    override fun getMetaFromState(state: IBlockState?): Int {
        return state!!.getValue(Const.MATERIAL).ordinal and 15
    }

    init { /* rendering stuff */
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

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean {
        if (Conf.showScaffoldInsideFaces)
            return super.shouldSideBeRendered(state, worldIn, pos, side)
        return if (worldIn.getBlockState(pos.offset(side)).block === this) false else super.shouldSideBeRendered(state, worldIn, pos, side)
    }

    init { /* ladder */
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
