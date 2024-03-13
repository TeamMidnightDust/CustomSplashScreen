package eu.midnightdust.customsplashscreen.texture;

import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;

public class EmptyTexture extends ResourceTexture {
    // Empty texture used for hiding the default mojang logo when using other logo styles //

    public EmptyTexture(Identifier location) {
        super(location);
    }

    protected TextureData loadTextureData(ResourceManager resourceManager) {
        try {
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("empty.png");
            TextureData texture = null;

            if (input != null) {
                try {
                    texture = new TextureData(new TextureResourceMetadata(true, true), NativeImage.read(input));
                } finally {
                    input.close();
                }

            }

            return texture;
        } catch (IOException var18) {
            return new TextureData(var18);
        }
    }
}
