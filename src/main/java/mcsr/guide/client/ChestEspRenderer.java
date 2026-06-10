package mcsr.guide.client;

import java.util.Map;
import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

import mcsr.guide.SpeedRunGuide;

public final class ChestEspRenderer {
	private static final float RED = 1.0F;
	private static final float GREEN = 0.58F;
	private static final float BLUE = 0.0F;
	private static final float ALPHA = 0.85F;

	private ChestEspRenderer() {
	}

	public static void render(WorldRenderContext context) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!SpeedRunGuide.isSingleplayer() || client.world == null || ChestEspCache.CHESTS_BY_CHUNK.isEmpty()) {
			return;
		}

		Frustum frustum = context.frustum();
		if (frustum == null) {
			return;
		}

		MatrixStack matrices = context.matrixStack();
		Camera camera = context.camera();
		Vec3d cameraPos = camera.getPos();
		Matrix4f matrix = matrices.peek().getModel();

		RenderSystem.disableTexture();
		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.lineWidth(2.0F);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

		for (Map.Entry<ChunkPos, Set<BlockPos>> entry : ChestEspCache.CHESTS_BY_CHUNK.entrySet()) {
			ChunkPos chunkPos = entry.getKey();
			if (!frustum.isVisible(getChunkBox(chunkPos))) {
				continue;
			}

			for (BlockPos pos : entry.getValue()) {
				Box box = new Box(pos);
				if (frustum.isVisible(box)) {
					addBox(buffer, matrix, box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z));
				}
			}
		}

		tessellator.draw();

		RenderSystem.lineWidth(1.0F);
		RenderSystem.disableBlend();
		RenderSystem.enableDepthTest();
		RenderSystem.enableTexture();
	}

	private static Box getChunkBox(ChunkPos chunkPos) {
		int minX = chunkPos.x << 4;
		int minZ = chunkPos.z << 4;
		return new Box(minX, 0, minZ, minX + 16, 256, minZ + 16);
	}

	private static void addBox(BufferBuilder buffer, Matrix4f matrix, Box box) {
		double minX = box.minX;
		double minY = box.minY;
		double minZ = box.minZ;
		double maxX = box.maxX;
		double maxY = box.maxY;
		double maxZ = box.maxZ;

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
