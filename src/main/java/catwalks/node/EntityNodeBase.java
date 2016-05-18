package catwalks.node;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import catwalks.CatwalksMod;
import catwalks.Const;
import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketUpdateNode;
import catwalks.raytrace.CustomAABBCollide;
import catwalks.raytrace.RayTraceUtil;
import catwalks.raytrace.RayTraceUtil.ITraceResult;
import catwalks.raytrace.RayTraceUtil.ITraceable;
import catwalks.raytrace.RayTraceUtil.SimpleTraceResult;
import catwalks.raytrace.node.NodeHit;
import catwalks.raytrace.node.NodeTraceable;
import catwalks.raytrace.primitives.Box;
import catwalks.raytrace.primitives.Quad;
import catwalks.raytrace.primitives.TexCoords;
import catwalks.register.ItemRegister;
import catwalks.shade.ccl.vec.Matrix4;
import catwalks.shade.ccl.vec.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityNodeBase extends Entity {

	public static double SIZE = 0.25;
	public int destroyTimer;
	
	protected boolean isPosDirty = false;
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
		if(destroyTimer > 0)
			destroyTimer--;
		if(this.worldObj.isRemote) {
			node.clientTick();
		} else {
			node.serverTick();
		}
	}
	
	@Override
	public void onEntityUpdate() {
		// NOOP
	}
	
	@Override
	public void onChunkLoad() {
		node.onLoad();
	}
	
	public NodeBase getNode() {
		return node;
	}

	public void onLeftClick(EntityPlayer player, int hit) {
		if(destroyTimer > 0) {
			this.kill();
		} else {
			destroyTimer = 10;
			sendNodeUpdate();
		}
	}
	
	public void onRightClick(EntityPlayer player, int hit) {
		if(worldObj.isRemote) {
			return;
		}
		
		if(hit > 0) {
//			node.onRightClick(player, hit);
			return;
		}
		if(hit == 0)
			return;
		
		if(hit == Const.NODE.PITCH_PLUS) {
			this.rotationPitch -= 5;
		}
		if(hit == Const.NODE.PITCH_MINUS) {
			this.rotationPitch += 5;
		}
		if(hit == Const.NODE.YAW_PLUS) {
			this.rotationYaw -= 5;
		}
		if(hit == Const.NODE.YAW_MINUS) {
			this.rotationYaw += 5;
		}
		
		sendNodeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	public ITraceResult<NodeHit> rayTraceNode(@Nullable EntityPlayer player, Vec3d start, Vec3d end) {
		if(player != null) {
			if(!(
				( player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() == ItemRegister.nodeManipulator ) ||
				( player.getHeldItemOffhand() != null && player.getHeldItemOffhand().getItem() == ItemRegister.nodeManipulator )
			)) {
				return (ITraceResult<NodeHit>) RayTraceUtil.MISS_RESULT;
			}
		}
		
		Vec3d relStart = start.subtract(posX, posY, posZ);
		Vec3d relEnd   =   end.subtract(posX, posY, posZ);
		
		Matrix4 matrix = new Matrix4();
		matrix.rotate(Math.toRadians( this.rotationYaw ),   new Vector3(0, -1, 0));
		matrix.rotate(Math.toRadians( this.rotationPitch ), new Vector3(1, 0, 0));
		
		Matrix4 derotate = new Matrix4();
		derotate.rotate(-Math.toRadians( this.rotationPitch ), new Vector3(1, 0, 0));
		derotate.rotate(-Math.toRadians( this.rotationYaw ),   new Vector3(0, -1, 0));
		
		Vec3d rotStart = derotate.apply(relStart);
		Vec3d rotEnd = derotate.apply(relEnd);
		
		ITraceResult<Integer> result = RayTraceUtil.min(
			RayTraceUtil.trace(rotStart, rotEnd, baseHits(), player),
			null
		);
		
		if(Double.isInfinite(result.hitDistance()))
			return (ITraceResult<NodeHit>) RayTraceUtil.MISS_RESULT;
		
		Vec3d adjustedHit = matrix.apply(result.hitPoint()).addVector(posX, posY, posZ); //.rotatePitch(this.rotationPitch).rotateYaw(this.rotationYaw).addVector(posX, posY, posZ);
		
		return new SimpleTraceResult<NodeHit>(start, adjustedHit, new NodeHit(this, result.data()));
	}
	
	List<NodeTraceable> traces;
	
	public List<NodeTraceable> baseHits() {
		initTraces();
		return traces;
	}
	
	public void initTraces() {
		double d = 0.25, D = 0.5;

		traces = new ArrayList<>();
		
		double s = SIZE/2;
		traces.add(new NodeTraceable(0,
			new Box(
				new Vec3d(-s, -s, -s),
				new Vec3d( s,  s,  s)
			), TexCoords.NULL
		));
		
		traces.add(new NodeTraceable(Const.NODE.PITCH_PLUS,
			new Quad(
				new Vec3d(0, d, d),
				new Vec3d(0, D, d),
				new Vec3d(0, D, D),
				new Vec3d(0, d, D)
			), new TexCoords(64,
				18, 16,
				18, 0,
				0,  0,
				0,  16
			)
		));
		
		traces.add(new NodeTraceable(Const.NODE.PITCH_MINUS,
			new Quad(
				new Vec3d(0, -D, d),
				new Vec3d(0, -d, d),
				new Vec3d(0, -d, D),
				new Vec3d(0, -D, D)
			), new TexCoords(64,
				18, 0,
				18, 16,
				0,  16,
				0,  0
			)
		));
		
		traces.add(new NodeTraceable(Const.NODE.YAW_PLUS,
			new Quad(
				new Vec3d(d, 0, d),
				new Vec3d(D, 0, d),
				new Vec3d(D, 0, D),
				new Vec3d(d, 0, D)
			), new TexCoords(64,
				18, 16,
				18, 0,
				0,  0,
				0,  16
			)
		));
		
		traces.add(new NodeTraceable(Const.NODE.YAW_MINUS,
			new Quad(
				new Vec3d(-D, 0, d),
				new Vec3d(-d, 0, d),
				new Vec3d(-d, 0, D),
				new Vec3d(-D, 0, D)
			), new TexCoords(64,
				18, 0,
				18, 16,
				0,  16,
				0,  0
			)
		));
			
	}
	
	@Override
	public AxisAlignedBB getEntityBoundingBox() {
		return super.getEntityBoundingBox();
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return true;
	}
	
	@Override
	public void setEntityBoundingBox(AxisAlignedBB bb) {
		final EntityNodeBase self = this;
		super.setEntityBoundingBox(new CustomAABBCollide(bb,
			new ITraceable<Object, NodeHit>() {

				@Override
				public ITraceResult<NodeHit> trace(Vec3d start, Vec3d end, Object param) {
					return self.rayTraceNode(CatwalksMod.proxy.getPlayerLooking(start, end), start, end);
				}
				
			}
		));
	}
	
	@Override
	protected void entityInit() {
		setEntityInvulnerable(true);
		setSize(0, 0);
		initTraces();
		this.node = new NodeParticleEmitter(this);
	}
	
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks) {
//		int i = this.getLightFromNeighborsFor(EnumSkyBlock.SKY, pos);
//        int j = this.getLightFromNeighborsFor(EnumSkyBlock.BLOCK, pos);

        return 15 << 20 | 15 << 4;
	}
    public float getBrightness(float partialTicks) { return 15; }
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double dist = 32;
        return distance < dist*dist;
    }

	public ITextComponent getNodeName() {
		return new TextComponentString("ASDF");
	}
	
	public void sendNodeUpdate() {
		if(!worldObj.isRemote) {
			NetworkHandler.network.sendToAllAround(new PacketUpdateNode(this), new TargetPoint(worldObj.provider.getDimension(), posX, posY, posZ, 32));
		}
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		
	}

}
