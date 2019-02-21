package tech.threekilogram.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Liujin 2019/2/21:13:00:25
 */
public class PagerMonthLayout extends FrameLayout {

      public PagerMonthLayout ( @NonNull Context context ) {

            this( context, null, 0 );
      }

      public PagerMonthLayout (
          @NonNull Context context, @Nullable AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public PagerMonthLayout (
          @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init();
      }

      private void init ( ) {

            setBackgroundColor( Color.BLUE );
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            super.onMeasure( widthMeasureSpec, heightMeasureSpec );
            int size = MeasureSpec.getSize( widthMeasureSpec );
            int[] ints = ViewMeasureUtils.measureView( this, widthMeasureSpec, heightMeasureSpec, size, 500 );
            setMeasuredDimension( ints[ 0 ], ints[ 1 ] );
      }
}
