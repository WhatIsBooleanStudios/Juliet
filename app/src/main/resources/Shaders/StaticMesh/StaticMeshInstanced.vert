#version 410 core

layout (location = 0) in vec3 vertPos;
layout (location = 1) in vec3 vertNormal;
layout (location = 2) in vec2 vertTexCoords;
layout (location = 3) in mat4 transform;

layout (std140) uniform Camera {
    mat4 transform;
} camera;


out vec2 texCoords;
out vec3 worldPos;
out vec3 normal;

void main() {
    gl_Position = camera.transform * transform * vec4(vertPos.xyz, 1.0);
    texCoords = vertTexCoords;
    normal = vertNormal;
    worldPos = (transform * vec4(vertPos, 1.0f)).xyz;
}
