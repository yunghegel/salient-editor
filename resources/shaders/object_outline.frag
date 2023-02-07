#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

float offset = 1.0 / 768.0;
varying vec2 v_texCoords;
varying vec4 v_fragmentColor;
uniform sampler2D u_texture;
uniform vec4 u_cameraPos;

varying  vec2 v_texCoords0;
varying  vec2 v_texCoords1;
varying  vec2 v_texCoords2;
varying  vec2 v_texCoords3;
varying  vec2 v_texCoords4;

const float u_depth_min = 0.0;
const float u_depth_max = 0.5;

const vec4 u_outer_color = vec4(0.0, 0.0, 0.0, 0);
const vec4 u_inner_color = vec4(1.0, 1.0, 1.0, 1.0);

void main()
{
    vec4 col = texture2D(u_texture, v_texCoords);
    if (col.a > 0)
    return;
    //gl_FragColor = col;
    else {
        float a = texture2D(u_texture, vec2(v_texCoords.x + offset, v_texCoords.y)).a +
        texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - offset)).a +
        texture2D(u_texture, vec2(v_texCoords.x - offset, v_texCoords.y)).a +
        texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + offset)).a;
        if (col.a < 1.0 && a > 0.0)
        gl_FragColor = vec4(1, 1, 1, 0.8);

    }
}



//2. If the current pixel is not transparent, then just draw it.
//3. If the current pixel is transparent, then get the alpha value of the four pixels around it.
//4. If the current pixel is transparent and the alpha value of the four pixels around it is greater than 0, then draw a black pixel with 80% opacity.
//5. If the current pixel is transparent and the alpha value of the four pixels around it is 0, then draw the current pixel.