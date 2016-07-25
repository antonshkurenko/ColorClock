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

public class ValtoneClock extends WallpaperService {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);
  private static final int DELAY_MILLIS = 1000;
  private static final Paint PAINT = new Paint();

  static {
    PAINT.setColor(Color.WHITE);
    PAINT.setAntiAlias(true);
  }

  @Override public Engine onCreateEngine() {
    return new ValtoneClockEngine();
  }

  private class ValtoneClockEngine extends Engine {

    private final Calendar mCalendar = Calendar.getInstance();

    private final Handler mHandler;
    private Date mDate;
    private boolean mVisible = true;
    private int mHalfWidth, mHalfHeight;

    private final Runnable mDrawRunner = new Runnable() {
      @Override public void run() {
        draw();
      }
    };

    ValtoneClockEngine() {
      mHandler = new Handler();
      mDate = new Date();

      final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
      PAINT.setTypeface(typeface);
    }

    @Override public void onVisibilityChanged(boolean visible) {
      this.mVisible = visible;
      if (visible) {
        mHandler.post(mDrawRunner);
      } else {
        mHandler.removeCallbacks(mDrawRunner);
      }
    }

    @Override public void onSurfaceDestroyed(SurfaceHolder holder) {
      super.onSurfaceDestroyed(holder);
      this.mVisible = false;
      mHandler.removeCallbacks(mDrawRunner);
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      this.mHalfWidth = width >> 1;
      this.mHalfHeight = height >> 1;
      PAINT.setTextSize(width / 6);
      super.onSurfaceChanged(holder, format, width, height);
    }

    private void draw() {
      final SurfaceHolder holder = getSurfaceHolder();
      Canvas canvas = null;
      try {
        canvas = holder.lockCanvas();
        mDate = new Date();

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
