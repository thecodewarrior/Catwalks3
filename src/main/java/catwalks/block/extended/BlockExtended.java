package catwalks.block.extended;

import catwalks.block.BlockBase;
import catwalks.block.extended.tileprops.ExtendedTileProperties;
import catwalks.block.extended.tileprops.TileExtended;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.function.Function;

public abstract class BlockExtended extends BlockBase {

	protected ExtendedTileProperties allocator = new ExtendedTileProperties();
	
	public BlockExtended(Material material, String name) {
		super(material, name, null);
	}
	
	public BlockExtended(Material materialIn, String name, Function<Block, ItemBlock> item) {
		super(materialIn, name, item);
	}
		
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileExtended();
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
}
