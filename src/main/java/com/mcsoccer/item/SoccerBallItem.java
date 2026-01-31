package com.mcsoccer.item;

import com.mcsoccer.entity.SoccerBallEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SoccerBallItem extends Item {

    public SoccerBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            Vec3 clickPos = context.getClickLocation();
            SoccerBallEntity ball = new SoccerBallEntity(level, clickPos.x, clickPos.y, clickPos.z);
            level.addFreshEntity(ball);
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
