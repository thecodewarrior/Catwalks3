package catwalks.node;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import catwalks.CatwalksMod;
import catwalks.Const;
import catwalks.network.NetworkHandler;
import catwalks.network.messages.PacketClientPortConnection;
import catwalks.network.messages.PacketNodeConnect;
import catwalks.network.messages.PacketNodeSettingsQuery;
import catwalks.network.messages.PacketUpdateNode;
import catwalks.network.messages.PacketUpdatePort;
import catwalks.node.NodeUtil.EnumNodes;
import catwalks.node.net.InputPort;
import catwalks.node.net.OutputPort;
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
import io.netty.buffer.ByteBuf;

public class EntityNodeBase extends Entity implements IEntityAdditionalSpawnData {

	public static double SIZE = 0.25;
	public int destroyTimer;
	
	protected boolean isPosDirty = false;
	protected NodeBase node;
	protected EnumNodes nodeType;
	
	public EntityNodeBase(World worldIn) {
		super(worldIn);
	}
	
	public EntityNodeBase(World worldIn, double x, double y, double z, EnumNodes node) {
		this(worldIn);
		setPosition(x, y, z);
		nodeType = node;
		this.node = nodeType.create(this);
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
		
		if(!worldObj.isRemote) {
			int i = 0;
			for(OutputPort<?> port : node.outputs()) {
				if(port.isModified()) {
					PacketBuffer buf = NetworkHandler.createBuffer();
					port.writeValueToBuf(buf);
					firePacket(new PacketUpdatePort(this, true, i, buf));
					if( port.updateConnected(this.worldObj) )
						firePacket(new PacketClientPortConnection(this.getEntityId(), i, port.connectedPoints()));
					port.resetModified();
				}
				i++;
			}
			for(InputPort<?> port : node.inputs()) {
				if(port.isModified()) {
					PacketBuffer buf = NetworkHandler.createBuffer();
					port.writeToBuf(buf);
					firePacket(new PacketUpdatePort(this, false, i, buf));
					port.resetModified();
				}
				i++;
			}
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

	public boolean clientRightClick(EntityPlayer player, int hit) {
		if(hit == Const.NODE.CONNECT_POINT) {
			CatwalksMod.proxy.setConnectingIndex(0);
			return true;
		}
		if(hit == 0 && CatwalksMod.proxy.getConnectingIndex() >= 0 && CatwalksMod.proxy.getSelectedNode() != null) {
			int index = CatwalksMod.proxy.getConnectingIndex();
			CatwalksMod.proxy.setConnectingIndex(-1);
			NetworkHandler.network.sendToServer(new PacketNodeConnect(CatwalksMod.proxy.getSelectedNode().getEntityId(), index, this.getEntityId(), 0));
			return true;
		}
		if(hit == 0 && CatwalksMod.proxy.getSelectedNode() == this) {
			NetworkHandler.network.sendToServer(new PacketNodeSettingsQuery(this.getEntityId()));
		}
		return false;
	}
	
	public boolean clientLeftClick(EntityPlayer player, int hit) {
		return false;
	}
	
	public void onLeftClick(EntityPlayer player, int hit) {
		if(destroyTimer > 0 && destroyTimer < 9) {
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
		
		if(this.rotationPitch < -90) {
			float diff = this.rotationPitch+90;
			this.rotationPitch -= 2* diff;
			this.rotationYaw += 180;
		}
		if(this.rotationPitch > 90) {
			float diff = this.rotationPitch-90;
			this.rotationPitch -= 2* diff;
			this.rotationYaw += 180;
		}
		
		this.rotationYaw = this.rotationYaw % 360;
		
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
		
		ITraceResult<Integer> result = box.trace(rotStart, rotEnd, player);
		ITraceResult<Integer> other = null;
		if(true) {
			other = RayTraceUtil.min(
				RayTraceUtil.trace(rotStart, rotEnd, baseHits(), player),
				null
			);
			
		}
		result = RayTraceUtil.min(result, other);
		
		if(Double.isInfinite(result.hitDistance())) {
//			Logs.debug("FAIL: %.2f, %.2f, %.2f -> %.2f, %.2f, %.2f", rotStart.xCoord, rotStart.yCoord, rotStart.zCoord, rotEnd.xCoord, rotEnd.yCoord, rotEnd.zCoord);
			return (ITraceResult<NodeHit>) RayTraceUtil.MISS_RESULT;
		}
		
		Vec3d adjustedHit = matrix.apply(result.hitPoint()).addVector(posX, posY, posZ);
//		Logs.debug("SUCC: %.2f, %.2f, %.2f -> %.2f, %.2f, %.2f", rotStart.xCoord, rotStart.yCoord, rotStart.zCoord, rotEnd.xCoord, rotEnd.yCoord, rotEnd.zCoord);
		return new SimpleTraceResult<NodeHit>(start, adjustedHit, new NodeHit(this, result.data()));
	}
	
	List<NodeTraceable> traces;
	NodeTraceable box;
	
	public List<NodeTraceable> baseHits() {
		initTraces();
		return traces;
	}
	
	public void initTraces() {
		double d = 0.25, D = 0.5;

		traces = new ArrayList<>();
		
		double s = SIZE/2;
		box = new NodeTraceable(0,
			new Box(
				new Vec3d(-s, -s, -s),
				new Vec3d( s,  s,  s)
			), TexCoords.NULL
		);
		
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
		
		traces.add(new NodeTraceable(Const.NODE.CONNECT_POINT,
			new Quad(
				new Vec3d(0, -d/4, SIZE),
				new Vec3d(0,  d/4, SIZE),
				new Vec3d(0,  d/4, SIZE+(3*d/2)),
				new Vec3d(0, -d/4, SIZE+(3*d/2))
			), new TexCoords(64,
				15, 38,
				15, 43,
				 0, 43,
				 0, 38
			)
		));
		
		traces.add(new NodeTraceable(Const.NODE.CONNECT_POINT,
			new Quad(
				new Vec3d(-d/4, 0, SIZE),
				new Vec3d( d/4, 0, SIZE),
				new Vec3d( d/4, 0, SIZE+(3*d/2)),
				new Vec3d(-d/4, 0, SIZE+(3*d/2))
			), new TexCoords(64,
				15, 38,
				15, 43,
				 0, 43,
				 0, 38
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
		this.ignoreFrustumCheck = true;
//		this.node = new NodeParticleEmitter(this);
	}
	
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks) {
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
			firePacket(new PacketUpdateNode(this));
		}
	}
	
	public void firePacket(IMessage message) {
		NetworkHandler.network.sendToAllAround(message, new TargetPoint(worldObj.provider.getDimension(), posX, posY, posZ, 32));
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		nodeType = EnumNodes.values()[ compound.getInteger("nodeID") ];
		if(node == null)
			node = nodeType.create(this);
		node.readFromNBT(compound.getCompoundTag("node"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		NBTTagCompound nodeTag = new NBTTagCompound();
		node.saveToNBT(nodeTag);
		
		compound.setInteger("nodeID", nodeType.ordinal());
		compound.setTag("node", nodeTag);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(nodeType.ordinal());
		node.writeSpawnData(buffer);
	}

	@Override
	public void readSpawnData(ByteBuf additionalData) {
		nodeType = EnumNodes.values()[ additionalData.readInt() ];
		node = nodeType.create(this);
		node.readSpawnData(additionalData);
	}

}
