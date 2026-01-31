package com.mcsoccer.data;

public class PlayerSoccerData {
    private boolean canTouchBall = true;
    private int touchCooldownTicks = 0;

    public boolean canTouchBall() {
        return canTouchBall;
    }

    public void blockTouch(int ticks) {
        this.canTouchBall = false;
        this.touchCooldownTicks = ticks;
    }

    public void tick() {
        if (touchCooldownTicks > 0) {
            touchCooldownTicks--;
            if (touchCooldownTicks <= 0) {
                canTouchBall = true;
            }
        }
    }
}
