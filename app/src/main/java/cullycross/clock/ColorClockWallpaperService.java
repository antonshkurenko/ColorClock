package cullycross.clock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ColorClockWallpaperService extends WallpaperService {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);
  private static final int DELAY_MILLIS = 1000;
  private static final Paint PAINT = new Paint();

  static {
    PAINT.setColor(Color.WHITE);
    PAINT.setAntiAlias(true);
  }

  @Override public Engine onCreateEngine() {
    return new ColorClockEngine();
  }

  private class ColorClockEngine extends Engine {

    private final Calendar mCalendar = Calendar.getInstance();

    private final Handler mHandler = new Handler();
    private final Date mDate = new Date();
    private boolean mVisible = true;
    private int mHalfWidth, mHalfHeight;

    private final Runnable mDrawRunner = new Runnable() {
      @Override public void run() {
        draw();
      }
    };

    ColorClockEngine() {
      final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
      PAINT.setTypeface(typeface);
    }

    @Override public void onVisibilityChanged(boolean visible) {
      mVisible = visible;
      if (visible) {
        mHandler.post(mDrawRunner);
      } else {
        mHandler.removeCallbacks(mDrawRunner);
      }
    }

    @Override public void onSurfaceDestroyed(SurfaceHolder holder) {
      super.onSurfaceDestroyed(holder);
      mVisible = false;
      mHandler.removeCallbacks(mDrawRunner);
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      super.onSurfaceChanged(holder, format, width, height);
      mHalfWidth = width >> 1;
      mHalfHeight = height >> 1;
      PAINT.setTextSize(width / 6);
    }

    private void draw() {

      final SurfaceHolder holder = getSurfaceHolder();

      Canvas canvas = null;
      try {
        canvas = holder.lockCanvas();
        mDate.setTime(System.currentTimeMillis());

        final String date = DATE_FORMAT.format(mDate);

        mCalendar.setTime(mDate);

        final int hours = mCalendar.get(Calendar.HOUR_OF_DAY);
        final int minutes = mCalendar.get(Calendar.MINUTE);
        final int seconds = mCalendar.get(Calendar.SECOND);

        canvas.drawColor(
            Color.parseColor(String.format(Locale.US, "#%02d%02d%02d", hours, minutes, seconds)));

        canvas.drawText(date, mHalfWidth - PAINT.measureText(date) / 2, mHalfHeight, PAINT);
      } finally {
        if (canvas != null) {
          holder.unlockCanvasAndPost(canvas);
        }
      }

      mHandler.removeCallbacks(mDrawRunner);
      if (mVisible) {
        mHandler.postDelayed(mDrawRunner, DELAY_MILLIS);
      }
    }
  }
}
