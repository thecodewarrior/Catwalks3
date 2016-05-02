package catwalks.block;

import catwalks.CatwalksMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockBase extends Block {

	
	public BlockBase(Material material, String name) {
		this(material, name, null);
	}
	
	@SuppressWarnings("unchecked")
	public BlockBase(Material materialIn, String name, Class<?> clazz) {
		super(materialIn);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CatwalksMod.tab);
		initPreRegister();
		if(clazz == null) {
			GameRegistry.registerBlock(this);
		} else {
			GameRegistry.registerBlock(this, (Class<? extends ItemBlock>)clazz);
		}
	}
	
	public void initPreRegister() {}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
		MovingObjectPosition mop = collisionRayTrace(world, pos, CatwalksMod.proxy.getPlayerLooking(start, end), start, end);
		return mop;
    }
	
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, EntityPlayer player, Vec3d start, Vec3d end) {
		return super.collisionRayTrace(world, pos, start, end);
	}
	
}
