package catwalks.block.extended;

import java.util.List;

import com.google.common.collect.ImmutableList;

import catwalks.Const;
import catwalks.block.BlockCatwalkBase;
import catwalks.block.ICatwalkConnect;
import catwalks.block.ICatwalkConnect.EnumSideType;
import catwalks.item.ItemBlockCatwalk;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockCagedLadder extends BlockCatwalkBase {

	public BlockCagedLadder() {
		super(Material.iron, "cagedLadder", ItemBlockCatwalk.class);
		setHardness(1.5f);
	}

	@Override
	public void addAdditionalProperties(List<IUnlistedProperty> list) {
		list.add(Const.NORTH_LADDER_EXT);
		list.add(Const.SOUTH_LADDER_EXT);
		list.add(Const.EAST_LADDER_EXT);
		list.add(Const.WEST_LADDER_EXT);
	}

	@Override
	public IExtendedBlockState addProperties(TileExtended tile, IExtendedBlockState state) {
		boolean north = false, south = false, east = false, west = false;
		
		BlockPos pos = tile.getPos();
		World world = tile.getWorld();
		
		return state
				.withProperty(Const.NORTH_LADDER_EXT, north)
				.withProperty(Const.SOUTH_LADDER_EXT, south)
				.withProperty(Const.EAST_LADDER_EXT, east)
				.withProperty(Const.WEST_LADDER_EXT, west)
		;
	}
	
	{ /* ICatwalkConnect */ }
	
	@Override
	public boolean hasEdge(World world, BlockPos pos, EnumCubeEdge edge) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		return state.getValue(sides.getA(edge.getDir1())) || (edge.getDir2().getAxis() != Axis.Y && state.getValue(sides.getA(edge.getDir2())) );
	}
	
	@Override
	public boolean hasSide(World world, BlockPos pos, EnumFacing side) {
		IExtendedBlockState state = (IExtendedBlockState) getExtendedState(world.getBlockState(pos), world, pos);
		return (side.getAxis() != Axis.Y && state.getValue(sides.getA(side)) );
	}
	
	@Override
	public void setSide(World world, BlockPos pos, EnumFacing side, boolean value) {
		if(side == EnumFacing.UP)
			return;
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		tile.setBoolean(sides.getC(side), value);
	}
	
	@Override
	public Object sideData(World world, BlockPos pos, EnumFacing side) {
		return null;
	}
	
	@Override
	public EnumSideType sideType(World world, BlockPos pos, EnumFacing side) {
		return EnumSideType.FULL;
	}

	@Override
	public void initColllisionBoxes() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<CollisionBox> getCollisionBoxes(IExtendedBlockState state, World world, BlockPos pos) {
		return ImmutableList.of();
	}

	@Override
	public void initSides() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<LookSide> lookSides(IExtendedBlockState state, World world, BlockPos pos) {
		return ImmutableList.of();

	}

}
