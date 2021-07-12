package eu.midnightdust.customsplashscreen.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() { // Provide our Config Screen to Mod Menu //
        return parent -> AutoConfig.getConfigScreen(CustomSplashScreenConfig.class, parent).get();
    }
}