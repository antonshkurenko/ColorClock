package cullycross.clock;

import android.graphics.Canvas;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.view.SurfaceHolder;

public class ColorClockWallpaperService extends WallpaperService {

  @Override public Engine onCreateEngine() {
    return new ColorClockEngine();
  }

  private class ColorClockEngine extends Engine implements ColorClock.OnDrawListener {

    final ColorClock mColorClock;

    ColorClockEngine() {
      mColorClock = new ColorClock(ColorClockWallpaperService.this);
      mColorClock.setOnDrawListener(this);
    }

    @Override public void onVisibilityChanged(boolean visible) {
      if (visible) {
        mColorClock.start();
      } else {
        mColorClock.stop();
      }
    }

    @Override public void onSurfaceDestroyed(SurfaceHolder holder) {
      super.onSurfaceDestroyed(holder);
      mColorClock.stop();
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
      super.onSurfaceChanged(holder, format, width, height);
      mColorClock.editConfiguration(width, height);
    }

    @Override public Canvas onBeforeDraw() {
      return getSurfaceHolder().lockCanvas();
    }

    @Override public void onAfterDraw(@NonNull Canvas canvas) {
      getSurfaceHolder().unlockCanvasAndPost(canvas);
    }
  }
}
