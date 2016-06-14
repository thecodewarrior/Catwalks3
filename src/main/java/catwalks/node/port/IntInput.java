package catwalks.node.port;

import catwalks.node.net.InputPort;
import io.netty.buffer.ByteBuf;

public class IntInput extends InputPort<Integer> {

	public IntInput(int value) {
		super(Integer.class, value);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		setValue(buf.readInt());
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(getValue());
	}

}
