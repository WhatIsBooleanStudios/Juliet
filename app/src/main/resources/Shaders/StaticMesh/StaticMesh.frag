#version 410 core
out vec4 fragColor;
in vec2 texCoords;
in vec3 worldPos;
in vec3 normal;

// material parameters
uniform sampler2D diffuseTexture;
uniform vec3 diffuseColor;
uniform float metallic;
uniform float roughness;

#define MAX_POINT_LIGHTS 128
#define MAX_SPOTLIGHTS 128

struct PointLight {
    vec3 position;
    float padding0;
    vec3 color;
    float padding1;
};

struct SpotLight {
    vec4 position; // w component is the cutoff angle
    vec3 direction;
    float padding0;
    vec3 color;
    float padding1;
};

layout(std140) uniform PointLights {
    PointLight pointLights[MAX_POINT_LIGHTS];
};
uniform int numPointLights;

layout(std140) uniform SpotLights {
    SpotLight spotLights[MAX_SPOTLIGHTS];
};
uniform int numSpotLights;

uniform vec3 camPos;

const float PI = 3.14159265359;
// ----------------------------------------------------------------------------
float distributionGGX(vec3 N, vec3 H, float roughness) {
    float a = roughness*roughness;
    float a2 = a*a;
    float NdotH = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float nom   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float geometrySchlickGGX(float NdotV, float roughness) {
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float nom   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return nom / denom;
}
// ----------------------------------------------------------------------------
float geometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2 = geometrySchlickGGX(NdotV, roughness);
    float ggx1 = geometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}
// ----------------------------------------------------------------------------
vec3 fresnelSchlick(float cosTheta, vec3 F0) {
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}
// ----------------------------------------------------------------------------

// ----------------------------------------------------------------------------
vec3 computeRadiance(vec3 N, vec3 V, vec3 F0, vec3 diffuse, vec3 position, vec3 color) {
    // calculate per-light radiance
    vec3 L = normalize(position - worldPos);
    vec3 H = normalize(V + L);
    float distance = length(position - worldPos);
    float attenuation = 1.0 / (distance * distance);
    vec3 radiance = color * attenuation;

    // Cook-Torrance BRDF
    float NDF = distributionGGX(N, H, roughness);
    float G   = geometrySmith(N, V, L, roughness);
    vec3 F    = fresnelSchlick(clamp(dot(H, V), 0.0, 1.0), F0);

    vec3 numerator    = NDF * G * F;
    float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001; // + 0.0001 to prevent divide by zero
    vec3 specular = numerator / denominator;

    // kS is equal to Fresnel
    vec3 kS = F;
    // for energy conservation, the diffuse and specular light can't
    // be above 1.0 (unless the surface emits light); to preserve this
    // relationship the diffuse component (kD) should equal 1.0 - kS.
    vec3 kD = vec3(1.0) - kS;
    // multiply kD by the inverse metalness such that only non-metals
    // have diffuse lighting, or a linear blend if partly metal (pure metals
    // have no diffuse light).
    kD *= 1.0 - metallic;

    // scale light by NdotL
    float NdotL = max(dot(N, L), 0.0);

    // add to outgoing radiance Lo
    return (kD * diffuse / PI + specular) * radiance * NdotL;  // note that we already multiplied the BRDF by the Fresnel (kS) so we won't multiply by kS again
}
// ----------------------------------------------------------------------------
float map(float value, float min1, float max1, float min2, float max2) {
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}
// ----------------------------------------------------------------------------

void main() {
    vec3 N = normalize(normal);
    vec3 V = normalize(camPos - worldPos);

    // check if diffuse color is negative which means that there is a diffuse texture to sample from
    vec3 diffuse = diffuseColor;
    if(diffuse.r < 0) {
        diffuse = texture(diffuseTexture, texCoords).rgb;
    }
    // calculate reflectance at normal incidence; if dia-electric (like plastic) use F0 
    // of 0.04 and if it's a metal, use the diffuse color as F0 (metallic workflow)    
    vec3 F0 = vec3(0.04);
    F0 = mix(F0, diffuse, metallic);

    // reflectance equation
    vec3 Lo = vec3(0.0);

    for(int i = 0; i < numPointLights; i++) {
        Lo += computeRadiance(N, V, F0, diffuse, pointLights[i].position, pointLights[i].color);
    }

    for(int i = 0; i < numSpotLights; i++) {
        vec3 lightDir = normalize(spotLights[i].position.xyz - worldPos);
        float theta = dot(lightDir, normalize(-spotLights[i].direction));
        if(theta > spotLights[i].position.w) {
            Lo += map(theta, spotLights[i].position.w, 1.0f, 0.0f, 1.0f) * computeRadiance(N, V, F0, diffuse, spotLights[i].position.xyz, spotLights[i].color);
        }
    }

    // ambient lighting (note that the next IBL tutorial will replace 
    // this ambient lighting with environment lighting).
    vec3 ambient = vec3(0.03) * diffuse;

    vec3 color = ambient + Lo;

    // HDR tonemapping
    color = color / (color + vec3(1.0));
    // gamma correct
    color = pow(color, vec3(1.0/2.2));

    fragColor = vec4(color, 1.0);
}
