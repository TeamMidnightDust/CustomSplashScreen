package eu.midnightdust.customsplashscreen.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import eu.midnightdust.customsplashscreen.config.CustomSplashScreenConfig;
import eu.midnightdust.customsplashscreen.texture.BlurredConfigTexture;
import eu.midnightdust.customsplashscreen.texture.ConfigTexture;
import eu.midnightdust.customsplashscreen.texture.EmptyTexture;
import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.lib.util.MidnightColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

@Mixin(value = SplashOverlay.class, priority = 3000)
public abstract class MixinSplashScreen {

    @Shadow @Final static Identifier LOGO;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private boolean reloading;
    @Shadow private float progress;
    @Shadow private long reloadCompleteTime;
    @Shadow private long reloadStartTime;

    @Shadow
    private static int withAlpha(int color, int alpha) {
        return 0;
    }

    @Shadow @Final private static IntSupplier BRAND_ARGB;
    private static final Identifier EMPTY_TEXTURE = new Identifier("customsplashscreen","empty.png");
    private static final Identifier MOJANG_TEXTURE = new Identifier("customsplashscreen", "wide_logo.png");
    private static final Identifier ASPECT_1to1_TEXTURE = new Identifier("customsplashscreen", "square_logo.png");
    private static final Identifier BOSS_BAR_TEXTURE = new Identifier("textures/gui/bars.png");
    private static final Identifier CUSTOM_PROGRESS_BAR_TEXTURE = new Identifier("customsplashscreen", "progressbar.png");
    private static final Identifier CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE = new Identifier("customsplashscreen", "progressbar_background.png");
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("customsplashscreen", "background.png");

    @Inject(method = "<init>", at = @At("TAIL"))
    private void css$init(MinecraftClient client, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading, CallbackInfo ci) { // Load our custom textures on screen init //
        if (CustomSplashScreenConfig.logoStyle.equals(CustomSplashScreenConfig.LogoStyle.Mojang))
            client.getTextureManager().registerTexture(LOGO, new BlurredConfigTexture(MOJANG_TEXTURE));
        else client.getTextureManager().registerTexture(LOGO, new EmptyTexture(EMPTY_TEXTURE));

        client.getTextureManager().registerTexture(ASPECT_1to1_TEXTURE, new ConfigTexture(ASPECT_1to1_TEXTURE));
        client.getTextureManager().registerTexture(BACKGROUND_TEXTURE, new ConfigTexture(BACKGROUND_TEXTURE));

        client.getTextureManager().registerTexture(CUSTOM_PROGRESS_BAR_TEXTURE, new ConfigTexture(CUSTOM_PROGRESS_BAR_TEXTURE));
        client.getTextureManager().registerTexture(CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE, new ConfigTexture(CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE));
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I", shift = At.Shift.BEFORE, ordinal = 2))
    private void css$renderSplashBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (CustomSplashScreenConfig.backgroundImage) {
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();
            float f = this.reloadCompleteTime > -1L ? (float)(Util.getMeasuringTimeMs() - this.reloadCompleteTime) / 1000.0F : -1.0F;
            float g = this.reloadStartTime> -1L ? (float)(Util.getMeasuringTimeMs() - this.reloadStartTime) / 500.0F : -1.0F;
            float s;
            if (f >= 1.0F) s = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
            else if (reloading) s = MathHelper.clamp(g, 0.0F, 1.0F);
            else s = 1.0F;
            RenderSystem.enableBlend();
            RenderSystem.blendEquation(32774);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, s);
            context.drawTexture(BACKGROUND_TEXTURE, 0, 0, 0, 0, 0, width, height, width, height);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void css$render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (CustomSplashScreenConfig.logoStyle == CustomSplashScreenConfig.LogoStyle.Aspect1to1) {
            float f = this.reloadCompleteTime > -1L ? (float)(Util.getMeasuringTimeMs() - this.reloadCompleteTime) / 1000.0F : -1.0F;
            float s = 1.0f;

            if (f >= 1.0F) s = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
            else if (this.reloading) s = MathHelper.clamp((this.reloadStartTime > -1L ? (float)(Util.getMeasuringTimeMs() - this.reloadStartTime) / 500.0F : -1.0F), 0.0F, 1.0F);

            double d = Math.min((double)this.client.getWindow().getScaledWidth() * 0.75D, this.client.getWindow().getScaledHeight()) * 0.25D;
            int w = (int)(d * 2);
            // Render the Logo
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            if (!CustomSplashScreenConfig.logoBlend) RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, s);
            context.drawTexture(ASPECT_1to1_TEXTURE,(int)(this.client.getWindow().getScaledWidth() * 0.5D) - (w / 2), (int)(d * 0.5D), w, w, 0, 0, 512, 512, 512, 512);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
        if (client.currentScreen != null && client.currentScreen instanceof MidnightConfig.MidnightConfigScreen) this.progress = 1f;
    }

    @Inject(at = @At("TAIL"), method = "renderProgressBar")
    private void css$renderProgressBar(DrawContext context, int x1, int y1, int x2, int y2, float opacity, CallbackInfo ci) {
        int i = MathHelper.ceil((float)(x2 - x1 - 2) * this.progress);

        // Bossbar Progress Bar
        if (CustomSplashScreenConfig.progressBarType == CustomSplashScreenConfig.ProgressBarType.BossBar) {
            int color = CustomSplashScreenConfig.bossBarColor.ordinal() * 10;
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);

            int overlay = 70 + CustomSplashScreenConfig.bossBarType.ordinal()*10;
            int width = (int) ((x2 - x1) * (CustomSplashScreenConfig.bossBarSize * 0.01f));
            int offset = ((x2 - x1) - width) / 2;
            i = MathHelper.ceil((float)(width - 2) * this.progress);

            context.drawTexture(BOSS_BAR_TEXTURE, x1 + offset, y1 + 1, width, (int) ((width / 182f) * 5), 0, color, 182, 5,256, 256);
            context.drawTexture(BOSS_BAR_TEXTURE, x1 + offset, y1 + 1, i, (int) ((width / 182f) * 5), 0, color+5, (int) (180 * this.progress), 5, 256, 256);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (overlay != 120) {
                context.drawTexture(BOSS_BAR_TEXTURE, x1 + offset, y1 + 1, width, (int) ((width / 182f) * 5), 0, overlay, 182, 5,256, 256);
            }
            RenderSystem.disableBlend();
        }

        // Custom Progress Bar
        if (CustomSplashScreenConfig.progressBarType == CustomSplashScreenConfig.ProgressBarType.Custom) {
            int regionWidth = CustomSplashScreenConfig.customProgressBarMode == CustomSplashScreenConfig.ProgressBarMode.Stretch ? x2 - x1 : i;
            int height = (int) (((x2 - x1) / 400f) * 10);
            int u = CustomSplashScreenConfig.customProgressBarMode.equals(CustomSplashScreenConfig.ProgressBarMode.Slide) ? x2 - x1 - i : 0;
            if (CustomSplashScreenConfig.progressBarBackground) {
                RenderSystem.setShader(GameRenderer::getPositionTexProgram);
                context.drawTexture(CUSTOM_PROGRESS_BAR_BACKGROUND_TEXTURE, x1, y1, x2 - x1, height, 0, 0, x2 - x1, height, x2 - x1, height);
            }
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            context.drawTexture(CUSTOM_PROGRESS_BAR_TEXTURE, x1, y1, i, height, u, 0, regionWidth, height, x2 - x1, height);
        }
        // Spinning Circle Progress Indicator
        if (CustomSplashScreenConfig.progressBarType == CustomSplashScreenConfig.ProgressBarType.SpinningCircle) {
            int centerX = x1+(x2-x1)/2;
            int centerY = y1+(y2-y1)/2;
            int size = (y2-y1)*CustomSplashScreenConfig.spinningCircleSize;
            float f = this.reloadCompleteTime > -1L ? (float) (Util.getMeasuringTimeMs() - this.reloadCompleteTime) / 1000.0F : -1.0F;
            int m = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            int time = (((int) (MidnightColorUtil.hue * 24 * CustomSplashScreenConfig.spinningCircleSpeed))%24)-1;

            int color = withAlpha(MidnightColorUtil.hex2Rgb(CustomSplashScreenConfig.splashProgressBarColor).getRGB(), m);
            for (int j = 0; j<=CustomSplashScreenConfig.spinningCircleTrail; j++) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                renderSpinningCircle(context,(time+j) % 24,centerY - size,centerY + size, centerX - size, centerX + size,size/5,color);
            }
        }
    }
    @Unique
    private void renderSpinningCircle(DrawContext context, int time, int top, int bottom, int left, int right, int blockSize, int color) {
        switch (time) {
            //top
            case 0 -> context.fill(left + 4*blockSize, top, left + 3*blockSize, top + blockSize, color);
            case 1 -> context.fill(left + 5*blockSize, top, left + 4*blockSize, top + blockSize, color);
            case 2 -> context.fill(left + 6*blockSize, top, left + 5*blockSize, top + blockSize, color);
            case 3 -> context.fill(left + 7*blockSize, top, left + 6*blockSize, top + blockSize, color);
            //top right
            case 4 -> context.fill(right - 3*blockSize, top + blockSize, right - 2*blockSize, top + 2*blockSize, color);
            case 5 -> context.fill(right - 2*blockSize, top + 2*blockSize, right - blockSize, top + 3*blockSize, color);
            //right
            case 6 -> context.fill(right - blockSize, top + 3*blockSize, right, top + 4*blockSize, color);
            case 7 -> context.fill(right - blockSize, top + 4*blockSize, right, top + 5*blockSize, color);
            case 8 -> context.fill(right - blockSize, top + 5*blockSize, right, top + 6*blockSize, color);
            case 9 -> context.fill(right - blockSize, top + 6*blockSize, right, top + 7*blockSize, color);
            //bottom right
            case 10 -> context.fill(right - 2*blockSize, bottom - 2*blockSize, right - blockSize, bottom - 3*blockSize, color);
            case 11 -> context.fill(right - 3*blockSize, bottom - blockSize, right - 2*blockSize, bottom - 2*blockSize, color);
            //bottom
            case 12 -> context.fill(right - 4*blockSize, bottom, right - 3*blockSize, bottom - blockSize, color);
            case 13 -> context.fill(right - 5*blockSize, bottom, right - 4*blockSize, bottom - blockSize, color);
            case 14 -> context.fill(right - 6*blockSize, bottom, right - 5*blockSize, bottom - blockSize, color);
            case 15 -> context.fill(right - 7*blockSize, bottom, right - 6*blockSize, bottom - blockSize, color);
            //bottom left
            case 16 -> context.fill(left + 3*blockSize, bottom - blockSize, left + 2*blockSize, bottom - 2*blockSize, color);
            case 17 -> context.fill(left + 2*blockSize, bottom - 2*blockSize, left + blockSize, bottom - 3*blockSize, color);
            //left
            case 18 -> context.fill(left + blockSize, bottom - 3*blockSize, left, bottom - 4*blockSize, color);
            case 19 -> context.fill(left + blockSize, bottom - 4*blockSize, left, bottom - 5*blockSize, color);
            case 20 -> context.fill(left + blockSize, bottom - 5*blockSize, left, bottom - 6*blockSize, color);
            case 21 -> context.fill(left + blockSize, bottom - 6*blockSize, left, bottom - 7*blockSize, color);
            //top left
            case 22 -> context.fill(left + 2*blockSize, top + 2*blockSize, left + blockSize, top + 3*blockSize, color);
            case 23 -> context.fill(left + 3*blockSize, top + blockSize, left + 2 * blockSize, top + 2*blockSize, color);
        }
    }
//    Replaced by the methods below for compatibility with Puzzle
//    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/util/function/IntSupplier;getAsInt()I"))
//    private int css$modifyBackground(IntSupplier instance) { // Set the Background Color to our configured value //
//        return !CustomSplashScreenConfig.backgroundImage ? MidnightColorUtil.hex2Rgb(CustomSplashScreenConfig.backgroundColor).getRGB() | 255 << 24 : 0;
//    }
    @Inject(method = "withAlpha", at = @At("RETURN"), cancellable = true)
    private static void css$modifyBackgroundColor(int color, int alpha, CallbackInfoReturnable<Integer> cir) {
        if (color == BRAND_ARGB.getAsInt()) {
            int configColor = !CustomSplashScreenConfig.backgroundImage ? MidnightColorUtil.hex2Rgb(CustomSplashScreenConfig.splashBackgroundColor).getRGB() | 255 << 24 : 0;
            cir.setReturnValue(configColor & 16777215 | alpha << 24);
        }
    }
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clearColor(FFFF)V"))
    private void css$clearModifiedColor(float red, float green, float blue, float alpha) {
        int k = !CustomSplashScreenConfig.backgroundImage ? MidnightColorUtil.hex2Rgb(CustomSplashScreenConfig.splashBackgroundColor).getRGB() : 0;
        float m = (float)(k >> 16 & 255) / 255.0F;
        float n = (float)(k >> 8 & 255) / 255.0F;
        float o = (float)(k & 255) / 255.0F;
        GlStateManager._clearColor(m, n, o, 1.0F);
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFunc(II)V", shift = At.Shift.AFTER))
    private void css$betterBlend(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!CustomSplashScreenConfig.logoBlend) RenderSystem.defaultBlendFunc();
    }
    @Inject(method = "renderProgressBar", at = @At("HEAD"))
    private void css$addProgressBarBackground(DrawContext context, int minX, int minY, int maxX, int maxY, float opacity, CallbackInfo ci) {
        if (CustomSplashScreenConfig.progressBarType.equals(CustomSplashScreenConfig.ProgressBarType.Vanilla) && CustomSplashScreenConfig.progressBarBackground) {
            float f = this.reloadCompleteTime > -1L ? (float) (Util.getMeasuringTimeMs() - this.reloadCompleteTime) / 1000.0F : -1.0F;
            int m = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
            RenderSystem.disableBlend();
            context.fill(minX, minY, maxX, maxY, withAlpha(MidnightColorUtil.hex2Rgb(CustomSplashScreenConfig.splashProgressBackgroundColor).getRGB(), m));
        }
    }

    @ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"), index = 4)
    private int css$modifyProgressFrame(int color) { // Set the Progress Bar Frame Color to our configured value //
        return CustomSplashScreenConfig.progressBarType.equals(CustomSplashScreenConfig.ProgressBarType.Vanilla) ? MidnightColorUtil.hex2Rgb(CustomSplashScreenConfig.splashProgressFrameColor).getRGB() | 255 << 24 : 0;
    }
    @ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0), index = 4)
    private int css$modifyProgressColor(int color) { // Set the Progress Bar Color to our configured value //
        return CustomSplashScreenConfig.progressBarType.equals(CustomSplashScreenConfig.ProgressBarType.Vanilla) ? MidnightColorUtil.hex2Rgb(CustomSplashScreenConfig.splashProgressBarColor).getRGB() | 255 << 24 : 0;
    }
}