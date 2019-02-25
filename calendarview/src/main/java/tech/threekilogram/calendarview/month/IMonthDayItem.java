package tech.threekilogram.calendarview.month;

import android.graphics.Canvas;
import android.view.View;
import java.util.Date;

/**
 * @author Liujin 2019/2/25:19:28:36
 */
public interface IMonthDayItem {

      int OUT_MONTH           = 10;
      int IN_MONTH_UNSELECTED = 11;
      int IN_MONTH_SELECTED   = 12;
      int TODAY               = 13;

      View getView ( );

      void bind ( Date date );

      void setState ( int state );

      void onDrawState ( int state, Canvas canvas );
}
