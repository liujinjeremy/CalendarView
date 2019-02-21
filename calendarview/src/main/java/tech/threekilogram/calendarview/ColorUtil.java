package tech.threekilogram.calendarview;

import android.graphics.Color;

/**
 * @author Liujin 2019/2/21:21:26:10
 */
public class ColorUtil {

      private static int[] colors = {
          Color.WHITE,
          Color.YELLOW,
          Color.GREEN,
          Color.RED,
          Color.LTGRAY
      };

      public static int getColor ( int index ) {

            int i = index % colors.length;
            return colors[ i ];
      }
}
