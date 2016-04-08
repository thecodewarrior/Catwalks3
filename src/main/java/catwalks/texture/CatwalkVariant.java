package catwalks.texture;

import catwalks.Const;
import catwalks.block.EnumCatwalkMaterial;

public class CatwalkVariant {

	private boolean tape, lights, vines;
	private EnumCatwalkMaterial material;
	
	public static final int COMBINATIONS = EnumCatwalkMaterial.values().length *2/*tape*/ *2/*lights*/ *2/*vines*/;
	public static final StaticCatwalkVariant[] VARIANTS = new StaticCatwalkVariant[COMBINATIONS];
	public static final StaticCatwalkVariant[] NULLS = new StaticCatwalkVariant[EnumCatwalkMaterial.values().length];
	static {
		boolean[] TF = new boolean[] {true, false};
		int i = 0;
		for (EnumCatwalkMaterial material : EnumCatwalkMaterial.values()) {
			for(boolean tape : TF) {
				for(boolean lights : TF) {
					for(boolean vines : TF) {					
						VARIANTS[i] = new StaticCatwalkVariant(material, tape, lights, vines);
						i++;
					}
				}
			}
			NULLS[material.ordinal()] = new StaticCatwalkVariant(material, false, false, false);
		}
	}
	
	public CatwalkVariant() {}
	
	public CatwalkVariant(EnumCatwalkMaterial material, boolean tape, boolean lights, boolean vines) {
		this.material = material;
		this.tape = tape;
		this.lights = lights;
		this.vines = vines;
	}
	
	public void set(EnumCatwalkMaterial material, boolean tape, boolean lights, boolean vines) {
		this.material = material;
		this.tape = tape;
		this.lights = lights;
		this.vines = vines;
	}
	
	public String getTextureName(String path) {
		return Const.MODID + ":/gen/" + textureKey() + "/" + path;
	}
	
	protected String textureKey() {
		String str = "";
		if(material != null) str += material.getName().toLowerCase() + "_";
		if(tape)   str += 't';
		if(lights) str += 'l';
		if(vines)  str += 'v';
		return str;
	}
	
	public boolean getTape() { return tape; }
	public void setTape(boolean v) { tape = v; }
	
	public boolean getLights() { return lights; }
	public void setLights(boolean v) { lights = v; }
	
	public boolean getVines() { return vines; }
	public void setVines(boolean v) { vines = v; }
	
	public EnumCatwalkMaterial getMaterial() { return material; }
	public void setMaterial(EnumCatwalkMaterial material) { this.material = material; }
	
	public static class StaticCatwalkVariant extends CatwalkVariant {
		public StaticCatwalkVariant(EnumCatwalkMaterial material, boolean tape, boolean lights, boolean vines) {
			super(material, tape, lights, vines);
		}
		@Override
		public void set(EnumCatwalkMaterial material, boolean tape, boolean lights, boolean vines) {}
		@Override
		public void setTape(boolean v) {}
		@Override
		public void setLights(boolean v) {}
		@Override
		public void setVines(boolean v) {}
		@Override
		public void setMaterial(EnumCatwalkMaterial material) {}
	}
	
}
