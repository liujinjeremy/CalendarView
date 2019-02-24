package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.LinkedList;
import tech.threekilogram.calendarview.CalendarUtils;

/**
 * @author Liujin 2019/2/21:21:32:09
 */
public class MonthPage extends ViewGroup {

      private static final String TAG = MonthPage.class.getSimpleName();

      private int mYear;
      private int mMonth;
      int mPosition;

      private int mMonthDayCount;
      private int mFirstDayOffset;

      private int mCellWidth;
      private int mCellHeight;

      private LinkedList<View> mReusedChild = new LinkedList<>();

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

      public void setInfo ( int year, int month, int position ) {

            mYear = year;
            mMonth = month;
            mPosition = position;

            mMonthDayCount = CalendarUtils.monthDayCount( year, month );
            int dayOfWeek = CalendarUtils.dayOfWeek( year, month, 1 );
            if( dayOfWeek == 1 ) {
                  mFirstDayOffset = 6;
            } else {
                  mFirstDayOffset = dayOfWeek - 2;
            }

            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  mReusedChild.add( child );
            }
            removeAllViews();
            for( int i = 0; i < mMonthDayCount; i++ ) {
                  View child;
                  if( mReusedChild.isEmpty() ) {
                        child = generateItemView();
                  } else {
                        child = mReusedChild.pollFirst();
                  }
                  addView( child );
                  bind( child, i );
            }
      }

      void setCellSize ( int cellWidth, int cellHeight ) {

            mCellWidth = cellWidth;
            mCellHeight = cellHeight;
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            int cellWidthSpec = MeasureSpec.makeMeasureSpec( mCellWidth, MeasureSpec.EXACTLY );
            int cellHeightSpec = MeasureSpec.makeMeasureSpec( mCellHeight, MeasureSpec.EXACTLY );

            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  child.measure( cellWidthSpec, cellHeightSpec );
            }

            int count = mMonthDayCount + mFirstDayOffset;
            int lines = count % 7 == 0 ? count / 7 : count / 7 + 1;
            int resultHeight = lines == 5 ? lines * mCellHeight : heightSize;

            setMeasuredDimension( widthSize, resultHeight );
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
