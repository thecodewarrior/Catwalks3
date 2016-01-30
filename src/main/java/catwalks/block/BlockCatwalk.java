package catwalks.block;

import catwalks.block.extended.BlockExtended;
import catwalks.block.extended.ExtendedData;
import catwalks.block.extended.TileExtended;
import catwalks.block.property.UPropertyBool;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockCatwalk extends BlockExtended implements ICatwalkConnect {

	public static UPropertyBool BOTTOM = new UPropertyBool("bottom");
	public static UPropertyBool NORTH  = new UPropertyBool("north");
	public static UPropertyBool SOUTH  = new UPropertyBool("south");
	public static UPropertyBool EAST   = new UPropertyBool("east");
	public static UPropertyBool WEST   = new UPropertyBool("west");
	
	public static UPropertyBool TAPE   = new UPropertyBool("tape");
	public static UPropertyBool LIGHTS = new UPropertyBool("lights");
	
	public BlockCatwalk() {
		super(Material.iron, "catwalk");
	}

	@Override
	public int getMetaFromState(IBlockState state) { return 0; }
	@Override
	public IBlockState getStateFromMeta(int meta) { return getDefaultState(); }
	
	@Override
	public ExtendedData getData(World world, IBlockState state) {
		return null;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected BlockState createBlockState() {
		IProperty[] listedProperties = new IProperty[0]; // no listed properties
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { BOTTOM, NORTH, SOUTH, WEST, EAST, TAPE, LIGHTS };
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	}
	
	int I_BOTTOM=0, I_NORTH=1, I_SOUTH=2, I_EAST=3, I_WEST=4, I_TAPE=5, I_LIGHTS=6;
	
	@Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		
		boolean pass = tile != null;
		
		return ((IExtendedBlockState)state)
				.withProperty(BOTTOM, pass && tile.getBoolean(I_BOTTOM))
				.withProperty(NORTH,  pass && tile.getBoolean(I_NORTH))
				.withProperty(SOUTH,  pass && tile.getBoolean(I_SOUTH))
				.withProperty(EAST,   pass && tile.getBoolean(I_EAST))
				.withProperty(WEST,   pass && tile.getBoolean(I_WEST))
				.withProperty(TAPE,   pass && tile.getBoolean(I_TAPE))
				.withProperty(LIGHTS, pass && tile.getBoolean(I_LIGHTS))
		;
	}
	
	public boolean isSideOpen(World world, BlockPos pos, EnumFacing side) {
		TileExtended tile = (TileExtended) world.getTileEntity(pos);
		int id = 0;
		switch (side) {
		case DOWN:
			id = I_BOTTOM;
			break;
		case NORTH:
			id = I_NORTH;
			break;
		case SOUTH:
			id = I_SOUTH;
			break;
		case EAST:
			id = I_EAST;
			break;
		case WEST:
			id = I_WEST;
			break;
		default:
			break;
		}
		return tile.getBoolean(id);
	}
	
	public boolean isWide(World world, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		
		TileExtended tile = (TileExtended) worldIn.getTileEntity(pos);
		int id = 0;
		
		switch (side) {
		case DOWN:
			id = 0;
			break;
		case NORTH:
			id = 1;
			break;
		case SOUTH:
			id = 2;
			break;
		case EAST:
			id = 3;
			break;
		case WEST:
			id = 4;
			break;
		default:
			break;
		}
		
		if(side != EnumFacing.UP) {
			tile.setBoolean(id, !tile.getBoolean(id));
			tile.markDirty();
			worldIn.markBlockForUpdate(pos);
		}
		
		return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
	}
	
	@Override
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT_MIPPED;
	}
	
}
