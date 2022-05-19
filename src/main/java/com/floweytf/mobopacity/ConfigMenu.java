package com.floweytf.mobopacity;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.AbstractMap;

public class ConfigMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        try {
            Class.forName("me.shedaniel.clothconfig2.api.ConfigBuilder");
        } catch (ClassNotFoundException e) {
            return parent -> null;
        }
        return ConfigScreen::new;
    }

    private static class ConfigScreen extends Screen {
        private final Screen parent;

        protected ConfigScreen(Screen parent) {
            super(new TranslatableComponent("mobopacity.config.title"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            super.init();
            ConfigBuilder config = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableComponent("mobopacity.config.title"))
                .setSavingRunnable(ModClientMain::saveConfig);

            config.getOrCreateCategory(new TranslatableComponent("mobopacity.config.category.isenabled"))
                .addEntry(
                    config.entryBuilder()
                        .startBooleanToggle(new TranslatableComponent("mobopacity.config.isenabled"), ModClientMain.options.enabled)
                        .setDefaultValue(true)
                        .setTooltip(
                            new TranslatableComponent("mobopacity.config.isenabled.tooltip")
                        )
                        .setSaveConsumer((v) -> ModClientMain.options.enabled = v)
                        .build()
                );

            config.getOrCreateCategory(new TranslatableComponent("mobopacity.config.category.misc")).addEntry(
                config.entryBuilder()
                    .startIntSlider(
                        new TranslatableComponent(
                            "mobopacity.config.opacity",
                            "Player"
                        ),
                        (int) (ModClientMain.options.opacities.getOrDefault(new ResourceLocation("minecraft:player"), 1f) * 100),
                        0, 100)
                    .setDefaultValue(100)
                    .setTooltip(
                        new TranslatableComponent(
                            "mobopacity.config.opacity.tooltip",
                            "Player"
                        )
                    )
                    .setSaveConsumer((v) -> ModClientMain.options.opacities.put(new ResourceLocation("minecraft:player"), (float) v / 100))
                    .build()
            );

            ConfigCategory otherMobs = config.getOrCreateCategory(
                new TranslatableComponent("mobopacity.config.category.othermobs"));

            Registry.ENTITY_TYPE.stream()
                .map((a) -> new AbstractMap.SimpleImmutableEntry<>(a, Registry.ENTITY_TYPE.getKey(a)))
                .forEach((a) -> {
                    if (a.getValue().toString().equals("minecraft:player"))
                        return;
                    otherMobs.addEntry(
                        config.entryBuilder()
                            .startIntSlider(
                                new TranslatableComponent(
                                    "mobopacity.config.opacity",
                                    a.getKey().getDescription().getString()
                                ),
                                (int) (ModClientMain.options.opacities.getOrDefault(a.getValue(), 1f) * 100),
                                0, 100)
                            .setDefaultValue(100)
                            .setTooltip(
                                new TranslatableComponent(
                                    "mobopacity.config.opacity.tooltip",
                                    a.getKey().getDescription().getString()
                                )
                            )
                            .setSaveConsumer((v) -> ModClientMain.options.opacities.put(a.getValue(), (float) v / 100))
                            .build()
                    );
                });

            if (minecraft != null) {
                minecraft.forceSetScreen(config.build());
            }
        }

        @Override
        public void onClose() {
            if (minecraft != null) {
                minecraft.forceSetScreen(parent);
            }
        }
    }
}
