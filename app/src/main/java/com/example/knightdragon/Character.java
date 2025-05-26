package com.example.knightdragon;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.BitmapFactory;

public class Character {
    private final Bitmap[] sprites;
    private int frame = 0;
    private int x;
    private final int y;
    private long lastFrameChangeTime = 0;
    private int hp = 100;

    public Character(Context context, int res1, int res2, int startX, int startY, boolean flipHorizontal) {
        sprites = new Bitmap[2];

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int desiredWidth = screenWidth / 6;

        Bitmap original1 = BitmapFactory.decodeResource(context.getResources(), res1);
        Bitmap original2 = BitmapFactory.decodeResource(context.getResources(), res2);

        float aspectRatio = (float) original1.getHeight() / original1.getWidth();
        int desiredHeight = (int) (desiredWidth * aspectRatio);

        original1 = Bitmap.createScaledBitmap(original1, desiredWidth, desiredHeight, true);
        original2 = Bitmap.createScaledBitmap(original2, desiredWidth, desiredHeight, true);

        if (flipHorizontal) {
            Matrix matrix = new Matrix();
            matrix.preScale(-1, 1);
            original1 = Bitmap.createBitmap(original1, 0, 0, original1.getWidth(), original1.getHeight(), matrix, true);
            original2 = Bitmap.createBitmap(original2, 0, 0, original2.getWidth(), original2.getHeight(), matrix, true);
        }

        sprites[0] = original1;
        sprites[1] = original2;

        this.x = startX;
        this.y = startY;
    }

    public void update() {
        if (System.currentTimeMillis() - lastFrameChangeTime > 300) {
            frame = (frame + 1) % 2;
            lastFrameChangeTime = System.currentTimeMillis();
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(sprites[frame], x, y, paint);

        int barWidth = sprites[frame].getWidth();
        int barHeight = 10;
        int barX = x;
        int barY = y - 20;

        paint.setColor(Color.RED);
        canvas.drawRect(barX, barY, barX + barWidth, barY + barHeight, paint);

        int maxHp = 100;
        float hpRatio = (float) hp / maxHp;
        paint.setColor(Color.GREEN);
        canvas.drawRect(barX, barY, barX + (int) (barWidth * hpRatio), barY + barHeight, paint);
    }

    public void move(int dx) {
        x += dx;
        if (x < 0) x = 0;
        int maxX = Resources.getSystem().getDisplayMetrics().widthPixels - sprites[0].getWidth();
        if (x > maxX) x = maxX;
    }

    public Rect getBounds() {
        return new Rect(x, y, x + sprites[0].getWidth(), y + sprites[0].getHeight());
    }

    public int getSpriteHeight() {
        return sprites[0].getHeight();
    }

    public int getSpriteWidth() {
        return sprites[0].getWidth();
    }

    public int getX() {
        return x;
    }

    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) hp = 0;
    }

}
