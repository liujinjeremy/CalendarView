package tech.threekilogram.calendarview.month;

import static tech.threekilogram.calendarview.month.MonthDayItemView.IN_MONTH_SELECTED;
import static tech.threekilogram.calendarview.month.MonthDayItemView.IN_MONTH_UNSELECTED;

import android.content.Context;
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

      private Date mDate;
      private int  mPosition;
      private int  mCurrentSelectedPosition;
      private int  mMonthDayCount;
      private int  mFirstDayOffset;
      private int  mState;

      private int mCellWidth  = -1;
      private int mCellHeight = -1;

      private int mPageHeight;
      private int mTopMoved;
      private int mBottomMoved;

      private int mTargetState = -1;

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

            //setBackgroundColor( Color.LTGRAY );
      }

      public Date getDate ( ) {

            return mDate;
      }

      public void setInfo ( boolean isFirstDayMonday, Date date, int position ) {

            mDate = date;
            mPosition = position;

            calculateMonthInfo( isFirstDayMonday, date );
            setChildrenState();
      }

      private void calculateMonthInfo ( boolean isFirstDayMonday, Date date ) {

            mMonthDayCount = CalendarUtils.getDayCountOfMonth( date );
            int dayOfWeek = CalendarUtils.getDayOfWeekAtMonthFirstDay( date );
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

            Date firstDayOfMonth = CalendarUtils.getFirstDayOfMonth( mDate );

            int beforeSelected = mCurrentSelectedPosition;

            for( int i = 0; i < childCount; i++ ) {
                  MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                  Date day = CalendarUtils.getDateByAddDay( firstDayOfMonth, offset );
                  child.bind( day );

                  if( offset < 0 || offset > mMonthDayCount - 1 ) {

                        if( mState == STATE_FOLDED ) {
                              child.setVisibility( VISIBLE );
                        } else {
                              child.setVisibility( INVISIBLE );
                        }
                        child.setState( IN_MONTH_UNSELECTED );
                  } else {

                        child.setVisibility( VISIBLE );
                        if( mDate.equals( day ) ) {
                              child.setState( IN_MONTH_SELECTED );
                              mCurrentSelectedPosition = i;
                        } else {
                              child.setState( IN_MONTH_UNSELECTED );
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

            int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
            int bottomDis = mPageHeight - ( topDis + mCellHeight );

            if( mTopMoved == 0 && mBottomMoved == 0 ) {
                  mState = STATE_EXPAND;
                  setChildrenExpandState();
            } else if( mTopMoved == -topDis && mBottomMoved == -bottomDis ) {
                  mState = STATE_FOLDED;
                  setChildrenFoldState();
            } else {
                  mState = STATE_MOVING;
                  setChildrenExpandState();
            }
      }

      private void setChildrenExpandState ( ) {

            int childCount = getChildCount();

            for( int i = 0; i < mFirstDayOffset; i++ ) {
                  MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                  if( child.getVisibility() == INVISIBLE ) {
                        break;
                  }
                  child.setVisibility( INVISIBLE );
            }

            for( int i = childCount - 1; i > mMonthDayCount - 1 + mFirstDayOffset; i-- ) {
                  MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                  if( child.getVisibility() == INVISIBLE ) {
                        break;
                  }
                  child.setVisibility( INVISIBLE );
            }
      }

      private void setChildrenFoldState ( ) {

            int childCount = getChildCount();

            for( int i = 0; i < mFirstDayOffset; i++ ) {
                  MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                  if( child.getVisibility() == VISIBLE ) {
                        break;
                  }
                  child.setVisibility( VISIBLE );
            }

            for( int i = childCount - 1; i > mMonthDayCount - 1 + mFirstDayOffset; i-- ) {
                  MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                  if( child.getVisibility() == VISIBLE ) {
                        break;
                  }
                  child.setVisibility( VISIBLE );
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
                  ( (MonthLayout) getParent() ).onSelectedDateChanged( date, mPosition );
            }
      }

      public void changeSelectedChild ( Date date ) {

            MonthDayItemView item = (MonthDayItemView) getChildAt( mCurrentSelectedPosition );
            item.setState( IN_MONTH_UNSELECTED );

            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                  if( child.getDate().equals( date ) ) {
                        mCurrentSelectedPosition = i;
                        child.setState( IN_MONTH_SELECTED );
                        break;
                  }
            }
            mDate = date;
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
                  if( -topDis + topMoved > 0 ) {
                        topMoved = topDis;
                  }
                  mTopMoved = -topDis + topMoved;

                  int bottomMoved = (int) ( dy - topMoved );
                  if( bottomMoved > bottomDis ) {
                        bottomMoved = bottomDis;
                  }
                  mBottomMoved = -bottomDis + bottomMoved;
            }

            requestLayout();
      }

      @Override
      public void computeScroll ( ) {

            super.computeScroll();

            if( mState == mTargetState ) {

                  if( mTargetState == STATE_FOLDED ) {
                        Log.i( TAG, "computeScroll: 已经折叠" );
                  } else if( mTargetState == STATE_EXPAND ) {
                        Log.i( TAG, "computeScroll: 已经展开" );
                  }

                  mTargetState = -1;
                  return;
            }

            int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
            int bottomDis = mPageHeight - ( topDis + mCellHeight );
            int total = topDis + bottomDis;

            if( mTargetState == STATE_EXPAND ) {
                  int i = total + mTopMoved + mBottomMoved + mCellHeight / 5;
                  moving( i );
            }
            if( mTargetState == STATE_FOLDED ) {
                  int i = mTopMoved + mBottomMoved - mCellHeight / 5;
                  moving( i );
            }
      }

      public void moveToExpand ( ) {

            moveToState( STATE_EXPAND );
      }

      public void moveToFold ( ) {

            moveToState( STATE_FOLDED );
      }

      private void moveToState ( int state ) {

            if( mTargetState == -1 && mState != state ) {

                  mTargetState = state;
                  computeScroll();
            }
      }

      public boolean isMovingToFinalState ( ) {

            return mTargetState != -1;
      }

      public int getState ( ) {

            return mState;
      }
}