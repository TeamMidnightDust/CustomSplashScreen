package eu.midnightdust.customsplashscreen;

import eu.midnightdust.customsplashscreen.config.CustomSplashScreenConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.*;

public class CustomSplashScreenClient implements ClientModInitializer {
    public static File CONFIG_PATH = new File(FabricLoader.getInstance().getConfigDir() + "/customsplashscreen");
    public static final Path BackgroundTexture = Paths.get(CONFIG_PATH + "/background.png");
    public static final Path WideLogoTexture = Paths.get(CONFIG_PATH + "/wide_logo.png");
    public static final Path SquareLogoTexture = Paths.get(CONFIG_PATH + "/square_logo.png");
    public static final Path ProgressBarTexture = Paths.get(CONFIG_PATH + "/progressbar.png");
    public static final Path ProgressBarBackgroundTexture = Paths.get(CONFIG_PATH + "/progressbar_background.png");
    private static float process = 0;

    private static void tick() {
        process = (process + 0.24f * CustomSplashScreenConfig.spinningCircleSpeed) % 24;
    }

    public static int getProcess(){
        return (int) (process-1);
    }

    @Override
    public void onInitializeClient() {
        CustomSplashScreenConfig.init("customsplashscreen", CustomSplashScreenConfig.class);

        if (!CONFIG_PATH.exists()) { // Run when config directory is nonexistant //
            CONFIG_PATH.mkdir(); // Create our custom config directory //
        }
        // Open Input Streams for copying the default textures to the config directory //
        InputStream background = Thread.currentThread().getContextClassLoader().getResourceAsStream("background.png");
        InputStream wide = Thread.currentThread().getContextClassLoader().getResourceAsStream("wide_logo.png");
        InputStream square = Thread.currentThread().getContextClassLoader().getResourceAsStream("square_logo.png");
        InputStream progressbar = Thread.currentThread().getContextClassLoader().getResourceAsStream("progressbar.png");
        InputStream progressbarBG = Thread.currentThread().getContextClassLoader().getResourceAsStream("progressbar_background.png");
        try {
            // Copy the default textures into the config directory //
            if (!BackgroundTexture.toFile().exists()) Files.copy(background,BackgroundTexture,StandardCopyOption.REPLACE_EXISTING);
            if (!WideLogoTexture.toFile().exists()) Files.copy(wide,WideLogoTexture,StandardCopyOption.REPLACE_EXISTING);
            if (!SquareLogoTexture.toFile().exists()) Files.copy(square,SquareLogoTexture,StandardCopyOption.REPLACE_EXISTING);
            if (!ProgressBarTexture.toFile().exists()) Files.copy(progressbar,ProgressBarTexture,StandardCopyOption.REPLACE_EXISTING);
            if (!ProgressBarBackgroundTexture.toFile().exists()) Files.copy(progressbarBG,ProgressBarBackgroundTexture,StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> tick()
        );
    }
}
