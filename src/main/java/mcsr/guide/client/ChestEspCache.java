package mcsr.guide.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

public final class ChestEspCache {
	public static final Map<ChunkPos, Map<BlockPos, Box>> CHESTS_BY_CHUNK = new ConcurrentHashMap<>();
	public static final Map<ChunkPos, Box> CHUNK_BOXES = new ConcurrentHashMap<>();

	private ChestEspCache() {
	}

	public static void add(BlockEntity blockEntity) {
		if (!isTracked(blockEntity)) {
			return;
		}

		BlockPos pos = blockEntity.getPos().toImmutable();
		ChunkPos chunkPos = new ChunkPos(pos);
		CHUNK_BOXES.computeIfAbsent(chunkPos, ChestEspCache::createChunkBox);
		CHESTS_BY_CHUNK.computeIfAbsent(chunkPos, ignored -> new ConcurrentHashMap<>()).put(pos, new Box(pos));
	}

	public static void remove(BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos);
		Map<BlockPos, Box> positions = CHESTS_BY_CHUNK.get(chunkPos);
		if (positions == null) {
			return;
		}

		positions.remove(pos);
		if (positions.isEmpty() && CHESTS_BY_CHUNK.remove(chunkPos, positions)) {
			CHUNK_BOXES.remove(chunkPos);
		}
	}

	public static void removeChunk(ChunkPos chunkPos) {
		CHESTS_BY_CHUNK.remove(chunkPos);
		CHUNK_BOXES.remove(chunkPos);
	}

	public static void clear() {
		CHESTS_BY_CHUNK.clear();
		CHUNK_BOXES.clear();
	}

	private static Box createChunkBox(ChunkPos chunkPos) {
		int minX = chunkPos.x << 4;
		int minZ = chunkPos.z << 4;
		return new Box(minX, 0, minZ, minX + 16, 256, minZ + 16);
	}

	private static boolean isTracked(BlockEntity blockEntity) {
		return blockEntity instanceof ChestBlockEntity || blockEntity instanceof ShulkerBoxBlockEntity;
	}
}
