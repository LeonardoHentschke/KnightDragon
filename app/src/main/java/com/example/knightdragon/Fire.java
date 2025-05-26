package com.example.knightdragon;

import android.content.Context;
import android.graphics.*;
import android.graphics.BitmapFactory;

public class Fire {
    private final Bitmap sprite;
    private final int width;
    private final int height;
    private int x, y;
    private boolean isVisible = false;

    public Fire(Context context, int resId, int startX, int startY, int width, int height) {
        Bitmap original = BitmapFactory.decodeResource(context.getResources(), resId);
        this.width = width;
        this.height = height;
        this.sprite = Bitmap.createScaledBitmap(original, width, height, true);

        this.x = startX;
        this.y = startY;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (isVisible) {
            canvas.drawBitmap(sprite, x, y, paint);
        }
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Rect getBounds() {
        return new Rect(x, y, x + width, y + height);
    }
}
