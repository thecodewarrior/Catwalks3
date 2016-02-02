package catwalks.block;

import java.util.LinkedList;
import java.util.List;

import catwalks.CatwalksMod;
import codechicken.lib.raytracer.ExtendedMOP;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
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
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockBase extends Block {

	
	public BlockBase(Material material, String name) {
		this(material, name, null);
	}
	
	public BlockBase(Material materialIn, String name, Class<? extends ItemBlock> clazz) {
		super(materialIn);
		setUnlocalizedName(name);
		setRegistryName(name);
		if(clazz == null) {
			GameRegistry.registerBlock(this);
		} else {
			GameRegistry.registerBlock(this, clazz);
		}
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		return collisionRayTrace(world, pos, CatwalksMod.proxy.getPlayerLooking(start, end), start, end);
    }
	
	public abstract MovingObjectPosition collisionRayTrace(World world, BlockPos pos, EntityPlayer player, Vec3 start, Vec3 end);
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.target.typeOfHit == MovingObjectType.BLOCK && event.player.worldObj.getBlockState(event.target.getBlockPos()).getBlock() == this)
            RayTracer.retraceBlock(event.player.worldObj, event.player, event.target.getBlockPos());
    }
    
	
}
