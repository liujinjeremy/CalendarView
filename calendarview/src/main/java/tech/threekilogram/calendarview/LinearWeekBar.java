package tech.threekilogram.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import tech.threekilogram.calendarview.CalendarView.ViewComponent;
import tech.threekilogram.calendarview.CalendarView.WeekDay;

/**
 * @author Liujin 2019/2/21:12:16:29
 */
public class LinearWeekBar extends ViewGroup implements ViewComponent {

      private static final String TAG = LinearWeekBar.class.getSimpleName();

      private CalendarView mParent;

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

            setBackgroundColor( Color.GRAY );
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            int widthCellSize = widthSize / 7;

            int widthCellSpec = MeasureSpec.makeMeasureSpec( widthCellSize, MeasureSpec.EXACTLY );
            int heightCellSpec = MeasureSpec.makeMeasureSpec( heightSize, MeasureSpec.AT_MOST );

            int childCount = getChildCount();
            int heightResult = 0;
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  child.measure( widthCellSpec, heightCellSpec );
                  int measuredHeight = child.getMeasuredHeight();
                  if( measuredHeight > heightResult ) {
                        heightResult = measuredHeight;
                  }
            }

            Log.i( TAG, "onMeasure: " + widthSize + " " + heightResult + " " + childCount );
            setMeasuredDimension( widthSize, heightResult );
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            int widthUsed = 0;
            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  int width = child.getMeasuredWidth();
                  child.layout( widthUsed, 0, widthUsed + width, child.getMeasuredHeight() );
                  widthUsed += width;
            }
      }

      @Override
      public View getView ( ) {

            return this;
      }

      @Override
      public void attachParent ( CalendarView parent ) {

            mParent = parent;

            if( getChildCount() != 0 ) {
                  removeAllViews();
            }
            addChildren( parent );
      }

      private void addChildren ( CalendarView parent ) {

            for( int i = 0; i < 7; i++ ) {
                  WeekDay weekDay = parent.getWeekDay( i );
                  View view = generateItemView( weekDay );
                  addView( view );
            }
      }

      protected View generateItemView ( WeekDay weekDay ) {

            TextView textView = new TextView( getContext() );
            textView.setGravity( Gravity.CENTER );
            textView.setText( weekDay.toString().substring( 0, 3 ) );
            return textView;
      }
}
