package catwalks.node.port;

import catwalks.node.NodeBase;
import catwalks.node.net.OutputPort;
import io.netty.buffer.ByteBuf;

public class BooleanPort extends OutputPort<Boolean> {

	public BooleanPort(boolean value, NodeBase node) {
		super(Boolean.class, value, node);
	}

	@Override
	public int getColor() {
		if(getValue()) {
			return 0xFF0000;
		} else {
			return 0x7F0000;
		}
	}
	
	@Override
	public void readFromBuf(ByteBuf buf) {
		super.readFromBuf(buf);
		setValue( buf.readBoolean() );
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		super.writeToBuf(buf);
		buf.writeBoolean(getValue());
	}

}
