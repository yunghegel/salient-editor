#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

attribute vec4 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projTrans;
uniform mat4 u_worldTrans;
varying vec2 v_texCoord0;

varying vec2 v_texCoords;

void main()
{
    v_texCoord0 = a_texCoord0;
    gl_Position =  u_projTrans *u_worldTrans* a_position;
}