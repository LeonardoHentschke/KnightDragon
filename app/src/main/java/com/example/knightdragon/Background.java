package com.example.knightdragon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Background {
    private final Bitmap tile;
    private final int screenWidth;
    private final int screenHeight;

    public Background(Context context) {
        tile = BitmapFactory.decodeResource(context.getResources(), R.drawable.tile_grass);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    public void draw(Canvas canvas, Paint paint) {
        for (int y = 0; y < screenHeight; y += tile.getHeight()) {
            for (int x = 0; x < screenWidth; x += tile.getWidth()) {
                canvas.drawBitmap(tile, x, y, paint);
            }
        }
    }
}
