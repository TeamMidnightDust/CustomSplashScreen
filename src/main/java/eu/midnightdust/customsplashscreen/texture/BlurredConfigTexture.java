package eu.midnightdust.customsplashscreen.texture;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BlurredConfigTexture extends ResourceTexture {
    // Load textures from the config directory //

    public BlurredConfigTexture(Identifier location) {
        super(location);
    }

    protected TextureData loadTextureData(ResourceManager resourceManager) {
        try {
            InputStream input = new FileInputStream(FabricLoader.getInstance().getConfigDir()+"/customsplashscreen/"+this.location.toString().replace("minecraft:",""));
            TextureData texture;

            try {
                texture = new TextureData(new TextureResourceMetadata(true, true), NativeImage.read(input));
            } finally {
                input.close();
            }

            return texture;
        } catch (IOException var18) {
            return new TextureData(var18);
        }
    }

}
