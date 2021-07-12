package eu.midnightdust.customsplashscreen.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "customsplashscreen")
public class CustomSplashScreenConfig implements ConfigData {

    @Comment(value = "Change the design of the progress bar")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ProgressBarType progressBarType = ProgressBarType.Vanilla;

    @Comment(value = "Change the texture of the logo")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public LogoStyle logoStyle = LogoStyle.Mojang;

    @Comment(value = "Enable/Disable the background image")
    public boolean backgroundImage = false;

    @Comment(value = "Change the color of the background")
    @ConfigEntry.ColorPicker
    public int backgroundColor = 15675965;
    @Comment(value = "Change the color of the progress bar")
    @ConfigEntry.ColorPicker
    public int progressBarColor = 16777215;
    @Comment(value = "Change the color of the progress bar frame")
    @ConfigEntry.ColorPicker
    public int progressFrameColor = 16777215;

    @Comment(value = "Change the mode of the custom loading bar")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public ProgressBarMode customProgressBarMode = ProgressBarMode.Linear;

    @Comment(value = "Enable/Disable the custom progress bar background")
    public boolean customProgressBarBackground = false;

    @Comment(value = "Change the style of the boss loading bar")
    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    public BossBarType bossBarType = BossBarType.NOTCHED_6;

    @ConfigEntry.Gui.CollapsibleObject
    public Textures textures = new Textures();

    public static class Textures {
        public String BackgroundTexture = "background.png";
        public String MojangLogo = "mojangstudios.png";
        public String Aspect1to1Logo = "mojank.png";
        public String BossBarTexture = "textures/gui/bars.png";
        public String CustomBarTexture = "progressbar.png";
        public String CustomBarBackgroundTexture = "progressbar_background.png";
    }

    public enum ProgressBarType {
        Vanilla, BossBar, Custom, Hidden;
    }
    public enum LogoStyle {
        Mojang, Aspect1to1, Hidden;
    }
    public enum ProgressBarMode {
        Linear, Stretch;
    }
    public enum BossBarType {
        PROGRESS, NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20;
    }
}
