package cullycross.clock;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.service.dreams.DreamService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) public class ColorClockDayDream
    extends DreamService {

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
    setContentView(mColorClockView);
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

  // todo(@tonyshkurenko), 8/3/16: make it work
  static class ColorClockView extends SurfaceView
      implements ColorClock.OnDrawListener, SurfaceHolder.Callback {

    private final ColorClock mColorClock;

    public ColorClockView(Context context) {
      super(context);
      mColorClock = new ColorClock(context);
      mColorClock.setOnDrawListener(this);
      getHolder().addCallback(this);
    }

    @Nullable @Override public Canvas onBeforeDraw() {
      return getHolder().lockCanvas();
    }

    @Override public void onAfterDraw(@NonNull Canvas canvas) {
      getHolder().unlockCanvasAndPost(canvas);
    }

    public void start() {
      mColorClock.start();
    }

    public void stop() {
      mColorClock.stop();
    }

    public void editConfiguration(int width, int height) {
      mColorClock.editConfiguration(width, height);
    }

    @Override public void surfaceCreated(SurfaceHolder surfaceHolder) {
      //mColorClock.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
      mColorClock.editConfiguration(width, height);
    }

    @Override public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
      mColorClock.stop();
    }
  }
}
