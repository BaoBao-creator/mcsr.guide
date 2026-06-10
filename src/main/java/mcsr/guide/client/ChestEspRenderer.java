package mcsr.guide.client;

import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

import mcsr.guide.SpeedRunGuide;

public final class ChestEspRenderer {
	private static final float RED = 1.0F;
	private static final float GREEN = 0.58F;
	private static final float BLUE = 0.0F;
	private static final float ALPHA = 0.85F;

	private ChestEspRenderer() {
	}

	public static void render(MatrixStack matrices, Camera camera, Frustum frustum) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!SpeedRunGuide.isSingleplayer() || client.world == null || ChestEspCache.CHESTS_BY_CHUNK.isEmpty() || frustum == null) {
			return;
		}
		Vec3d cameraPos = camera.getPos();
		Matrix4f matrix = matrices.peek().getModel();
		int cameraChunkX = MathHelper.floor(cameraPos.x) >> 4;
		int cameraChunkZ = MathHelper.floor(cameraPos.z) >> 4;
		int maxChunkDistance = client.options.viewDistance + 1;

		RenderSystem.disableTexture();
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.lineWidth(2.0F);

		try {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);

			for (Map.Entry<ChunkPos, Map<BlockPos, Box>> entry : ChestEspCache.CHESTS_BY_CHUNK.entrySet()) {
				ChunkPos chunkPos = entry.getKey();
				if (!isChunkInRenderDistance(chunkPos, cameraChunkX, cameraChunkZ, maxChunkDistance)) {
					continue;
				}

				Box chunkBox = ChestEspCache.CHUNK_BOXES.get(chunkPos);
				if (chunkBox == null || !frustum.isVisible(chunkBox)) {
					continue;
				}

				for (Box box : entry.getValue().values()) {
					if (frustum.isVisible(box)) {
						addBox(buffer, matrix, box, cameraPos);
					}
				}
			}

			tessellator.draw();
		} finally {
			RenderSystem.lineWidth(1.0F);
			RenderSystem.disableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.enableTexture();
		}
	}

	private static boolean isChunkInRenderDistance(ChunkPos chunkPos, int cameraChunkX, int cameraChunkZ, int maxChunkDistance) {
		return Math.abs(chunkPos.x - cameraChunkX) <= maxChunkDistance && Math.abs(chunkPos.z - cameraChunkZ) <= maxChunkDistance;
	}

	private static void addBox(BufferBuilder buffer, Matrix4f matrix, Box box, Vec3d cameraPos) {
		double minX = box.minX - cameraPos.x;
		double minY = box.minY - cameraPos.y;
		double minZ = box.minZ - cameraPos.z;
		double maxX = box.maxX - cameraPos.x;
		double maxY = box.maxY - cameraPos.y;
		double maxZ = box.maxZ - cameraPos.z;

		line(buffer, matrix, minX, minY, minZ, maxX, minY, minZ);
		line(buffer, matrix, maxX, minY, minZ, maxX, minY, maxZ);
		line(buffer, matrix, maxX, minY, maxZ, minX, minY, maxZ);
		line(buffer, matrix, minX, minY, maxZ, minX, minY, minZ);

		line(buffer, matrix, minX, maxY, minZ, maxX, maxY, minZ);
		line(buffer, matrix, maxX, maxY, minZ, maxX, maxY, maxZ);
		line(buffer, matrix, maxX, maxY, maxZ, minX, maxY, maxZ);
		line(buffer, matrix, minX, maxY, maxZ, minX, maxY, minZ);

		line(buffer, matrix, minX, minY, minZ, minX, maxY, minZ);
		line(buffer, matrix, maxX, minY, minZ, maxX, maxY, minZ);
		line(buffer, matrix, maxX, minY, maxZ, maxX, maxY, maxZ);
		line(buffer, matrix, minX, minY, maxZ, minX, maxY, maxZ);
	}

	private static void line(BufferBuilder buffer, Matrix4f matrix, double x1, double y1, double z1, double x2, double y2, double z2) {
		vertex(buffer, matrix, x1, y1, z1);
		vertex(buffer, matrix, x2, y2, z2);
	}

	private static void vertex(BufferBuilder buffer, Matrix4f matrix, double x, double y, double z) {
		buffer.vertex(matrix, (float) x, (float) y, (float) z).color(RED, GREEN, BLUE, ALPHA).next();
	}
}
