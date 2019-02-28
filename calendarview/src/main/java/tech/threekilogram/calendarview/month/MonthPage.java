package tech.threekilogram.calendarview.month;

import static tech.threekilogram.calendarview.month.MonthDayItemView.IN_MONTH_SELECTED;
import static tech.threekilogram.calendarview.month.MonthDayItemView.IN_MONTH_UNSELECTED;
import static tech.threekilogram.calendarview.month.MonthDayItemView.OUT_MONTH;

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
                              child.setState( IN_MONTH_UNSELECTED );
                              child.setVisibility( VISIBLE );
                        } else {
                              child.setState( OUT_MONTH );
                              child.setVisibility( INVISIBLE );
                        }
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

            //Log.i( TAG, "onMeasure: " + mTopMoved + " " + mBottomMoved + " " + measuredHeight );
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
            }

            //Log.i( TAG, "onLayout: " + mState + " " + mTopMoved + " " + mBottomMoved );
      }

      private void setChildrenExpandState ( ) {

            int childCount = getChildCount();
            int offset = -mFirstDayOffset;

            for( int i = 0; i < childCount; i++ ) {

                  if( offset < 0 || offset > mMonthDayCount - 1 ) {
                        MonthDayItemView child = (MonthDayItemView) getChildAt( i );
                        child.setState( OUT_MONTH );
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
                        child.setState( IN_MONTH_UNSELECTED );
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
                  changeSelectedChild( v, date );
            }
      }

      private void changeSelectedChild ( View v, Date date ) {

            MonthDayItemView item = (MonthDayItemView) getChildAt( mCurrentSelectedPosition );
            item.setState( IN_MONTH_UNSELECTED );
            ( (MonthDayItemView) v ).setState( IN_MONTH_SELECTED );
            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  if( child == v ) {
                        mCurrentSelectedPosition = i;
                  }
            }
            mDate = date;
      }

      public int getPosition ( ) {

            return mPosition;
      }

      public int moving ( float dy ) {

            if( dy == 0 ) {
                  return 0;
            }

            int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
            int bottomDis = mPageHeight - ( topDis + mCellHeight );
            float radio = topDis * 1f / ( topDis + bottomDis );

            if( dy < 0 ) {

                  if( mState == STATE_FOLDED ) {
                        return 0;
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
                        return 0;
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

            int result = mBottomMoved + mTopMoved;
            if( result != 0 ) {
                  return result;
            }
            return 0;
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
