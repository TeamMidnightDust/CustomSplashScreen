package eu.midnightdust.customsplashscreen.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import eu.midnightdust.customsplashscreen.CustomSplashScreenClient;
import eu.midnightdust.customsplashscreen.config.CustomSplashScreenConfig;
import eu.midnightdust.customsplashscreen.texture.BlurredConfigTexture;
import eu.midnightdust.customsplashscreen.texture.ConfigTexture;
import eu.midnightdust.customsplashscreen.texture.EmptyTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;
import static net.minecraft.client.gui.DrawableHelper.fill;

@Mixin(SplashOverlay.class)
public class SplashScreenMixin {

    @Shadow @Final static Identifier LOGO;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private boolean reloading;
    @Shadow @Final private ResourceReload reload;
    @Shadow private float progress;
    @Shadow private long reloadCompleteTime;
    @Shadow private long reloadStartTime;
    @Shadow @Final private Consumer<Optional<Throwable>> exceptionHandler;

    private static final CustomSplashScreenConfig CS_CONFIG = CustomSplashScreenClient.CS_CONFIG;

    private static final Identifier EMPTY_TEXTURE = new Identifier("empty.png");
    private static final Identifier MOJANG_TEXTURE = new Identifier(CS_CONFIG.textures.MojangLogo);
    private static final Identifier ASPECT_1to1_TEXTURE = new Identifier(CS_CONFIG.textures.Aspect1to1Logo);
    private static final Identifier BOSS_BAR_TEXTURE = new Identifier(CS_CONFIG.textures.BossBarTexture);
    private static final Identifier CUSTOM_PROGRESS_BAR_TEXTURE = new Identifier(CS_CONFIG.textures.CustomBarTexture);
    private static final Identifier CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE = new Identifier(CS_CONFIG.textures.CustomBarBackgroundTexture);
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(CS_CONFIG.textures.BackgroundTexture);

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;)V", at = @At("HEAD"), cancellable = true)
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

    @Inject(at = @At("TAIL"), method = "render", cancellable = false)
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int i = this.client.getWindow().getScaledWidth();
        int j = this.client.getWindow().getScaledHeight();
        long l = Util.getMeasuringTimeMs();
        if (this.reloading && this.reloadStartTime == -1L) {
            this.reloadStartTime = l;
        }

        float f = this.reloadCompleteTime > -1L ? (float)(l - this.reloadCompleteTime) / 1000.0F : -1.0F;
        float g = this.reloadStartTime > -1L ? (float)(l - this.reloadStartTime) / 500.0F : -1.0F;
        float s;
        int m;

        // Render our custom color
        if (f >= 1.0F) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(matrices, 0, 0, delta);
            }

            m = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            fill(matrices, 0, 0, i, j, withAlpha(m));
            s = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
        } else if (this.reloading) {
            if (this.client.currentScreen != null && g < 1.0F) {
                this.client.currentScreen.render(matrices, mouseX, mouseY, delta);
            }

            m = MathHelper.ceil(MathHelper.clamp((double)g, 0.15D, 1.0D) * 255.0D);
            fill(matrices, 0, 0, i, j, withAlpha(m));
            s = MathHelper.clamp(g, 0.0F, 1.0F);
        } else {
            m = getBackgroundColor();
            float p = (float)(m >> 16 & 255) / 255.0F;
            float q = (float)(m >> 8 & 255) / 255.0F;
            float r = (float)(m & 255) / 255.0F;
            GlStateManager._clearColor(p, q, r, 1.0F);
            GlStateManager._clear(16384, MinecraftClient.IS_SYSTEM_MAC);
            s = 1.0F;
        }

        m = (int)((double)this.client.getWindow().getScaledWidth() * 0.5D);
        int u = (int)((double)this.client.getWindow().getScaledHeight() * 0.5D);
        double d = Math.min((double)this.client.getWindow().getScaledWidth() * 0.75D, (double)this.client.getWindow().getScaledHeight()) * 0.25D;
        int v = (int)(d * 0.5D);
        double e = d * 4.0D;
        int w = (int)(e * 0.5D);

        // Render our custom background image
        if (CS_CONFIG.backgroundImage) {
            RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendEquation(32774);
            RenderSystem.blendFunc(770, 1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, s);
            drawTexture(matrices, 0, 0, 0, 0, 0, i, j, j, i);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }

        // Render the Logo
        RenderSystem.setShaderTexture(0, CustomSplashScreenClient.CS_CONFIG.logoStyle == CustomSplashScreenConfig.LogoStyle.Aspect1to1 ? ASPECT_1to1_TEXTURE : LOGO);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        if (CustomSplashScreenClient.CS_CONFIG.logoStyle == CustomSplashScreenConfig.LogoStyle.Aspect1to1) {
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, s);
            drawTexture(matrices, m - (w / 2), v, w, w, 0, 0, 512, 512, 512, 512);
        } else if (CustomSplashScreenClient.CS_CONFIG.logoStyle == CustomSplashScreenConfig.LogoStyle.Mojang) {
            RenderSystem.blendFunc(770, 1);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, s);
            drawTexture(matrices, m - w, u - v, w, (int)d, -0.0625F, 0.0F, 120, 60, 120, 120);
            drawTexture(matrices, m, u - v, w, (int)d, 0.0625F, 60.0F, 120, 60, 120, 120);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        int x = (int)((double)this.client.getWindow().getScaledHeight() * 0.8325D);
        float y = this.reload.getProgress();
        this.progress = MathHelper.clamp(this.progress * 0.95F + y * 0.050000012F, 0.0F, 1.0F);
        if (f < 1.0F) {
            this.renderProgressBar(matrices, i / 2 - w, x - 5, i / 2 + w, x + 5, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F), null);
        }

        if (f >= 2.0F) {
            this.client.setOverlay(null);
        }

        if (this.reloadCompleteTime == -1L && this.reload.isComplete() && (!this.reloading || g >= 2.0F)) {
            try {
                this.reload.throwException();
                this.exceptionHandler.accept(Optional.empty());
            } catch (Throwable var23) {
                this.exceptionHandler.accept(Optional.of(var23));
            }

            this.reloadCompleteTime = Util.getMeasuringTimeMs();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
            }
        }
    }

    private static int getBackgroundColor() {
        if (CS_CONFIG.backgroundImage) {
            return BackgroundHelper.ColorMixer.getArgb(0, 0, 0, 0);
        }
        else {
            return CustomSplashScreenClient.CS_CONFIG.backgroundColor;
        }
    }

    private static int withAlpha(int alpha) {
        return getBackgroundColor() | alpha << 24;
    }

    @Inject(at = @At("TAIL"), method = "renderProgressBar", cancellable = false)
    private void renderProgressBar(MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity, CallbackInfo ci) {
        int i = MathHelper.ceil((float)(x2 - x1 - 2) * this.progress);

        // Bossbar Progress Bar
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.BossBar) {
            RenderSystem.setShaderTexture(0, BOSS_BAR_TEXTURE);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);

            int overlay = 0;

            if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_6) {overlay = 93;}
            else if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_10) {overlay = 105;}
            else if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_12) {overlay = 117;}
            else if (CustomSplashScreenClient.CS_CONFIG.bossBarType == CustomSplashScreenConfig.BossBarType.NOTCHED_20) {overlay = 129;}

            int bbWidth = (int) ((x2 - x1+1) * 1.4f);
            int bbHeight = (y2 - y1) * 30;
            drawTexture(matrices, x1, y1 + 1, 0, 0, 0, x2 - x1, (int) ((y2-y1) / 1.4f), bbHeight, bbWidth);
            drawTexture(matrices, x1, y1 + 1, 0, 0, 5f, i, (int) ((y2 - y1) / 1.4f), bbHeight, bbWidth);

            RenderSystem.enableBlend();
            RenderSystem.blendEquation(32774);
            RenderSystem.blendFunc(770, 1);
            if (overlay != 0) {
                drawTexture(matrices, x1, y1 + 1, 0, 0, overlay, x2 - x1, (int) ((y2 - y1) / 1.4f), bbHeight, bbWidth);
            }
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }

        // Custom Progress Bar
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.Custom) {
            int customWidth = CustomSplashScreenClient.CS_CONFIG.customProgressBarMode == CustomSplashScreenConfig.ProgressBarMode.Linear ? x2 - x1 : i;
            if (CS_CONFIG.customProgressBarBackground) {
                RenderSystem.setShaderTexture(0, CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                drawTexture(matrices, x1, y1, 0, 0, 6, x2 - x1, y2 - y1, 10, x2-x1);
            }
            RenderSystem.setShaderTexture(0, CUSTOM_PROGRESS_BAR_TEXTURE);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            drawTexture(matrices, x1, y1, 0, 0, 6, i, y2 - y1, 10, customWidth);
        }

        // Vanilla / With Color progress bar
        if (CustomSplashScreenClient.CS_CONFIG.progressBarType == CustomSplashScreenConfig.ProgressBarType.Vanilla) {
            int j = Math.round(opacity * 255.0F);
            int k = CustomSplashScreenClient.CS_CONFIG.progressBarColor | 255 << 24;
            int kk = CustomSplashScreenClient.CS_CONFIG.progressFrameColor | 255 << 24;
            fill(matrices, x1 + 2, y1 + 2, x1 + i, y2 - 2, k);
            fill(matrices, x1 + 1, y1, x2 - 1, y1 + 1, kk);
            fill(matrices, x1 + 1, y2, x2 - 1, y2 - 1, kk);
            fill(matrices, x1, y1, x1 + 1, y2, kk);
            fill(matrices, x2, y1, x2 - 1, y2, kk);
        }

    }

}
