package tech.threekilogram.calendarview;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-18
 * @time: 17:01
 */
public class BaseLineUtils {

      public static float getBaselineOffset ( Paint paint ) {

            FontMetrics fontMetrics = paint.getFontMetrics();
            float leading = fontMetrics.leading;
            float ascent = fontMetrics.ascent;
            float descent = fontMetrics.descent;

            return ( descent - ascent - leading ) / 2 - descent;
      }
}
