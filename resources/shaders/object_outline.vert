attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_color;
uniform mat4 u_projTrans;
varying vec2 v_texCoords;
varying vec4 v_fragmentColor;

void main()
{
    v_fragmentColor = a_color;
    v_texCoords = a_texCoord0;
    gl_Position =  u_projTrans * a_position;
}