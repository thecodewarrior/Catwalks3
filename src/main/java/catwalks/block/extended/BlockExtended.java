package catwalks.block.extended;

import catwalks.block.BlockBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockExtended extends BlockBase {

	public BlockExtended(Material material, String name) {
		super(material, name);
	}
	
	public abstract ExtendedData getData(World world, IBlockState state);
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileExtended(getData(world, state));
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
}
