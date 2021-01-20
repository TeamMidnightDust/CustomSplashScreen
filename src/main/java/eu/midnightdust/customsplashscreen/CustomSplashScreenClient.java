package eu.midnightdust.customsplashscreen;

import eu.midnightdust.customsplashscreen.config.CustomSplashScreenConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.TextureHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.data.client.model.Texture;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.*;
import java.util.logging.FileHandler;

public class CustomSplashScreenClient implements ClientModInitializer {

    public static CustomSplashScreenConfig CS_CONFIG;
    public static File CONFIG_PATH = new File(FabricLoader.getInstance().getConfigDir() + "/customsplashscreen");
    private static Path BackgroundTexture = Paths.get(CONFIG_PATH + "/background.png");
    private static Path MojangTexture = Paths.get(CONFIG_PATH + "/mojangstudios.png");
    private static Path MojankTexture = Paths.get(CONFIG_PATH + "/mojank.png");
    private static Path ProgressBarTexture = Paths.get(CONFIG_PATH + "/progressbar.png");
    private static Path ProgressBarBackgroundTexture = Paths.get(CONFIG_PATH + "/progressbar_background.png");

    @Override
    public void onInitializeClient() {
        AutoConfig.register(CustomSplashScreenConfig.class, JanksonConfigSerializer::new);
        CS_CONFIG = AutoConfig.getConfigHolder(CustomSplashScreenConfig.class).getConfig();

        if (!CONFIG_PATH.exists()) { // Run when config directory is nonexistant //
            CONFIG_PATH.mkdir(); // Create our custom config directory //

            // Open Input Streams for copying the default textures to the config directory //
            InputStream background = Thread.currentThread().getContextClassLoader().getResourceAsStream("background.png");
            InputStream mojangstudios = Thread.currentThread().getContextClassLoader().getResourceAsStream("mojangstudios.png");
            InputStream mojank = Thread.currentThread().getContextClassLoader().getResourceAsStream("mojank.png");
            InputStream progressbar = Thread.currentThread().getContextClassLoader().getResourceAsStream("progressbar.png");
            InputStream progressbarBG = Thread.currentThread().getContextClassLoader().getResourceAsStream("progressbar_background.png");
            try {
                // Copy the default textures into the config directory //
                Files.copy(background,BackgroundTexture,StandardCopyOption.REPLACE_EXISTING);
                Files.copy(mojangstudios,MojangTexture,StandardCopyOption.REPLACE_EXISTING);
                Files.copy(mojank,MojankTexture,StandardCopyOption.REPLACE_EXISTING);
                Files.copy(progressbar,ProgressBarTexture,StandardCopyOption.REPLACE_EXISTING);
                Files.copy(progressbarBG,ProgressBarBackgroundTexture,StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
