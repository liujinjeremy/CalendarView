package tech.threekilogram.calendar.util;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

/**
 * 用于计算文字基准线与中线的距离
 *
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
