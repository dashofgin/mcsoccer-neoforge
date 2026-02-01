package com.mcsoccer.client.model;

import com.mcsoccer.MCSoccerMod;
import com.mcsoccer.entity.SoccerBallEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class SoccerBallModel extends DefaultedEntityGeoModel<SoccerBallEntity> {

    public SoccerBallModel() {
        super(ResourceLocation.fromNamespaceAndPath(MCSoccerMod.MOD_ID, "soccer_ball"));
    }
}
