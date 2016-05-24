#version 120

uniform int time;
uniform float thickness;

void main() {
    vec2 uv = vec2(gl_TexCoord[0]) - vec2(0.5, 0.5);
	float len = length(uv);
	
	if(len > 0.5-thickness/2.0 && len < 0.5) {
        float ang = degrees( atan(uv.x, uv.y) )+0.5;
        float lrg = mod( ang, 10.0);
        float sml = mod( ang+5.0, 10.0);
		
        if(len > 0.5-thickness/4.0 && lrg > -1.0 && lrg < 1.0)
            gl_FragColor = vec4(0,0,0,1);
        else if(len > 0.5-thickness/8.0 && sml > -1.0 && sml < 1.0)
            gl_FragColor = vec4(0,0,0,1);
        else
			gl_FragColor = gl_Color;
    } else
        gl_FragColor = vec4(0, 0, 0 ,0);
}