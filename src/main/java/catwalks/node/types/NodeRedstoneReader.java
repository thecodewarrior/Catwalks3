package catwalks.node.types;

import java.util.List;

import catwalks.node.EntityNodeBase;
import catwalks.node.NodeBase;
import catwalks.node.net.OutputPort;
import catwalks.node.port.BooleanPort;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import scala.actors.threadpool.Arrays;

public class NodeRedstoneReader extends NodeBase {
	
	public BooleanPort value;
	
	public NodeRedstoneReader(EntityNodeBase entity) {
		super(entity);
		value = new BooleanPort(false, this);
	}
	
	@Override
	public List<OutputPort> outputs() {
		return Arrays.asList(new OutputPort[] { value });
	}
	
	@Override
	public int getColor() {
		if(value.getValue()) {
			return 0xFF0000;
		} else {
			return 0x7F0000;
		}
	}
	
	@Override
	public void serverTick() {
		Vec3d pos = entity.getPositionVector().subtract( entity.getLook(1).scale(0.125) );
		BlockPos bpos = new BlockPos(pos);
		value.setValue(entity.worldObj.getRedstonePower(bpos, EnumFacing.UP) > 0);
	}
	
}
