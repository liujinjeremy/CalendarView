package tech.threekilogram.calendarview.week;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import tech.threekilogram.calendarview.CalendarView;
import tech.threekilogram.calendarview.CalendarView.ViewComponent;

/**
 * @author Liujin 2019/2/21:12:16:29
 */
public class LinearWeekBar extends ViewGroup implements ViewComponent {

      private static final String TAG = LinearWeekBar.class.getSimpleName();

      /**
       * 每周第一天是周一时,显示的text
       */
      private static String[] firstMonday = { "一", "二", "三", "四", "五", "六", "日" };
      /**
       * 每周第一天是周日时,显示的text
       */
      private static String[] firstSunday = { "日", "一", "二", "三", "四", "五", "六" };

      /**
       * parent 用于通信
       */
      private CalendarView mCalendarView;

      public LinearWeekBar ( Context context ) {

            this( context, null, 0 );
      }

      public LinearWeekBar ( Context context, AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public LinearWeekBar ( Context context, AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
      }

      /**
       * 宽度均分为7份,高度包裹自己
       */
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

      /**
       * 从左到右依次布局
       */
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

      /**
       * 生成每个条目
       */
      private TextView generateItemView ( String text ) {

            TextView textView = new TextView( getContext() );
            textView.setText( text );
            textView.setGravity( Gravity.CENTER );
            textView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 20 );
            textView.setPadding( 0, 10, 0, 10 );
            return textView;
      }

      @Override
      public View getView ( ) {

            return this;
      }

      @Override
      public void bindParent ( CalendarView calendarView ) {

            mCalendarView = calendarView;

            boolean firstDayMonday = calendarView.isFirstDayMonday();
            String[] texts;
            if( firstDayMonday ) {
                  texts = firstMonday;
            } else {
                  texts = firstSunday;
            }
            for( int i = 0; i < 7; i++ ) {
                  addView( generateItemView( texts[ i ] ) );
            }
      }

      @Override
      public void notifyFirstDayIsMondayChanged ( boolean isFirstDayMonday ) {

            String[] texts;
            if( isFirstDayMonday ) {
                  texts = firstMonday;
            } else {
                  texts = firstSunday;
            }
            for( int i = 0; i < 7; i++ ) {
                  View view = getChildAt( i );
                  ( (TextView) view ).setText( texts[ i ] );
            }
      }
}
