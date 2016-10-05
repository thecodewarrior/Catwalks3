package catwalks.block;

import catwalks.CatwalksMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.function.Function;

public class BlockBase extends Block {

	
	public BlockBase(Material material, String name) {
		super(material);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CatwalksMod.tab);
		initPreRegister();
		GameRegistry.register(this);
	}
	
	@SuppressWarnings("unchecked")
	public BlockBase(Material materialIn, String name, Function<Block, ItemBlock> item) {
		this(materialIn, name);
		if(item == null) {
			GameRegistry.register(new ItemBlock(this).setRegistryName(getRegistryName()));
		} else {
			GameRegistry.register(item.apply(this).setRegistryName(getRegistryName()));
		}
	}
	
	public void initPreRegister() {}
	
	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start,
			Vec3d end) {
		return collisionRayTrace(blockState, worldIn, pos, CatwalksMod.proxy.getPlayerLooking(start, end), start, end);
    }
	
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, EntityPlayer player, Vec3d start, Vec3d end) {
		return super.collisionRayTrace(state, world, pos, start, end);
	}
	
}
