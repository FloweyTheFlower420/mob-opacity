package com.floweytf.mobopacity;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class Options {
    public boolean enabled = true;

    Map<ResourceLocation, Float> opacities = new HashMap<>();
}
