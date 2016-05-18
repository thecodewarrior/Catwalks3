package catwalks.raytrace.primitives;

import java.util.ArrayList;
import java.util.List;

public class TexCoords {
	
	public static final TexCoords NULL = new TexCoords(0, 0, 0);
	
	public UV[] uvs;
	
	public TexCoords(int texSize, double... uvs) {
		if(uvs.length < 2)
			throw new IllegalArgumentException("UV list must have at least two elements");
		if(uvs.length % 2 != 0)
			throw new IllegalArgumentException("UV list must have an even number of elements");
		
		List<UV> coords = new ArrayList<>();
		
		for (int i = 0; i < uvs.length; i += 2) {
			coords.add(new UV( uvs[i]/(float)texSize, uvs[i+1]/(float)texSize ));
		}
		
		this.uvs = coords.toArray(new UV[0]);
	}
	
	public static class UV {
		public double u, v;
		public UV(double u, double v) {
			this.u = u;
			this.v = v;
		}
	}
}
