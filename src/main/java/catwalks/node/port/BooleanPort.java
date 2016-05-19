package catwalks.node.port;

import catwalks.node.net.OutputPort;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class BooleanPort extends OutputPort<Boolean> {

	public BooleanPort(boolean value) {
		super(value);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		setValue( buf.readBoolean() );
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeBoolean(getValue());
	}

}
