package catwalks.node;

import java.util.List;

import javax.annotation.Nullable;

import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.RayTraceUtil.ITraceable;
import catwalks.raytrace.node.NodeHit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class EntityNodeBase extends Entity {

	public static double SIZE = 0.25;
	
	protected NodeBase node;
	
	public EntityNodeBase(World worldIn) {
		super(worldIn);
	}
	
	public EntityNodeBase(World worldIn, double x, double y, double z) {
		this(worldIn);
		setPosition(x, y, z);
	}

	@Override
	public void onUpdate() {
		node.onTick();
	}
	
	@Override
	public void onEntityUpdate() {
		// NOOP
	}
	
	@Override
	public void onChunkLoad() {
		node.onLoad();
	}
	
	public ITraceResult<NodeHit> rayTraceNode(@Nullable EntityPlayer player, Vec3d start, Vec3d end) {
		return null;
	}
	
	public List<ITraceable<EntityPlayer, NodeHit>> baseHits() {
		
	}
	
	@Override
	protected void entityInit() {
		setEntityInvulnerable(true);
		setSize((float)SIZE, (float)SIZE);
		this.node = new NodeParticleEmitter(this);
	}

	public ITextComponent getNodeName() {
		return new TextComponentString("ASDF");
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		
	}

}
