package catwalks.block;

import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;

import java.util.*;

/**
 * Planned expansion:
 * 000 - Custom 0
 * 001 - Custom 1
 * 002 - Custom 2
 * 003 - Custom 3
 *
 * 004 - Normal
 * 005 - Nyan
 *
 * 006 - IE Steel
 * 007 - Wood
 *
 * 008 - Oak
 * 009 - Spruce
 * 010 - Birch
 * 011 - Jungle
 * 012 - Acacia
 * 013 - Dark Oak
 *
 * 014 - Glass
 *
 * 015 - Livingrock
 * 016 - Livingwood
 * 017 - Managlass
 * 018 - Alfglass
 *
 * 019 - N/A
 * 020 - N/A
 * 021 - N/A
 * 022 - N/A
 * 023 - N/A
 * 024 - N/A
 * 025 - N/A
 * 026 - N/A
 * 027 - N/A
 * 028 - N/A
 * 029 - N/A
 * 030 - N/A
 * 031 - N/A
 * 032 - N/A
 * 033 - N/A
 * 034 - N/A
 * 035 - N/A
 * 036 - N/A
 * 037 - N/A
 * 038 - N/A
 * 039 - N/A
 * 040 - N/A
 * 041 - N/A
 * 042 - N/A
 * 043 - N/A
 * 045 - N/A
 * 046 - N/A
 * 047 - N/A
 * 048 - N/A
 * 049 - N/A
 * 050 - N/A
 * 051 - N/A
 * 052 - N/A
 * 053 - N/A
 * 054 - N/A
 * 055 - N/A
 * 056 - N/A
 * 057 - N/A
 * 058 - N/A
 * 059 - N/A
 * 060 - N/A
 * 061 - N/A
 * 062 - N/A
 * 063 - N/A
 * 064 - N/A
 * 065 - N/A
 * 066 - N/A
 * 067 - N/A
 * 068 - N/A
 * 069 - N/A
 * 070 - N/A
 * 071 - N/A
 * 072 - N/A
 * 073 - N/A
 * 074 - N/A
 * 075 - N/A
 * 076 - N/A
 * 077 - N/A
 * 078 - N/A
 * 079 - N/A
 * 080 - N/A
 * 081 - N/A
 * 082 - N/A
 * 083 - N/A
 * 084 - N/A
 * 085 - N/A
 * 086 - N/A
 * 087 - N/A
 * 088 - N/A
 * 089 - N/A
 * 090 - N/A
 * 091 - N/A
 * 092 - N/A
 * 093 - N/A
 * 094 - N/A
 * 095 - N/A
 * 096 - N/A
 * 097 - N/A
 * 098 - N/A
 * 099 - N/A
 * 100 - N/A
 * 101 - N/A
 * 102 - N/A
 * 103 - N/A
 * 104 - N/A
 * 105 - N/A
 * 106 - N/A
 * 107 - N/A
 * 108 - N/A
 * 109 - N/A
 * 110 - N/A
 * 111 - N/A
 * 112 - N/A
 * 113 - N/A
 * 114 - N/A
 * 115 - N/A
 * 116 - N/A
 * 117 - N/A
 * 118 - N/A
 * 119 - N/A
 * 120 - N/A
 * 121 - N/A
 * 122 - N/A
 * 123 - N/A
 * 124 - N/A
 * 125 - N/A
 * 126 - N/A
 * 127 - N/A
 *
 */
public enum EnumCatwalkMaterial implements IStringSerializable {
	CUSTOM(BlockRenderLayer.CUTOUT),
	STEEL(BlockRenderLayer.CUTOUT),
	IESTEEL(BlockRenderLayer.CUTOUT),
	WOOD(BlockRenderLayer.CUTOUT);

	public final BlockRenderLayer LAYER;
	public final List<EnumDecoration> DECORATIONS;
	
	EnumCatwalkMaterial(BlockRenderLayer layer, EnumDecoration... decorations) {
		LAYER = layer;
		DECORATIONS = Arrays.asList(decorations);
	}
	
	public int getID(EnumDecoration decor) {
		return DECORATIONS.indexOf(decor);
	}
	
	public EnumDecoration getDecor(int id) {
		return DECORATIONS.get(id);
	}
	
	@Override
	public String getName() {
		return this.name().toLowerCase();
	}
}