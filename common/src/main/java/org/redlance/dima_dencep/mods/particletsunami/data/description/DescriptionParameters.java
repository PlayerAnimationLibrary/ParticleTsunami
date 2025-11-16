package org.redlance.dima_dencep.mods.particletsunami.data.description;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.ResourceLocation;

public class DescriptionParameters {
    public static final Codec<DescriptionParameters> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DescriptionMaterial.CODEC.fieldOf("material").orElse(DescriptionMaterial.PARTICLE_SHEET_TRANSLUCENT).forGetter(DescriptionParameters::material),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(DescriptionParameters::texture)
    ).apply(instance, DescriptionParameters::new));
    private final DescriptionMaterial material;
    private final ResourceLocation texture;

    public DescriptionParameters(DescriptionMaterial material, ResourceLocation texture) {
        this.material = material;
        this.texture = texture;
    }

    public DescriptionMaterial material() {
        return material;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public TextureAtlasSprite getTexture() {
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.PARTICLES).getSprite(this.texture);
    }
}
