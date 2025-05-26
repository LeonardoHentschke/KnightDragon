package com.example.knightdragon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable, SensorEventListener {
    private Thread thread;
    private boolean isPlaying;
    private final Background background;
    private final Character knight;
    private final Character dragon;
    private final SurfaceHolder holder;
    private final Paint paint;
    private final MediaPlayer musicPlayer;
    private float accelX = 0;
    private final List<Fire> fires = new ArrayList<>();
    private long lastFireTime = 0;
    private static final int FIRE_INTERVAL = 3000;
    private static final int FIRE_DURATION = 1000;
    private long lastFireDamageTime = 0;
    private static final int FIRE_DAMAGE_COOLDOWN = 1000;
    private final Random random = new Random();

    public GameView(Context context) {
        super(context);

        background = new Background(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        Character tempKnight = new Character(context, R.drawable.knight_walk1, R.drawable.knight_walk2, 0, 0, false);
        Character tempDragon = new Character(context, R.drawable.dragon_idle1, R.drawable.dragon_idle2, 0, 0, true);

        int knightY = (screenHeight - tempKnight.getSpriteHeight()) / 2;
        int dragonY = (screenHeight - tempDragon.getSpriteHeight()) / 2;

        knight = new Character(context, R.drawable.knight_walk1, R.drawable.knight_walk2, 200, knightY, false);
        dragon = new Character(context, R.drawable.dragon_idle1, R.drawable.dragon_idle2,
                screenWidth - (screenWidth / 6) - 200, dragonY, true);

        int spacing = (dragon.getX() - knight.getX()) / 4;
        for (int i = 1; i <= 3; i++) {
            int fireX = dragon.getX() - spacing * i;
            Fire fireInstance = new Fire(context, R.drawable.fireball, fireX, dragonY, tempDragon.getSpriteWidth(), tempDragon.getSpriteHeight());
            fires.add(fireInstance);
        }

        holder = getHolder();
        paint = new Paint();

        musicPlayer = MediaPlayer.create(context, R.raw.bg_music);
        musicPlayer.setLooping(true);
        musicPlayer.setVolume(0.3f, 0.3f);
        musicPlayer.start();

        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        setFocusable(true);
    }

    @Override
    public void run() {
        while (isPlaying) {
            if (!holder.getSurface().isValid()) continue;

            Canvas canvas = holder.lockCanvas();

            background.draw(canvas, paint);

            long currentTime = System.currentTimeMillis();

            if (currentTime - lastFireTime >= FIRE_INTERVAL) {
                int numberOfFires = random.nextInt(3) + 1;

                for (int i = 0; i < fires.size(); i++) {
                    fires.get(i).setVisible(i < numberOfFires);
                }

                lastFireTime = currentTime;
            } else if (currentTime - lastFireTime >= FIRE_DURATION) {
                for (Fire f : fires) {
                    f.setVisible(false);
                }
            }

            int deltaX = (int) -accelX * 5;
            Rect nextKnightBounds = new Rect(
                    knight.getBounds().left + deltaX,
                    knight.getBounds().top,
                    knight.getBounds().right + deltaX,
                    knight.getBounds().bottom
            );

            boolean intersectsFire = false;
            for (Fire f : fires) {
                if (f.isVisible() && Rect.intersects(nextKnightBounds, f.getBounds())) {
                    intersectsFire = true;
                    break;
                }
            }

            if (!Rect.intersects(nextKnightBounds, dragon.getBounds()) && !intersectsFire) {
                knight.move(deltaX);
            }

            for (Fire f : fires) {
                if (f.isVisible() && Rect.intersects(knight.getBounds(), f.getBounds())) {
                    long now = System.currentTimeMillis();
                    if (now - lastFireDamageTime >= FIRE_DAMAGE_COOLDOWN) {
                        knight.takeDamage(15);
                        lastFireDamageTime = now;
                    }
                }
            }

            knight.update();
            dragon.update();

            knight.draw(canvas, paint);
            dragon.draw(canvas, paint);
            for (Fire f : fires) {
                f.draw(canvas, paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
            musicPlayer.pause();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
        musicPlayer.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = -event.values[1];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Rect knightAttackRange = new Rect(knight.getBounds());
            knightAttackRange.right += 50;

            if (Rect.intersects(knightAttackRange, dragon.getBounds())) {
                dragon.takeDamage(10);
            }

            MediaPlayer attackSound = MediaPlayer.create(getContext(), R.raw.attack_sound);
            attackSound.setVolume(1.0f, 1.0f);
            attackSound.start();
        }
        return true;
    }
}
