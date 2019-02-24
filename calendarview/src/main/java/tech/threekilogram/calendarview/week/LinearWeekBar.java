package tech.threekilogram.calendarview.week;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import tech.threekilogram.calendarview.CalendarView.ViewComponent;
import tech.threekilogram.calendarview.ColorUtil;

/**
 * @author Liujin 2019/2/21:12:16:29
 */
public class LinearWeekBar extends ViewGroup implements ViewComponent {

      private static final String TAG = LinearWeekBar.class.getSimpleName();

      private boolean isFirstDayMonday = true;

      public LinearWeekBar ( Context context ) {

            super( context );
            init();
      }

      private void init ( ) {

            if( getChildCount() != 0 ) {
                  removeAllViews();
            }
            addChildren();
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

      public void setFirstDayMonday ( boolean firstDayMonday ) {

            isFirstDayMonday = firstDayMonday;

            try {
                  for( int i = 0; i < 7; i++ ) {
                        bind( getChildAt( i ), i );
                  }
            } catch(Exception e) {
                  /* nothing */
            }
      }

      @Override
      public View getView ( ) {

            return this;
      }

      private void addChildren ( ) {

            for( int i = 0; i < 7; i++ ) {
                  View view = generateItemView();
                  addView( view );
                  bind( view, i );
            }
      }

      protected View generateItemView ( ) {

            TextView textView = new TextView( getContext() );
            textView.setGravity( Gravity.CENTER );

            return textView;
      }

      protected void bind ( View textView, int index ) {

            ( (TextView) textView ).setText( getWeekDayString( index ) );
            int color = ColorUtil.getColor( index );
            textView.setBackgroundColor( color );
      }

      private String getWeekDayString ( int index ) {

            if( isFirstDayMonday ) {

                  String[] temp = { "一", "二", "三", "四", "五", "六", "日" };
                  return temp[ index ];
            } else {
                  String[] temp = { "日", "一", "二", "三", "四", "五", "六" };
                  return temp[ index ];
            }
      }
}
