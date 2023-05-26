package eu.midnightdust.customsplashscreen.texture;

import eu.midnightdust.customsplashscreen.CustomSplashScreenClient;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

public class ConfigTexture extends ResourceTexture {
    public static int randomBackgroundId;
    public static int prevBackgroundLength;
    // Load textures from the config directory //
    public boolean shouldBlur = false;

    public ConfigTexture(Identifier location) {
        super(location);
    }

    protected TextureData loadTextureData(ResourceManager resourceManager) {
        try {
            InputStream input = new FileInputStream(CustomSplashScreenClient.CONFIG_PATH+"/"+this.location.getPath());
            if (this.location.getPath().equals("background.png") && CustomSplashScreenClient.CONFIG_PATH.toPath().resolve("backgrounds").toFile().isDirectory()) {
                if (CustomSplashScreenClient.CONFIG_PATH.toPath().resolve("backgrounds").toFile().listFiles() != null) {
                    File[] backgrounds = Arrays.stream(Objects.requireNonNull(CustomSplashScreenClient.CONFIG_PATH.toPath().resolve("backgrounds").toFile().listFiles())).filter(file -> file.toString().endsWith(".png") || file.toString().endsWith(".jpg") || file.toString().endsWith(".jpeg")).toList().toArray(new File[0]);
                    if (backgrounds.length > 0) {
                        if (ConfigTexture.randomBackgroundId == -1 || ConfigTexture.prevBackgroundLength != backgrounds.length) ConfigTexture.randomBackgroundId = Random.create().nextInt(backgrounds.length);
                        input = new FileInputStream(backgrounds[ConfigTexture.randomBackgroundId]);
                        ConfigTexture.prevBackgroundLength = backgrounds.length;
                    }
                }
            }

            TextureData texture;

            try {
                texture = new TextureData(new TextureResourceMetadata(shouldBlur, true), NativeImage.read(input));
            } finally {
                input.close();
            }

            return texture;
        } catch (IOException var18) {
            return new TextureData(var18);
        }
    }

}
