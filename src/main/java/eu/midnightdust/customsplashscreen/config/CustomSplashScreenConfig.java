package eu.midnightdust.customsplashscreen.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class CustomSplashScreenConfig extends MidnightConfig {
    public static final String general = "general";
    public static final String loading = "loading_indicator";
    public static final String colors = "colors";

    //"Change the design of the progress bar")
    @Entry(category = loading)
    public static ProgressBarType progressBarType = ProgressBarType.Vanilla;

    //"Change the texture of the logo")
    @Entry(category = general)
    public static LogoStyle logoStyle = LogoStyle.Mojang;

    //"Enable/Disable the background image")
    @Entry(category = general)
    public static boolean backgroundImage = false;

    //"Enable/Disable logo blend")
    @Entry(category = general)
    public static boolean logoBlend = true;

    //"Change the color of the background")
    @Entry(category = colors, isColor = true)
    public static String splashBackgroundColor = "#EF323D";
    //"Change the color of the progress bar")
    @Entry(category = colors, isColor = true)
    public static String splashProgressBarColor = "#FFFFFF";
    //"Change the color of the progress bar frame")
    @Entry(category = colors, isColor = true)
    public static String splashProgressFrameColor = "#FFFFFF";
    @Entry(category = colors, isColor = true)
    public static String splashProgressBackgroundColor = "#000000";

    //"Enable/Disable the progress bar background")
    @Entry(category = loading)
    public static boolean progressBarBackground = false;

    //"Change the mode of the custom loading bar")
    @Entry(category = loading)
    public static ProgressBarMode customProgressBarMode = ProgressBarMode.Linear;

    @Entry(category = loading, isSlider = true, min = 1, max = 10)
    public static int spinningCircleSize = 2;
    @Entry(category = loading, isSlider = true, min = 1, max = 10)
    public static int spinningCircleSpeed = 4;
    @Entry(category = loading, isSlider = true, min = 0, max = 23)
    public static int spinningCircleTrail = 5;

    public enum ProgressBarType {
        Vanilla, Custom, SpinningCircle, Hidden;
    }
    public enum LogoStyle {
        Mojang, Aspect1to1, Hidden;
    }
    public enum ProgressBarMode {
        Linear, Stretch, Slide;
    }
}
