package catwalks.texture;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;

public class CompositeTexture {
	
	public ResourceLocation name;
	public List<ResourceLocation> layers;
	
	public CompositeTexture(ResourceLocation name, ResourceLocation... layers) {
		this.name = name;
		this.layers = Lists.newArrayList(layers);
	}
	
	public CompositeTexture(ResourceLocation name, List<ResourceLocation> layers) {
		this.name = name;
		this.layers = layers;
	}
	
}
