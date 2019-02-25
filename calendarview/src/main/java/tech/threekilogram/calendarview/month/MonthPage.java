package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Date;
import tech.threekilogram.calendarview.CalendarUtils;

/**
 * @author Liujin 2019/2/21:21:32:09
 */
public class MonthPage extends ViewGroup implements OnClickListener {

      private static final String TAG = MonthPage.class.getSimpleName();

      private Date mDate;
      int mPosition;

      private int mMonthDayCount;
      private int mFirstDayOffset;

      private int mCellWidth  = -1;
      private int mCellHeight = -1;

      public MonthPage ( Context context ) {

            this( context, null, 0 );
      }

      public MonthPage ( Context context, AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public MonthPage ( Context context, AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init();
      }

      private void init ( ) {

            for( int i = 0; i < 6 * 7; i++ ) {
                  addView( generateItemView() );
            }

            setBackgroundColor( Color.GRAY );
      }

      public Date getDate ( ) {

            return mDate;
      }

      public void setInfo ( boolean isFirstDayMonday, Date date, int position ) {

            mDate = date;
            mPosition = position;

            mMonthDayCount = CalendarUtils.monthDayCount( date );
            int dayOfWeek = CalendarUtils.weekOfMonthFirstDay( date );
            if( isFirstDayMonday ) {
                  if( dayOfWeek == 1 ) {
                        mFirstDayOffset = 6;
                  } else {
                        mFirstDayOffset = dayOfWeek - 2;
                  }
            } else {
                  mFirstDayOffset = dayOfWeek - 1;
            }

            int childCount = getChildCount();
            int offset = -mFirstDayOffset;
            Date firstDayOfMonth = CalendarUtils.firstDayOfMonth( date );

            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  Date day = CalendarUtils.getDayByStep( firstDayOfMonth, offset );
                  bind( child, CalendarUtils.getDayOfMonth( day ) );
                  offset++;

                  if( offset <= 0 || offset > mMonthDayCount ) {
                        child.setBackgroundColor( Color.WHITE );
                  } else {
                        child.setBackgroundColor( Color.TRANSPARENT );
                  }
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
            if( mCellWidth == -1 ) {
                  mCellWidth = widthSize / 7;
            }
            if( mCellHeight == -1 ) {
                  mCellHeight = heightSize / 6;
            }

            int cellWidthSpec = MeasureSpec.makeMeasureSpec( mCellWidth, MeasureSpec.EXACTLY );
            int cellHeightSpec = MeasureSpec.makeMeasureSpec( mCellHeight, MeasureSpec.EXACTLY );

            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  child.measure( cellWidthSpec, cellHeightSpec );
            }

            int count = mMonthDayCount + mFirstDayOffset;
            int lines = count % 7 == 0 ? count / 7 : count / 7 + 1;

            int resultHeight;
            if( lines == 6 ) {
                  resultHeight = heightSize;
            } else {
                  resultHeight = lines * mCellHeight;
            }

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
                  int left = ( i % 7 ) * cellWidth;
                  int top = i / 7 * cellHeight;
                  view.layout( left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight() );
            }
      }

      protected View generateItemView ( ) {

            TextView textView = new TextView( getContext() );
            textView.setGravity( Gravity.CENTER );
            textView.setTextColor( Color.BLUE );
            //textView.setOnClickListener( this );
            return textView;
      }

      protected void bind ( View itemView, int dayOfMonth ) {

            ( (TextView) itemView ).setText( String.valueOf( dayOfMonth ) );
      }

      @Override
      public void onClick ( View v ) {

            TextView textView = (TextView) v;
            String text = textView.getText().toString();
            int day = Integer.parseInt( text );
            Log.i( TAG, "onClick: " + day );
            textView.setTextColor( Color.RED );
      }
}
