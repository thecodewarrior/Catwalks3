package catwalks;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// Custom texture class to handle the ore generation
@SideOnly(Side.CLIENT)
public class TextureOverlay extends TextureAtlasSprite {

    public String mod, prefix, base;
    public String[] decorations;
    public int type;
    public BufferedImage output_image = null;
    private int n;
    private ResourceLocation textureLocation;
    private int renderType = 0;

    public TextureOverlay(String mod, String prefix, String base, String... decorations ) {
        super(mod + ":" + prefix + base);
        this.mod = mod; this.prefix = prefix; this.base = base; this.decorations = decorations;
    }
    
    private static int[] composite(int w, int h, int[] bg, int[] fg) {
    	int[] new_data = new int[bg.length];
    	
    	for (int i = 0; i < new_data.length; i++) {
    		new_data[i] = composite(bg[i], fg[i]);
		}
    	
    	return new_data;
    }
    
    private static int composite(int bg, int fg) {
    	
    	double fg_alpha = getAlpha(fg)/255.0;
    	int fg_red   = (int)(   getRed(fg)*fg_alpha );
    	int fg_blue  = (int)(  getBlue(fg)*fg_alpha );
    	int fg_green = (int)( getGreen(fg)*fg_alpha );
    	
    	double bg_alpha = getAlpha(bg)/255.0;
    	int bg_red   = (int)(   getRed(bg)*bg_alpha );
    	int bg_blue  = (int)(  getBlue(bg)*bg_alpha );
    	int bg_green = (int)( getGreen(bg)*bg_alpha );
    	
    	int new_alpha = (int)( fg_alpha + ( bg_alpha * ( 1 - fg_alpha ) ) );
    	int new_red   = (int)( fg_red + ( bg_red * ( 1 - fg_alpha ) ) );
    	int new_blue  = (int)( fg_blue + ( bg_blue * ( 1 - fg_alpha ) ) );
    	int new_green = (int)( fg_green + ( bg_green * ( 1 - fg_alpha ) ) );
    	
    	return makeCol(new_red, new_green, new_blue, new_alpha);
    }
    
    private static int[] createDenseTexture(int w, int h, int[] ore_data, int[][] overlays, int renderType) {
    	
    	int[] new_data = ore_data.clone();
    	
    	for (int i = 0; i < overlays.length; i++) {
			new_data = composite(w, h, new_data, overlays[i]);
		}
    	
        return new_data;
    }

    // loads the textures
    // note: the documentation

    public static int getAlpha(int col) {
        return (col & 0xff000000) >> 24;
    }

    public static int getRed(int col) {
        return (col & 0x00ff0000) >> 16;
    }

    public static int getGreen(int col) {
        return (col & 0x0000ff00) >> 8;
    }

    public static int getBlue(int col) {
        return col & 0x000000ff;
    }

    public static int makeCol(int red, int green, int blue, int alpha) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    // should we use a custom loader to get our texture?
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
    	if(1-1==0) return false;
        ResourceLocation location1 = new ResourceLocation(location.getResourceDomain(), String.format("%s/%s%s", new Object[]{"textures", location.getResourcePath(), ".png"}));
        try {
            // check to see if the resource can be loaded (someone added an
            // override)
            manager.getResource(location1);
//            LogHelper.info("Dense Ores: Detected override for " + name);
            return false;
        } catch (IOException e) {
            // file not found: let's generate one
            return true;
        }
    }

    /**
     * Load the specified resource as this sprite's data. Returning false from
     * this function will prevent this icon from being stitched onto the master
     * texture.
     *
     * @param manager  Main resource manager
     * @param location File resource location
     * @return False to prevent this Icon from being stitched
     */
    // is not correct - return TRUE to prevent this Icon from being stitched
    // (makes no sense but... whatever)

    // this code is based on code from TextureMap.loadTextureAtlas, only with
    // the
    // code for custom mip-mapping textures and animation removed.
    // TODO: add animation support
    public boolean load(IResourceManager manager, ResourceLocation location) {
    	
    	
    	
//        // get mipmapping level
//        int mp = Minecraft.getMinecraft().gameSettings.mipmapLevels;
//
//        // creates a buffer that will be used for our texture and the
//        // various mip-maps
//        // (mip-mapping is where you use smaller textures when objects are
//        // far-away
//        // see: http://en.wikipedia.org/wiki/Mipmap)
//        // these will be generated from the base texture by Minecraft
//        BufferedImage[] ore_image = new BufferedImage[1 + mp];
//
//        BufferedImage stone_image;
//        int w;
//
//        AnimationMetadataSection animation;
//
//        try {
//            IResource iresource = manager.getResource(getBlockResource(name));
//            IResource iresourceBase = manager.getResource(getBlockResource(base));
//
//            // load the ore texture
//            ore_image[0] = ImageIO.read(iresource.getInputStream());
//
//            // load animation
//            animation = (AnimationMetadataSection) iresource.getMetadata("animation");
//
//            // load the stone texture
//            stone_image = ImageIO.read(iresourceBase.getInputStream());
//
//            w = ore_image[0].getWidth();
//
//            if (stone_image.getWidth() != w) {
//                List<IResource> resourcePacks = manager.getAllResources(getBlockResource(base));
//                for (int i = resourcePacks.size() - 1; i >= 0; --i) {
//                    IResource resource = (IResource) resourcePacks.get(i);
//                    stone_image = ImageIO.read(resource.getInputStream());
//
//                    if (stone_image.getWidth() == w)
//                        break;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return true;
//        }
//
//        if (stone_image.getWidth() != w) {
////            LogHelper.error("Error generating texture" + name + ". Unable to find base texture with same size.");
//            return true;
//        }
//
//        int h = ore_image[0].getHeight();
//
//        // create an ARGB output image that will be used as our texture
//        output_image = new BufferedImage(w, h, 2);
//
//        // create some arrays t hold the pixel data
//        // pixel data is in the form 0xaarrggbb
//        int[] ore_data = new int[w * w];
//        int[] stone_data = new int[w * w];
//
//        stone_image.getRGB(0, 0, w, w, stone_data, 0, w);
//
//        for (int y = 0; y < h; y += w) {
//            // read the ARGB color data into our arrays
//            ore_image[0].getRGB(0, y, w, w, ore_data, 0, w);
//
//            // generate our new texture
//            int[] new_data = createDenseTexture(w, ore_data, stone_data, renderType);
//
//            // write the new image data to the output image buffer
//            output_image.setRGB(0, y, w, w, new_data, 0, w);
//        }
//
//        // replace the old texture
//        ore_image[0] = output_image;
//
//        // load the texture
//        try {
//			this.loadSprite(ore_image, animation);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
////        Log.info("Dense Ores: Succesfully generated dense ore texture for '" + name + "' with background '" + base + "'. Place " + name + "_dense.png in the assets folder to override.");
        return false;
    }
}