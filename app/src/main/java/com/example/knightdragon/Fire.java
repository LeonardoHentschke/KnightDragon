package com.example.knightdragon;

import android.content.Context;
import android.graphics.*;
import android.graphics.BitmapFactory;

public class Fire {
    private Bitmap sprite;
    private int x, y;
    private boolean visible = false;

    public Fire(Context context, int resId, int x, int y) {
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), resId);
        int desiredWidth = context.getResources().getDisplayMetrics().widthPixels / 8;
        float aspectRatio = (float) original.getHeight() / original.getWidth();
        int desiredHeight = (int) (desiredWidth * aspectRatio);

        this.sprite = Bitmap.createScaledBitmap(original, desiredWidth, desiredHeight, true);
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (visible) {
            canvas.drawBitmap(sprite, x, y, paint);
        }
    }

    public void setVisible(boolean value) {
        visible = value;
    }

    public boolean isVisible() {
        return visible;
    }

    public Rect getBounds() {
        return new Rect(x, y, x + sprite.getWidth(), y + sprite.getHeight());
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
