package catwalks.node.port;

import catwalks.node.net.InputPort;
import io.netty.buffer.ByteBuf;

public class BooleanInput extends InputPort<Boolean> {

	public BooleanInput(Boolean value) {
		super(Boolean.class, value);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		setValue(buf.readBoolean());
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeBoolean(getValue());
	}

}
