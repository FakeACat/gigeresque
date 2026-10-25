package mods.cybercat.gigeresque.client.entity.render.blocks;

import mod.azure.azurelib.common.animation.AzAnimator;
import mod.azure.azurelib.common.render.block.AzBlockEntityRenderer;
import mod.azure.azurelib.common.render.block.AzBlockEntityRendererConfig;
import mod.azure.azurelib.common.render.layer.AzRenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class BaseBlockRenderer<T extends BlockEntity> extends AzBlockEntityRenderer<T> {

    @SafeVarargs
    public BaseBlockRenderer(
        ResourceLocation model,
        ResourceLocation texture,
        @Nullable Supplier<AzAnimator<Long, T>> animator,
        @Nullable AzRenderLayer<Long, T>... layers
    ) {
        super(buildConfig(be -> model, be -> texture, animator, layers));
    }

    public BaseBlockRenderer(ResourceLocation model, ResourceLocation texture, @Nullable Supplier<AzAnimator<Long, T>> animator) {
        this(model, texture, animator, new AzRenderLayer[0]);
    }

    public BaseBlockRenderer(ResourceLocation model, ResourceLocation texture) {
        this(model, texture, null, new AzRenderLayer[0]);
    }

    public BaseBlockRenderer(Function<T, ResourceLocation> model, Function<T, ResourceLocation> texture) {
        super(buildConfig(model, texture, null));
    }

    @SafeVarargs
    private static <T extends BlockEntity> AzBlockEntityRendererConfig<T> buildConfig(
        Function<T, ResourceLocation> model,
        Function<T, ResourceLocation> texture,
        @Nullable Supplier<AzAnimator<Long, T>> animator,
        @Nullable AzRenderLayer<Long, T>... layers
    ) {
        var builder = AzBlockEntityRendererConfig.builder(model, texture);
        if (animator != null)
            builder.setAnimatorProvider(animator);
        if (layers != null) {
            for (AzRenderLayer<Long, T> layer : layers) {
                if (layer != null) {
                    builder.addRenderLayer(layer);
                }
            }
        }
        return builder.build();
    }
}
