package catwalks.block;

import catwalks.CatwalksMod;
import catwalks.shade.ccl.raytracer.RayTracer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockBase extends Block {

	
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
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		MovingObjectPosition mop = collisionRayTrace(world, pos, CatwalksMod.proxy.getPlayerLooking(start, end), start, end);
		return mop;
    }
	
	public abstract MovingObjectPosition collisionRayTrace(World world, BlockPos pos, EntityPlayer player, Vec3 start, Vec3 end);
	
}
