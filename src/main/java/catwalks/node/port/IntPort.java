package catwalks.node.port;

import catwalks.node.NodeBase;
import catwalks.node.net.OutputPort;
import io.netty.buffer.ByteBuf;

public class IntPort extends OutputPort<Integer> {

	public IntPort(int value, NodeBase node) {
		super(Integer.class, value, node);
	}

	@Override
	public int getColor() {
		if(getValue() > 0) {
			return 0xFF0000;
		} else {
			return 0x7F0000;
		}
	}
	
	@Override
	public void readValueFromBuf(ByteBuf buf) {
		setValue( buf.readInt() );
	}

	@Override
	public void writeValueToBuf(ByteBuf buf) {
		buf.writeInt(getValue());
	}

}
