package com.floweytf.mobopacity;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

@Environment(EnvType.CLIENT)
public class ModClientMain implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		try {
			options = readJson();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Options options = new Options();

	private Options readJson() {
		try (FileReader reader = new FileReader(FabricLoader.getInstance().getConfigDir().resolve("mobopacity.json").toFile())) {
			Options opt = new Options();
			JsonObject o = new GsonBuilder().create().fromJson(reader, JsonElement.class).getAsJsonObject();
			opt.enabled = o.get("enabled").getAsBoolean();
			o.get("opacities").getAsJsonObject().entrySet().forEach((e) -> {
				opt.opacities.put(new ResourceLocation(e.getKey()), e.getValue().getAsFloat());
			});
			return opt;
		} catch(Exception e) {
			return new Options();
		}
	}

	private static void writeJson(Options o) throws Exception {
		File f = FabricLoader.getInstance().getConfigDir().resolve("mobopacity.json").toFile();
		f.createNewFile();
		try (FileWriter writer = new FileWriter(f)) {
			writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(o));
		}
	}

	public static void saveConfig() {
		Minecraft.getInstance().execute(() -> {
			try {
				writeJson(options);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	public static float opacityFor(ResourceLocation id) {
		return options.opacities.getOrDefault(id, 1f);
	}
}
