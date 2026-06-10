package mcsr.guide.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import mcsr.guide.client.ChestEspRenderer;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@Inject(method = "render", at = @At("TAIL"))
	private void speedrunguide$onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		ChestEspRenderer.render(matrices, camera, createFrustum(matrices, matrix4f, camera));
	}

	private static Frustum createFrustum(MatrixStack matrices, Matrix4f projectionMatrix, Camera camera) {
		Frustum frustum = new Frustum(matrices.peek().getModel(), projectionMatrix);
		frustum.setPosition(camera.getPos().x, camera.getPos().y, camera.getPos().z);
		return frustum;
	}
}
