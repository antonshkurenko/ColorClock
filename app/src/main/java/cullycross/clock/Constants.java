package cullycross.clock;

import android.graphics.Color;
import android.graphics.Paint;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Project: ColorClock
 * Code style: SquareAndroid (https://github.com/square/java-code-styles)
 * Follow me: @tonyshkurenko
 *
 * @author Anton Shkurenko
 * @since 8/20/16
 */

final class Constants {

  private Constants() {
    throw new UnsupportedOperationException("No instances!");
  }

  public static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.US);
  public static final String FONTS_OPEN_SANS_REGULAR_TTF = "fonts/OpenSans-Regular.ttf";
  public static final int DELAY_MILLIS = 1000;

  public static final Paint PAINT = new Paint();

  static {
    PAINT.setColor(Color.WHITE);
    PAINT.setAntiAlias(true);
  }
}
