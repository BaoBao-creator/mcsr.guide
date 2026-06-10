package mcsr.guide.client;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public final class ChestEspCache {
	public static final Map<ChunkPos, Set<BlockPos>> CHESTS_BY_CHUNK = new ConcurrentHashMap<>();

	private ChestEspCache() {
	}

	public static void add(BlockEntity blockEntity) {
		if (!isTracked(blockEntity)) {
			return;
		}

		BlockPos pos = blockEntity.getPos();
		CHESTS_BY_CHUNK.computeIfAbsent(new ChunkPos(pos), chunkPos -> ConcurrentHashMap.newKeySet()).add(pos.toImmutable());
	}

	public static void remove(BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos);
		Set<BlockPos> positions = CHESTS_BY_CHUNK.get(chunkPos);
		if (positions == null) {
			return;
		}

		positions.remove(pos);
		if (positions.isEmpty()) {
			CHESTS_BY_CHUNK.remove(chunkPos, positions);
		}
	}

	public static void removeChunk(ChunkPos chunkPos) {
		CHESTS_BY_CHUNK.remove(chunkPos);
	}

	public static void clear() {
		CHESTS_BY_CHUNK.clear();
	}

	private static boolean isTracked(BlockEntity blockEntity) {
		return blockEntity instanceof ChestBlockEntity || blockEntity instanceof ShulkerBoxBlockEntity;
	}
}
