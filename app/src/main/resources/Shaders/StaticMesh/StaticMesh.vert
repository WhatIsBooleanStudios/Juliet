#version 410 core

layout (location = 0) in vec3 vertPos;
layout (location = 1) in vec3 vertNormal;
layout (location = 2) in vec2 vertTexCoords;

layout (std140) uniform Camera {
    mat4 transform;
} camera;

uniform vec3 uTranslation;

out vec2 texCoords;
out vec3 worldPos;
out vec3 normal;

void main() {
    gl_Position = camera.transform * vec4(vertPos + uTranslation, 1.0);
    texCoords = vertTexCoords;
    normal = vertNormal;
    worldPos = vertPos + uTranslation;
}
