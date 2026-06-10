package mcsr.guide;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mcsr.guide.client.ChestEspCache;
import mcsr.guide.client.ChestEspRenderer;

public class SpeedRunGuide implements ClientModInitializer {
	public static final String MOD_ID = "speedrunguide";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!isSingleplayer(client)) {
				ChestEspCache.clear();
			}
		});

		ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
			if (isSingleplayer(MinecraftClient.getInstance())) {
				ChestEspCache.removeChunk(chunk.getPos());
			}
		});

		WorldRenderEvents.LAST.register(ChestEspRenderer::render);
		LOGGER.info("SpeedRunGuide chest ESP enabled for singleplayer worlds only.");
	}

	public static boolean isSingleplayer() {
		return isSingleplayer(MinecraftClient.getInstance());
	}

	private static boolean isSingleplayer(MinecraftClient client) {
		return client != null && client.world != null && client.isInSingleplayer();
	}
}
