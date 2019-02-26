package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
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

      private static final int STATE_EXPAND = 0;
      private static final int STATE_MOVING = 1;
      private static final int STATE_FOLDED = 2;

      private Date    mDate;
      private int     mPosition;
      private int     mCurrentSelectedPosition;
      private int     mMonthDayCount;
      private int     mFirstDayOffset;
      private boolean isFirstDayMonday = true;
      private int     mState;

      private int mCellWidth  = -1;
      private int mCellHeight = -1;

      private int mPageHeight;
      private int mTopMoved;
      private int mBottomMoved;

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

            setBackgroundColor( Color.LTGRAY );
      }

      public Date getDate ( ) {

            return mDate;
      }

      public void setInfo ( boolean isFirstDayMonday, Date date, int position ) {

            this.isFirstDayMonday = isFirstDayMonday;
            mDate = date;
            mPosition = position;

            calculateMonthInfo( isFirstDayMonday, date );
            setChildrenState();
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

      private void setChildrenState ( ) {

            int childCount = getChildCount();
            int offset = -mFirstDayOffset;

            Date firstDayOfMonth = CalendarUtils.firstDayOfMonth( mDate );

            int beforeSelected = mCurrentSelectedPosition;

            for( int i = 0; i < childCount; i++ ) {
                  MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                  Date day = CalendarUtils.getDayByStep( firstDayOfMonth, offset );
                  child.bind( day );

                  if( offset < 0 || offset > mMonthDayCount - 1 ) {

                        if( mState == STATE_FOLDED ) {
                              child.setState( IMonthDayItem.IN_MONTH_UNSELECTED );
                              child.setVisibility( VISIBLE );
                        } else {
                              child.setState( IMonthDayItem.OUT_MONTH );
                              child.setVisibility( INVISIBLE );
                        }
                  } else {

                        child.setVisibility( VISIBLE );
                        if( mDate.equals( day ) ) {
                              child.setState( IMonthDayItem.IN_MONTH_SELECTED );
                              mCurrentSelectedPosition = i;
                        } else {
                              child.setState( IMonthDayItem.IN_MONTH_UNSELECTED );
                        }
                  }
                  offset++;
            }

            if( beforeSelected != mCurrentSelectedPosition ) {
                  requestLayout();
            }
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            if( mCellWidth == -1 || mCellHeight == -1 ) {
                  mCellWidth = widthSize / 7;
                  mCellHeight = heightSize / 6;

                  int cellWidthSpec = MeasureSpec.makeMeasureSpec( mCellWidth, MeasureSpec.EXACTLY );
                  int cellHeightSpec = MeasureSpec.makeMeasureSpec( mCellHeight, MeasureSpec.EXACTLY );

                  int childCount = getChildCount();
                  for( int i = 0; i < childCount; i++ ) {
                        View child = getChildAt( i );
                        child.measure( cellWidthSpec, cellHeightSpec );
                  }
            }

            int count = mMonthDayCount + mFirstDayOffset;
            int lines = count % 7 == 0 ? count / 7 : count / 7 + 1;
            int resultHeight = lines * mCellHeight;

            mPageHeight = resultHeight;

            int measuredHeight = resultHeight + mTopMoved + mBottomMoved;
            setMeasuredDimension( widthSize, measuredHeight );

            Log.i( TAG, "onMeasure: " + mTopMoved + " " + mBottomMoved + " " + measuredHeight );
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
                  view.layout(
                      left, top + mTopMoved, left + view.getMeasuredWidth(), top + view.getMeasuredHeight() + mTopMoved );
            }

            if( mTopMoved == 0 ) {
                  mState = STATE_EXPAND;
                  setChildrenExpandState();
            } else if( mTopMoved == -mCurrentSelectedPosition / 7 * mCellHeight ) {
                  mState = STATE_FOLDED;
                  setChildrenFoldState();
            } else {
                  mState = STATE_MOVING;
            }

            Log.i( TAG, "onLayout: " + mState + " " + mTopMoved + " " + mBottomMoved );
      }

      private void setChildrenExpandState ( ) {

            int childCount = getChildCount();
            int offset = -mFirstDayOffset;

            for( int i = 0; i < childCount; i++ ) {

                  if( offset < 0 || offset > mMonthDayCount - 1 ) {
                        MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                        child.setState( IMonthDayItem.OUT_MONTH );
                        child.setVisibility( INVISIBLE );
                  }
                  offset++;
            }
      }

      private void setChildrenFoldState ( ) {

            int childCount = getChildCount();
            int offset = -mFirstDayOffset;

            for( int i = 0; i < childCount; i++ ) {

                  if( offset < 0 || offset > mMonthDayCount - 1 ) {
                        MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                        child.setState( IMonthDayItem.IN_MONTH_UNSELECTED );
                        child.setVisibility( VISIBLE );
                  }
                  offset++;
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
                  setInfo( isFirstDayMonday, date, mPosition );
            }
      }

      public int getPosition ( ) {

            return mPosition;
      }

      public void moving ( float dy ) {

            if( dy == 0 ) {
                  return;
            }

            int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
            int bottomDis = mPageHeight - ( topDis + mCellHeight );
            float radio = topDis * 1f / ( topDis + bottomDis );

            if( dy < 0 ) {

                  if( mState == STATE_FOLDED ) {
                        return;
                  }

                  mTopMoved = (int) ( dy * radio );

                  if( mTopMoved < -topDis ) {
                        mTopMoved = -topDis;
                  }

                  mBottomMoved = (int) ( dy - mTopMoved );
                  if( bottomDis + mBottomMoved < 0 ) {
                        mBottomMoved = -bottomDis;
                  }
            } else {

                  if( mState == STATE_EXPAND ) {
                        return;
                  }

                  int topMoved = (int) ( dy * radio );
                  if( mTopMoved + topMoved > 0 ) {
                        topMoved = -mTopMoved;
                  }
                  mTopMoved += topMoved;

                  int bottomMoved = (int) ( dy - topMoved );
                  if( mBottomMoved + bottomMoved > 0 ) {
                        bottomMoved = -mBottomMoved;
                  }
                  mBottomMoved += bottomMoved;
            }

            requestLayout();
      }

      public void expanded ( ) {

            mTopMoved = 0;
            mBottomMoved = 0;
            requestLayout();
      }

      public void folded ( ) {

            mTopMoved = -mCurrentSelectedPosition / 7 * mCellHeight;
            mBottomMoved = ( -mTopMoved + mCellHeight ) - mPageHeight;

            requestLayout();
      }
}
