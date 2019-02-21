package tech.threekilogram.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

/**
 * @author Liujin 2019/2/21:12:16:29
 */
public class LinearWeekBar extends LinearLayout {

      public LinearWeekBar ( Context context ) {

            this( context, null, 0 );
      }

      public LinearWeekBar (
          Context context, @Nullable AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public LinearWeekBar ( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init();
      }

      private void init ( ) {

            setOrientation( LinearLayout.HORIZONTAL );
            setBackgroundColor( Color.GRAY );
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int size = MeasureSpec.getSize( widthMeasureSpec );
            int[] ints = ViewMeasureUtils.measureView(
                this,
                widthMeasureSpec,
                heightMeasureSpec,
                size,
                200
            );
            setMeasuredDimension( ints[ 0 ], ints[ 1 ] );
      }
}
