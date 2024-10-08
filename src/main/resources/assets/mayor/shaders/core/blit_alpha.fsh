#version 150

uniform sampler2D DiffuseSampler;
uniform float Alpha;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    color.a *= Alpha;

    fragColor = color;
}