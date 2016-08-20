package cullycross.clock;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.service.dreams.DreamService;
import android.view.View;
import android.view.ViewGroup;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) public class ColorClockDayDream
    extends DreamService {

  private static final String TAG = ColorClockDayDream.class.getSimpleName();

  private final Point mPointSize = new Point();

  private ColorClockView mColorClockView;

  @Override public void onAttachedToWindow() {
    super.onAttachedToWindow();

    // Exit dream upon user touch?
    setInteractive(true);

    // Hide system UI?
    setFullscreen(true);

    // Keep screen at full brightness?
    setScreenBright(true);

    // Set the content view, just like you would with an Activity.
    mColorClockView = new ColorClockView(this);
    setContentView(mColorClockView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT));
  }

  @Override public void onDreamingStarted() {
    super.onDreamingStarted();

    // Refresh Size of Window
    getWindowManager().getDefaultDisplay().getSize(mPointSize);

    final int windowWidth = mPointSize.x;
    final int windowHeight = mPointSize.y;

    mColorClockView.editConfiguration(windowWidth, windowHeight);
    mColorClockView.start();
  }

  @Override public void onDreamingStopped() {
    super.onDreamingStopped();

    mColorClockView.stop();
  }

  @Override public void onDetachedFromWindow() {
    super.onDetachedFromWindow();

    mColorClockView.stop();
  }

  static class ColorClockView extends View {

    private static final int DELAY_MILLIS = 1000;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);
    private static final String FONTS_OPEN_SANS_REGULAR_TTF = "fonts/OpenSans-Regular.ttf";

    private static final Paint PAINT = new Paint();

    static {
      PAINT.setColor(Color.WHITE);
      PAINT.setAntiAlias(true);
    }

    private final Date mDate = new Date();
    private final Calendar mCalendar = Calendar.getInstance();

    private final Runnable mDrawRunner = new Runnable() {
      @Override public void run() {
        invalidate();
      }
    };

    private boolean mVisible = true;
    private int mHalfWidth, mHalfHeight;

    public ColorClockView(Context context) {
      super(context);
      PAINT.setTypeface(Typeface.createFromAsset(context.getAssets(), FONTS_OPEN_SANS_REGULAR_TTF));
    }

    @Override protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);

      removeCallbacks(mDrawRunner);
      if (mVisible) {
        postDelayed(mDrawRunner, DELAY_MILLIS);
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
    }

    void start() {
      mVisible = true;
      post(mDrawRunner);
    }

    void stop() {
      mVisible = false;
      removeCallbacks(mDrawRunner);
    }

    void editConfiguration(int width, int height) {
      mHalfWidth = width >> 1;
      mHalfHeight = height >> 1;
      PAINT.setTextSize(width / 6);
    }
  }
}
