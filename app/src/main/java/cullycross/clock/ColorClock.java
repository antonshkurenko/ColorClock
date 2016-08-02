package cullycross.clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by: Anton Shkurenko (tonyshkurenko)
 * Project: ColorClock
 * Date: 8/1/16
 * Code style: SquareAndroid (https://github.com/square/java-code-styles)
 * Follow me: @tonyshkurenko
 */
final class ColorClock {

  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);
  private static final String FONTS_OPEN_SANS_REGULAR_TTF = "fonts/OpenSans-Regular.ttf";
  private static final int DELAY_MILLIS = 1000;

  private static final Paint PAINT = new Paint();

  static {
    PAINT.setColor(Color.WHITE);
    PAINT.setAntiAlias(true);
  }

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

  private OnDrawListener mOnDrawListener;

  ColorClock(Context ctx) {
    this(Typeface.createFromAsset(ctx.getAssets(), FONTS_OPEN_SANS_REGULAR_TTF));
  }

  ColorClock(Typeface typeface) {
    PAINT.setTypeface(typeface);
  }

  void start() {

    if (mOnDrawListener == null) {
      throw new NullPointerException("On draw listener is null before drawing!");
    }

    mVisible = true;
    mHandler.post(mDrawRunner);
  }

  void stop() {
    mVisible = false;
    mHandler.removeCallbacks(mDrawRunner);
  }

  void editConfiguration(int width, int height) {
    mHalfWidth = width >> 1;
    mHalfHeight = height >> 1;
    PAINT.setTextSize(width / 6);
  }

  void setOnDrawListener(OnDrawListener onDrawListener) {
    mOnDrawListener = onDrawListener;
  }

  private void draw() {

    mHandler.removeCallbacks(mDrawRunner);
    if (mVisible) {
      mHandler.postDelayed(mDrawRunner, DELAY_MILLIS);
    }

    final Canvas canvas = mOnDrawListener.onBeforeDraw();

    if (canvas == null) {
      return;
    }

    mDate.setTime(System.currentTimeMillis());

    final String date = DATE_FORMAT.format(mDate);

    mCalendar.setTime(mDate);

    final int hours = mCalendar.get(Calendar.HOUR_OF_DAY);
    final int minutes = mCalendar.get(Calendar.MINUTE);
    final int seconds = mCalendar.get(Calendar.SECOND);

    canvas.drawColor(
        Color.parseColor(String.format(Locale.US, "#%02d%02d%02d", hours, minutes, seconds)));

    canvas.drawText(date, mHalfWidth - PAINT.measureText(date) / 2, mHalfHeight, PAINT);

    mOnDrawListener.onAfterDraw(canvas);
  }

  interface OnDrawListener {

    @Nullable Canvas onBeforeDraw();

    void onAfterDraw(@NonNull Canvas canvas);
  }
}
