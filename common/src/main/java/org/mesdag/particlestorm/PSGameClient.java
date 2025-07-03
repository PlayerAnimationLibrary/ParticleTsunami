package org.mesdag.particlestorm;

public final class PSGameClient {
    /*public static final ParticleRenderType PARTICLE_ADD = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, @NotNull TextureManager textureManager) {
            RenderSystem.enableDepthTest();
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        public String toString() {
            return "PARTICLE_ADD";
        }
    };*/

    /*@SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            NeoForge.EVENT_BUS.addListener(PSGameClient::tick);
            NeoForge.EVENT_BUS.addListener(PSGameClient::renderLevelStage);
        });
    }*/

    /*private static void renderLevelStage(RenderLevelStageEvent event) {
        if (!ParticleTsunamiPlatform.isDevEnv()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES && minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes())
            for (ParticleEmitter value : LOADER.emitters.values()) {
                if (!value.isInitialized() || value.attached != null) continue;
                PoseStack poseStack = event.getPoseStack();
                MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
                double x = value.getX();
                double y = value.getY();
                double z = value.getZ();
                DebugRenderer.renderFloatingText(poseStack, bufferSource, value.getDetail().option.getId().toString(), x, y + 0.5, z, 0xFFFFFF);
                DebugRenderer.renderFloatingText(poseStack, bufferSource, "id: " + value.id, x, y + 0.3, z, 0xFFFFFF);
                int maxNum = ((ParticleEngineAccessor) minecraft.particleEngine).trackedParticleCounts().getInt(value.particleGroup);
                DebugRenderer.renderFloatingText(poseStack, bufferSource, "particles: " + maxNum, x, y + 0.1, z, maxNum == value.particleGroup.getLimit() ? 0xFF0000 : 0xFFFFFF);
                Camera camera = event.getCamera();
                double d0 = camera.getPosition().x;
                double d1 = camera.getPosition().y;
                double d2 = camera.getPosition().z;
                poseStack.pushPose();
                poseStack.translate(x - d0, y - d1, z - d2);
                LevelRenderer.rend(poseStack, bufferSource.getBuffer(RenderType.lines()), -0.5, -0.5, -0.5, 0.5, 0.5, 0.5, 0, 1, 0, 1);
                poseStack.popPose();
            }
    }*/

    /*@SubscribeEvent
    public static void reload(RegisterClientReloadListenersEvent event) {
        registerComponents();
        registerEventNodes();
        event.registerReloadListener(LOADER);
    }*/
}
