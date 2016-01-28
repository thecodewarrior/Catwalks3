package catwalks.block.extended;

import io.netty.buffer.ByteBuf;

public abstract class ExtendedData {

	public abstract void write(ByteBuf buf);
	public abstract void read(ByteBuf buf);
	
}
