#version 330 core

layout (location = 0) in vec3 vertPos;
layout (location = 1) in vec2 vertTexCoords;

layout (std140) uniform Camera {
    mat4 transform;
} camera;

out vec2 texCoords;

void main() {
    gl_Position = camera.transform * vec4(vertPos, 1.0);
    texCoords = vertTexCoords;
}
