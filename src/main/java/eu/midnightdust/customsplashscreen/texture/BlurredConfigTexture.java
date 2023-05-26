package eu.midnightdust.customsplashscreen.texture;

import net.minecraft.util.Identifier;

public class BlurredConfigTexture extends ConfigTexture {
    // Load textures from the config directory //

    public BlurredConfigTexture(Identifier location) {
        super(location);
        shouldBlur = true;
    }
}
