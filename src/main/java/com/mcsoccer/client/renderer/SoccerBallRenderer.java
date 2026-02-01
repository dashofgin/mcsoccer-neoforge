package com.mcsoccer.client.renderer;

import com.mcsoccer.client.model.SoccerBallModel;
import com.mcsoccer.entity.SoccerBallEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SoccerBallRenderer extends GeoEntityRenderer<SoccerBallEntity, SoccerBallRenderer.State> {

    public SoccerBallRenderer(EntityRendererProvider.Context context) {
        super(context, new SoccerBallModel());
        this.withScale(0.5f);
    }

    public static class State extends net.minecraft.client.renderer.entity.state.EntityRenderState
            implements software.bernie.geckolib.renderer.base.GeoRenderState {
        private final java.util.Map<software.bernie.geckolib.constant.dataticket.DataTicket<?>, Object> dataMap = new java.util.HashMap<>();

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
