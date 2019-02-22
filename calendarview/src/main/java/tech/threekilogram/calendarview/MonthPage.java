package tech.threekilogram.calendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Liujin 2019/2/21:21:32:09
 */
public class MonthPage extends ViewGroup {

      private static final String TAG = MonthPage.class.getSimpleName();

      private int mYear;
      private int mMonth;

      private int mMonthDayCount;
      private int mFirstDayOffset;

      public MonthPage ( Context context ) {

            super( context );
            init( context );
      }

      public MonthPage ( Context context, AttributeSet attrs ) {

            super( context, attrs );
            init( context );
      }

      public MonthPage ( Context context, AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init( context );
      }

      private void init ( Context context ) { }

      public void setDate ( int year, int month, int offset ) {

            mYear = year;
            mMonth = month;

            mMonthDayCount = CalendarUtils.monthDayCount( year, month );
            int dayOfWeek = CalendarUtils.dayOfWeek( year, month, mMonthDayCount );
            mFirstDayOffset = ( dayOfWeek + offset ) % 7;

            adjustChildCount();
      }

      private void adjustChildCount ( ) {

            int childCount = getChildCount();
            if( childCount < mMonthDayCount ) {
                  for( int i = childCount; i < mMonthDayCount; i++ ) {
                        addView( generateItemView() );
                  }
            } else if( mMonthDayCount < childCount ) {

                  for( int i = mMonthDayCount; i < childCount; i++ ) {
                        removeViewAt( i );
                  }
            }

            childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  bind( child, i );
            }
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            int cellWidth = widthSize / 7;
            int cellHeight = heightSize / 6;

            int cellWidthSpec = MeasureSpec.makeMeasureSpec( cellWidth, MeasureSpec.EXACTLY );
            int cellHeightSpec = MeasureSpec.makeMeasureSpec( cellHeight, MeasureSpec.EXACTLY );

            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  child.measure( cellWidthSpec, cellHeightSpec );
            }

            setMeasuredDimension( widthSize, heightSize );
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            View child = getChildAt( 0 );
            int cellWidth = child.getMeasuredWidth();
            int cellHeight = child.getMeasuredHeight();

            int count = getChildCount();
            for( int i = 0; i < count; i++ ) {
                  View view = getChildAt( i );
                  int left = ( ( i + mFirstDayOffset ) % 7 ) * cellWidth;
                  int top = ( i + mFirstDayOffset ) / 7 * cellHeight;

                  view.layout( left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight() );
            }
      }

      protected View generateItemView ( ) {

            TextView textView = new TextView( getContext() );
            textView.setGravity( Gravity.CENTER );
            return textView;
      }

      protected void bind ( View itemView, int dayOfMonth ) {

            ( (TextView) itemView ).setText( String.valueOf( dayOfMonth + 1 ) );
      }
}
