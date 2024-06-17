package eu.midnightdust.customsplashscreen.mixin;

import eu.midnightdust.lib.config.MidnightConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SimpleResourceReload;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Mixin(value = MidnightConfig.MidnightConfigScreen.class)
public class MixinMidnightConfig extends Screen {
    @Shadow(remap = false) @Final
    public String modid;

    protected MixinMidnightConfig(Text title) {
        super(title);
    }

    @Inject(at = @At("HEAD"), method = "init")
    protected void init(CallbackInfo ci) {
        if(this.modid.equals("customsplashscreen")) {
            this.addDrawableChild(ButtonWidget.builder(Text.literal("Preview"), (button) -> {
                MidnightConfig.write("customsplashscreen");
                (Objects.requireNonNull(this.client)).setOverlay(
                                new SplashOverlay(client, SimpleResourceReload.create(ResourceManager.Empty.INSTANCE, List.of()
                                        ,Object::notify,Object::notify,new CompletableFuture<>()), throwable -> {}, true));
            }).dimensions(this.width / 2 + 157, this.height - 26, 50, 20).build());
        }
    }
}
