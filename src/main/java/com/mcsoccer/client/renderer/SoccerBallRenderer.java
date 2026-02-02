package com.mcsoccer.client.renderer;

import com.mcsoccer.client.model.SoccerBallModel;
import com.mcsoccer.entity.SoccerBallEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class SoccerBallRenderer extends GeoEntityRenderer<SoccerBallEntity, SoccerBallRenderer.State> {

    public SoccerBallRenderer(EntityRendererProvider.Context context) {
        super(context, new SoccerBallModel());
        this.withScale(0.65f);
    }

    @Override
    public void preRender(State state, PoseStack poseStack, BakedGeoModel model,
                          SubmitNodeCollector renderTasks, CameraRenderState cameraState,
                          int packedLight, int packedOverlay, int renderColor) {
        if (state.rotationAngle != 0f) {
            // Translate to ball center, apply velocity-based rotation, translate back
            poseStack.translate(0, 0.25, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-state.rotationAxisYaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(state.rotationAngle));
            poseStack.mulPose(Axis.YP.rotationDegrees(state.rotationAxisYaw));
            poseStack.translate(0, -0.25, 0);
        }
    }

    @Override
    public void extractRenderState(SoccerBallEntity entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.rotationAngle = entity.getRenderRotation(partialTick);
        state.rotationAxisYaw = entity.getRotationAxisYaw();
    }

    @Override
    public State createRenderState(SoccerBallEntity entity, Void relatedObject) {
        return new State();
    }

    public static class State extends EntityRenderState implements GeoRenderState {
        private final java.util.Map<software.bernie.geckolib.constant.dataticket.DataTicket<?>, Object> dataMap = new java.util.HashMap<>();
        public float rotationAngle = 0f;
        public float rotationAxisYaw = 0f;

        @Override
        public <D> void addGeckolibData(software.bernie.geckolib.constant.dataticket.DataTicket<D> ticket, D data) {
            dataMap.put(ticket, data);
        }

        @Override
        public boolean hasGeckolibData(software.bernie.geckolib.constant.dataticket.DataTicket<?> ticket) {
            return dataMap.containsKey(ticket);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <D> D getGeckolibData(software.bernie.geckolib.constant.dataticket.DataTicket<D> ticket) {
            return (D) dataMap.get(ticket);
        }

        @Override
        public java.util.Map<software.bernie.geckolib.constant.dataticket.DataTicket<?>, Object> getDataMap() {
            return dataMap;
        }
    }
}
