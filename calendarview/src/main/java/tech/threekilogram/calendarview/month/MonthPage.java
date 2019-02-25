package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import java.util.Date;
import tech.threekilogram.calendarview.CalendarUtils;

/**
 * @author Liujin 2019/2/21:21:32:09
 */
public class MonthPage extends ViewGroup implements OnClickListener {

      private static final String TAG = MonthPage.class.getSimpleName();

      private Date mDate;
      private int  mCurrentSelectedPosition;
      private int  mPosition;

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
                  View child = generateItemView();
                  addView( child );
                  child.setOnClickListener( this );
            }
      }

      public Date getDate ( ) {

            return mDate;
      }

      public void setInfo ( boolean isFirstDayMonday, Date date, int position, int selectedDayOfMonth ) {

            mDate = date;
            mPosition = position;

            calculateMonthInfo( isFirstDayMonday, date );
            setChildrenState( selectedDayOfMonth );
      }

      private void calculateMonthInfo ( boolean isFirstDayMonday, Date date ) {

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
      }

      private void setChildrenState ( int selectedDayOfMonth ) {

            int childCount = getChildCount();
            int offset = -mFirstDayOffset;
            int selected = selectedDayOfMonth <= mMonthDayCount ? selectedDayOfMonth : mMonthDayCount;

            Date firstDayOfMonth = CalendarUtils.firstDayOfMonth( mDate );

            for( int i = 0; i < childCount; i++ ) {
                  MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                  Date day = CalendarUtils.getDayByStep( firstDayOfMonth, offset );
                  child.bind( day );

                  if( offset < 0 || offset > mMonthDayCount - 1 ) {

                        child.setState( IMonthDayItem.OUT_MONTH );
                        child.setVisibility( INVISIBLE );
                  } else {

                        child.setVisibility( VISIBLE );
                        if( offset == selected - 1 ) {
                              child.setState( IMonthDayItem.IN_MONTH_SELECTED );
                              mCurrentSelectedPosition = i;
                        } else {
                              child.setState( IMonthDayItem.IN_MONTH_UNSELECTED );
                        }
                  }
                  offset++;
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

            return new MonthDayItemView( getContext() );
      }

      @Override
      public void onClick ( View v ) {

            MonthDayItemView itemView = (MonthDayItemView) v;
            Date date = itemView.getDate();
            if( !date.equals( mDate ) ) {
                  mDate = date;
                  int dayOfMonth = CalendarUtils.getDayOfMonth( date );
                  ( (MonthLayout) getParent() ).updateSelectedDayOfMonth( dayOfMonth );
            }
      }

      public int getPosition ( ) {

            return mPosition;
      }

      public void updateSelectedDayOfMonth ( int selectedDayOfMonth ) {

            int position = selectedDayOfMonth + mFirstDayOffset - 1;
            int max = mMonthDayCount + mFirstDayOffset - 1;
            if( position > max ) {
                  position = max;
            }

            if( position != mCurrentSelectedPosition ) {
                  ( (MonthDayItemView) getChildAt( position ) ).setState( IMonthDayItem.IN_MONTH_SELECTED );
                  ( (MonthDayItemView) getChildAt( mCurrentSelectedPosition ) ).setState( IMonthDayItem.IN_MONTH_UNSELECTED );
                  mCurrentSelectedPosition = position;
            }
      }
}
