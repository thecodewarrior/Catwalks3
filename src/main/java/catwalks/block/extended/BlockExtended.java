package catwalks.block.extended;

import java.util.function.Function;

import catwalks.block.BlockBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockExtended extends BlockBase {

	public BlockExtended(Material material, String name) {
		super(material, name);
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
