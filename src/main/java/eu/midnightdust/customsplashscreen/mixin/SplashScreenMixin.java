package eu.midnightdust.customsplashscreen.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.midnightdust.customsplashscreen.CustomSplashScreenClient;
import eu.midnightdust.customsplashscreen.config.CustomSplashScreenConfig;
import eu.midnightdust.customsplashscreen.texture.BlurredConfigTexture;
import eu.midnightdust.customsplashscreen.texture.ConfigTexture;
import eu.midnightdust.customsplashscreen.texture.EmptyTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

@Mixin(SplashScreen.class)
public class SplashScreenMixin {

    @Shadow @Final private static Identifier LOGO;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private boolean reloading;
    @Shadow private long applyCompleteTime;
    @Shadow @Final private ResourceReloadMonitor reloadMonitor;
    @Shadow private long prepareCompleteTime;
    @Shadow private float progress;

    private static final CustomSplashScreenConfig CS_CONFIG = CustomSplashScreenClient.CS_CONFIG;


    private static final Identifier EMPTY_TEXTURE = new Identifier("empty.png");
    private static final Identifier MOJANG_TEXTURE = new Identifier(CS_CONFIG.textures.MojangLogo);
    private static final Identifier ASPECT_1to1_TEXTURE = new Identifier(CS_CONFIG.textures.Aspect1to1Logo);
    private static final Identifier BOSS_BAR_TEXTURE = new Identifier(CS_CONFIG.textures.BossBarTexture);
    private static final Identifier CUSTOM_PROGRESS_BAR_TEXTURE = new Identifier(CS_CONFIG.textures.CustomBarTexture);
    private static final Identifier CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE = new Identifier(CS_CONFIG.textures.CustomBarBackgroundTexture);
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(CS_CONFIG.textures.BackgroundTexture);

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;)V", at = @At("HEAD"), cancellable=true)
    private static void init(MinecraftClient client, CallbackInfo ci) { // Load our custom textures at game start //
        if (CS_CONFIG.logoStyle == CustomSplashScreenConfig.LogoStyle.Mojang) {
            client.getTextureManager().registerTexture(LOGO, new BlurredConfigTexture(MOJANG_TEXTURE));
        }
        else {
            client.getTextureManager().registerTexture(LOGO, new EmptyTexture(EMPTY_TEXTURE));
        }
        client.getTextureManager().registerTexture(ASPECT_1to1_TEXTURE, new ConfigTexture(ASPECT_1to1_TEXTURE));
        client.getTextureManager().registerTexture(BACKGROUND_TEXTURE, new ConfigTexture(BACKGROUND_TEXTURE));

        client.getTextureManager().registerTexture(CUSTOM_PROGRESS_BAR_TEXTURE, new ConfigTexture(CUSTOM_PROGRESS_BAR_TEXTURE));
        client.getTextureManager().registerTexture(CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE, new ConfigTexture(CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE));

        ci.cancel();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/TextureManager;bindTexture(Lnet/minecraft/util/Identifier;)V",shift = At.Shift.BEFORE), method = "render")
    private void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) { // Render our background image //
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && (this.reloadMonitor.isPrepareStageComplete() || this.client.currentScreen != null) && this.prepareCompleteTime == -1L) {
            this.prepareCompleteTime = l;
        }

        float f = this.applyCompleteTime > -1L ? (float)(l - this.applyCompleteTime) / 1000.0F : -1.0F;
        float g = this.prepareCompleteTime > -1L ? (float)(l - this.prepareCompleteTime) / 500.0F : -1.0F;
        float o;
        if (f >= 1.0F) {
            o = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.reloading) {
            o = MathHelper.clamp(g, 0.0F, 1.0F);
        } else {
            o = 1.0F;
        }

        int maxX = this.client.getWindow().getScaledWidth();
        int maxY = this.client.getWindow().getScaledHeight();

        if (CS_CONFIG.backgroundImage) {
            client.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.alphaFunc(516, 0.0F);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, o);
            drawTexture(matrices, 0, 0, 0, 0, 0, maxX, maxY, maxY, maxX);
            RenderSystem.defaultBlendFunc();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.disableBlend();
        }
    }

    @Inject(at = @At("TAIL"), method = "render")
    private void renderLogo(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) { // Render our 1 to 1 logo //
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && (this.reloadMonitor.isPrepareStageComplete() || this.client.currentScreen != null) && this.prepareCompleteTime == -1L) {
            this.prepareCompleteTime = l;
        }
        float f = this.applyCompleteTime > -1L ? (float)(l - this.applyCompleteTime) / 1000.0F : -1.0F;
        float g = this.prepareCompleteTime > -1L ? (float)(l - this.prepareCompleteTime) / 500.0F : -1.0F;
        float o;
        int m;
        if (f >= 1.0F) {
            o = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.reloading) {
            o = MathHelper.clamp(g, 0.0F, 1.0F);
        } else {
            o = 1.0F;
        }

        m = (int)((double)this.client.getWindow().getScaledWidth() * 0.5D);

        if (CustomSplashScreenClient.CS_CONFIG.logoStyle == CustomSplashScreenConfig.LogoStyle.Aspect1to1) {
            double d = Math.min((double)this.client.getWindow().getScaledWidth() * 0.75D, this.client.getWindow().getScaledHeight()) * 0.25D;
            int r = (int)(d * 0.5D);
            double e = d * 4.0D;
            int s = (int)(e * 0.5D);
            client.getTextureManager().bindTexture(ASPECT_1to1_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.alphaFunc(516, 0.0F);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, o);
            drawTexture(matrices, m - (s / 2), r, s, s, 0, 0, 512, 512, 512, 512);
            RenderSystem.defaultBlendFunc();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.disableBlend();
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 5)
    private int modifyBackgroundColor(int color) { // Set the Background Color to our configured value //
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && (this.reloadMonitor.isPrepareStageComplete() || this.client.currentScreen != null) && this.prepareCompleteTime == -1L) {
            this.prepareCompleteTime = l;
        }
        float f = this.applyCompleteTime > -1L ? (float)(l - this.applyCompleteTime) / 1000.0F : -1.0F;
        int m;
        m = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);

        if (CS_CONFIG.backgroundImage) {
            return BackgroundHelper.ColorMixer.getArgb(0, 0, 0, 0);
        }
        else {
            return CustomSplashScreenClient.CS_CONFIG.backgroundColor | m << 24;
        }
    }

    @ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index = 5)
    private int modifyProgressFrame(int color) { // Set the Progress Bar Frame Color to our configured value //
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.BossBar || CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.Hidden) {
            return BackgroundHelper.ColorMixer.getArgb(0, 0, 0, 0);
        }
        else return CustomSplashScreenClient.CS_CONFIG.progressFrameColor | 255 << 24;
    }
    @ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V", ordinal = 4), index = 5)
    private int modifyProgressColor(int color) { // Set the Progress Bar Color to our configured value //
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.BossBar || CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.Hidden || CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.Custom) {
            return BackgroundHelper.ColorMixer.getArgb(0, 0, 0, 0);
        }
        else return CustomSplashScreenClient.CS_CONFIG.progressBarColor | 255 << 24;
    }

    @Inject(at = @At("TAIL"), method = "renderProgressBar", cancellable = true)
    private void renderCSProgressBar(MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity, CallbackInfo ci) { // Render our custom Progress Bar //
        int i = MathHelper.ceil((float)(x2 - x1 - 2) * this.progress);
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.BossBar) { // Bossbar Style Progress Bar //
            client.getTextureManager().bindTexture(BOSS_BAR_TEXTURE);
            int overlay = 0;

            if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_6) {overlay = 93;}
            else if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_10) {overlay = 105;}
            else if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_12) {overlay = 117;}
            else if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_20) {overlay = 129;}

            int bbWidth = (int) ((x2 - x1+1) * 1.4f);
            int bbHeight = (y2 - y1) * 30;
            drawTexture(matrices, x1, y1 + 1, 0, 0, 0, x2-x1, (int) ((y2-y1)/1.4f), bbHeight, bbWidth);
            drawTexture(matrices, x1, y1 + 1, 0, 0, 5f, i, (int) ((y2-y1)/1.4f), bbHeight, bbWidth);

            RenderSystem.alphaFunc(516, 0.0F);
            RenderSystem.enableBlend();
            if (overlay != 0) {
                drawTexture(matrices, x1, y1 + 1, 0, 0, overlay, x2 - x1, (int) ((y2 - y1) / 1.4f), bbHeight, bbWidth);
            }
            RenderSystem.defaultBlendFunc();
            RenderSystem.defaultAlphaFunc();
            RenderSystem.disableBlend();
        }
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.Custom) { // Custom Progress Bar //
            int customWidth = CustomSplashScreenClient.CS_CONFIG.customProgressBarMode == CustomSplashScreenConfig.ProgressBarMode.Linear ? x2-x1 : i;
            if (CS_CONFIG.customProgressBarBackground) {
                client.getTextureManager().bindTexture(CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE);
                drawTexture(matrices, x1, y1, 0, 0, 6, x2-x1, y2-y1, 10, x2-x1);
            }
            client.getTextureManager().bindTexture(CUSTOM_PROGRESS_BAR_TEXTURE);
            drawTexture(matrices, x1, y1, 0, 0, 6, i, y2-y1, 10, customWidth);
        }
    }

}
