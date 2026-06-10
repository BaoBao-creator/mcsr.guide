package mcsr.guide.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mcsr.guide.SpeedRunGuide;
import mcsr.guide.client.ChestEspCache;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
	@Inject(method = "addBlockEntity", at = @At("TAIL"))
	private void speedrunguide$onAddBlockEntity(BlockEntity blockEntity, CallbackInfo ci) {
		if (SpeedRunGuide.isSingleplayer()) {
			ChestEspCache.add(blockEntity);
		}
	}

	@Inject(method = "removeBlockEntity", at = @At("HEAD"))
	private void speedrunguide$onRemoveBlockEntity(BlockPos pos, CallbackInfo ci) {
		if (SpeedRunGuide.isSingleplayer()) {
			ChestEspCache.remove(pos);
		}
	}
}
