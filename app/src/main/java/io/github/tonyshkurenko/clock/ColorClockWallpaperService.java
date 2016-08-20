package io.github.tonyshkurenko.clock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ColorClockWallpaperService extends WallpaperService {

  @Override public Engine onCreateEngine() {
    return new ColorClockEngine();
  }

  private class ColorClockEngine extends Engine {

    private final Runnable mDrawRunner = new Runnable() {
      @Override public void run() {
        draw();
      }
    };

    private final Calendar mCalendar = Calendar.getInstance();

    private final Handler mHandler = new Handler();
    private final Date mDate = new Date();
    private boolean mVisible = true;
    private int mHalfWidth, mHalfHeight;

    ColorClockEngine() {
      Constants.PAINT.setTypeface(Constants.PAINT.setTypeface(
          Typeface.createFromAsset(getAssets(), Constants.FONTS_OPEN_SANS_REGULAR_TTF)));
    }

    @Override public void onVisibilityChanged(boolean visible) {
      if (visible) {
        start();
      } else {
        stop();
      }
    }

    @Override public void onSurfaceDestroyed(SurfaceHolder holder) {
      super.onSurfaceDestroyed(holder);
      stop();
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      super.onSurfaceChanged(holder, format, width, height);
      mHalfWidth = width >> 1;
      mHalfHeight = height >> 1;
      Constants.PAINT.setTextSize(width / 6);
    }

    private void draw() {

      mHandler.removeCallbacks(mDrawRunner);
      if (mVisible) {
        mHandler.postDelayed(mDrawRunner, Constants.DELAY_MILLIS);
      }

      final Canvas canvas = getSurfaceHolder().lockCanvas();

      if (canvas == null) {
        return;
      }

      mDate.setTime(System.currentTimeMillis());

      final String date = Constants.DATE_FORMAT.format(mDate);

      mCalendar.setTime(mDate);

      final int hours = mCalendar.get(Calendar.HOUR_OF_DAY);
      final int minutes = mCalendar.get(Calendar.MINUTE);
      final int seconds = mCalendar.get(Calendar.SECOND);

      canvas.drawColor(
          Color.parseColor(String.format(Locale.US, "#%02d%02d%02d", hours, minutes, seconds)));

      canvas.drawText(date, mHalfWidth - Constants.PAINT.measureText(date) / 2, mHalfHeight,
          Constants.PAINT);

      getSurfaceHolder().unlockCanvasAndPost(canvas);
    }

    private void start() {
      mVisible = true;
      mHandler.post(mDrawRunner);
    }

    private void stop() {
      mVisible = false;
      mHandler.removeCallbacks(mDrawRunner);
    }
  }
}
