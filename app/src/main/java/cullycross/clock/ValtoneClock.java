package cullycross.clock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.Date;


public class ValtoneClock extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new ValtoneClockEngine();
    }

    private class ValtoneClockEngine extends Engine {
        private final Handler mHandler;
        private Date mDate;
        private Paint mPaint;
        private boolean mVisible = true;
        private int mWidth, mHeight;

        private final Runnable mDrawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        ValtoneClockEngine() {
            mHandler = new Handler();
            mDate = new Date(System.currentTimeMillis());
            mPaint = new Paint();
            mPaint.setColor(Color.WHITE);
            mPaint.setAntiAlias(true);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
            mPaint.setTypeface(typeface);
        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            this.mVisible = visible;
            if (visible) {
                mHandler.post(mDrawRunner);
            } else {
                mHandler.removeCallbacks(mDrawRunner);
            }
        }
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.mVisible = false;
            mHandler.removeCallbacks(mDrawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            mPaint.setTextSize(width/6);
            super.onSurfaceChanged(holder, format, width, height);
        }

        private int fastConvertHexToDec(int number) {
            Log.d("NUMBER", " " + number);
            return Integer.valueOf(Integer.toString(number), 16);
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                long timeMillis = System.currentTimeMillis();
                mDate = new Date(timeMillis);
                String hh, mm, ss;
                hh = mDate.getHours()<10?("0"+mDate.getHours()):Integer.toString(mDate.getHours());
                mm = mDate.getMinutes()<10?("0"+mDate.getMinutes()):Integer.toString(mDate.getMinutes());
                ss = mDate.getSeconds()<10?("0"+mDate.getSeconds()):Integer.toString(mDate.getSeconds());
                String date = hh + " : " + mm + " : " + ss;
                int hours = fastConvertHexToDec(mDate.getHours());
                int minutes = fastConvertHexToDec(mDate.getMinutes());
                int seconds = fastConvertHexToDec(mDate.getSeconds());
                canvas.drawColor(Color.rgb(hours,minutes,seconds));
                canvas.drawText(date,mWidth/2 - mPaint.measureText(date)/2,mHeight/2,mPaint);
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            mHandler.removeCallbacks(mDrawRunner);
            if (mVisible) {
                mHandler.postDelayed(mDrawRunner, 1000);
            }
        }
    }
}
