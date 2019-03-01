package tech.threekilogram.calendarview.month;

import static tech.threekilogram.calendarview.month.MonthDayItemView.SELECTED;
import static tech.threekilogram.calendarview.month.MonthDayItemView.UNSELECTED;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import java.util.Date;
import tech.threekilogram.calendarview.CalendarUtils;

/**
 * {@link MonthLayout}的一个页面
 *
 * @author Liujin 2019/2/21:21:32:09
 */
public class MonthPage extends ViewGroup implements OnClickListener {

      private static final String TAG = MonthPage.class.getSimpleName();

      /**
       * 当前状态之一:已经展开
       */
      private static final int STATE_EXPAND        = 0;
      /**
       * 当前状态之一:正在展开
       */
      private static final int STATE_EXPAND_MOVING = 1;
      /**
       * 当前状态之一:已经折叠
       */
      private static final int STATE_FOLDED        = 2;
      /**
       * 当前状态之一:正在折叠
       */
      private static final int STATE_FOLDED_MOVING = 3;

      /**
       * 当前页面日期
       */
      private Date mDate;
      /**
       * 当前页面位于pager中的位置
       */
      private int  mPosition;
      /**
       * 当前页面中选中的日期
       */
      private int  mCurrentSelectedPosition;
      /**
       * 日期代表的月份的总天数
       */
      private int  mMonthDayCount;
      /**
       * 日期的第一天是周几
       */
      private int  mFirstDayOffset;
      /**
       * 当前状态:{@link #STATE_EXPAND},{@link #STATE_EXPAND_MOVING},{@link #STATE_FOLDED},{@link #STATE_FOLDED_MOVING}
       */
      private int  mState;

      /**
       * 显示天的view的宽度
       */
      private int mCellWidth  = -1;
      /**
       * 显示天的view的高度
       */
      private int mCellHeight = -1;
      /**
       * 根据总天数计算的页面高度
       */
      private int mPageHeight;

      /**
       * 辅助类用于界面展开折叠
       */
      private ExpandFold mExpandFold = new ExpandFold();

      public MonthPage ( Context context ) {

            super( context );
            init();
      }

      private void init ( ) {
            /*每个月最多使用7列6行个子view就能包含所有日期*/
            for( int i = 0; i < 6 * 7; i++ ) {
                  View child = generateItemView();
                  addView( child );
                  child.setOnClickListener( this );
            }
      }

      Date getDate ( ) {

            return mDate;
      }

      /**
       * 设置页面显示信息
       *
       * @param isFirstDayMonday 每周第一天是周一或者周日,true:周一
       * @param monthMode true:月显示模式,false:周显示模式
       * @param date 显示日期
       * @param position 位于pager的位置
       */
      void setInfo ( boolean isFirstDayMonday, boolean monthMode, Date date, int position ) {

            mDate = date;
            mPosition = position;

            if( monthMode ) {
                  mState = STATE_EXPAND;
            } else {
                  mState = STATE_FOLDED;
            }

            calculateMonthInfo( isFirstDayMonday, date );
            setChildrenState();
      }

      /**
       * 计算总天数,这个月第一天是周几
       */
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

      /**
       * 设置显示状态
       */
      private void setChildrenState ( ) {

            int childCount = getChildCount();
            int offset = -mFirstDayOffset;

            Date firstDayOfMonth = CalendarUtils.getFirstDayOfMonth( mDate );

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
                        child.setState( UNSELECTED );
                  } else {

                        child.setVisibility( VISIBLE );
                        if( mDate.equals( day ) ) {
                              child.setState( SELECTED );
                              mCurrentSelectedPosition = i;
                        } else {
                              child.setState( UNSELECTED );
                        }
                  }
                  offset++;
            }
            requestLayout();
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );

            MonthLayout parent = (MonthLayout) getParent();
            mCellWidth = parent.getCellWidth();
            mCellHeight = parent.getCellHeight();

            View view = getChildAt( 0 );
            if( view.getMeasuredWidth() != mCellWidth || view.getMeasuredHeight() != mCellHeight ) {
                  int cellWidthSpec = MeasureSpec.makeMeasureSpec( mCellWidth, MeasureSpec.EXACTLY );
                  int cellHeightSpec = MeasureSpec.makeMeasureSpec( mCellHeight, MeasureSpec.EXACTLY );

                  int childCount = getChildCount();
                  for( int i = 0; i < childCount; i++ ) {
                        View child = getChildAt( i );
                        child.measure( cellWidthSpec, cellHeightSpec );
                  }
            }

            /* 设置高度信息 */
            int count = mMonthDayCount + mFirstDayOffset;
            int lines = count % 7 == 0 ? count / 7 : count / 7 + 1;
            int resultHeight = lines * mCellHeight;
            mPageHeight = resultHeight;

            /* 当折叠或者展开时,计算偏移量 */
            if( mState == STATE_FOLDED ) {
                  mExpandFold.calculateFoldMoved();
            } else if( mState == STATE_EXPAND ) {
                  mExpandFold.calculateExpandMoved();
            }

            int measuredHeight = mExpandFold.calculateMeasuredHeight( resultHeight );
            setMeasuredDimension( widthSize, measuredHeight );
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            View child = getChildAt( 0 );
            int cellWidth = child.getMeasuredWidth();
            int cellHeight = child.getMeasuredHeight();

            int count = getChildCount();
            int topMoved = mExpandFold.getTopMoved();
            for( int i = 0; i < count; i++ ) {
                  View view = getChildAt( i );
                  int left = ( i % 7 ) * cellWidth;
                  int top = i / 7 * cellHeight;
                  view.layout(
                      left,
                      top + topMoved,
                      left + view.getMeasuredWidth(),
                      top + view.getMeasuredHeight() + topMoved
                  );
            }
      }

      /**
       * 创建子view
       *
       * @return 子view
       */
      protected View generateItemView ( ) {

            return new MonthDayItemView( getContext() );
      }

      /**
       * 子view点击事件
       */
      @Override
      public void onClick ( View v ) {

            MonthDayItemView itemView = (MonthDayItemView) v;
            Date date = itemView.getDate();
            /* 日期变化了 */
            if( !date.equals( mDate ) ) {
                  MonthLayout parent = (MonthLayout) getParent();
                  if( mState == STATE_EXPAND ) {
                        parent.onDateChanged( date, mPosition, true );
                  }
                  if( mState == STATE_FOLDED ) {
                        parent.onDateChanged( date, mPosition, false );
                  }
                  parent.onNewDateSelected( date );
            }
      }

      int getPosition ( ) {

            return mPosition;
      }

      /**
       * 滑动一段距离,直至展开至月显示模式,或者折叠到周显示模式
       *
       * @param dy 距离
       */
      void moving ( float dy ) {

            mExpandFold.move( dy );
      }

      /**
       * 展开时,如果日期不处于这个月,那么设为不可见
       */
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

      /**
       * 折叠时,如果日期不处于这个月,那么设为可见
       */
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

      /**
       * 用于手势释放时,展开或者折叠到最终状态
       */
      @Override
      public void computeScroll ( ) {

            super.computeScroll();

            if( mExpandFold.isMovingToFinalState() ) {
                  mExpandFold.computeScroll();
            }
      }

      void moveToExpand ( ) {

            mExpandFold.moveToState( STATE_EXPAND );
      }

      void moveToFold ( ) {

            mExpandFold.moveToState( STATE_FOLDED );
      }

      /**
       * 是否已经滑动到最终状态
       *
       * @return true:滑动到最终状态
       */
      boolean isMovingToFinalState ( ) {

            return mExpandFold.isMovingToFinalState();
      }

      private class ExpandFold {

            /**
             * 所有子view的top偏移
             */
            private int mTopMoved;
            /**
             * 当前页面bottom偏移
             */
            private int mBottomMoved;
            /**
             * 手势释放后,折叠或者展开
             */
            private int mTargetState = -1;

            /**
             * 滑动到指定状态
             *
             * @param state 最终状态
             */
            private void moveToState ( int state ) {

                  if( mTargetState == -1 && mState != state ) {

                        mTargetState = state;
                        computeScroll();
                  }
            }

            private boolean isMovingToFinalState ( ) {

                  return mTargetState != -1;
            }

            private void move ( float dy ) {

                  if( mState == STATE_FOLDED ) {
                        mState = STATE_FOLDED_MOVING;
                        setChildrenExpandState();
                  } else if( mState == STATE_EXPAND ) {
                        mState = STATE_EXPAND_MOVING;
                  }

                  if( mState == STATE_EXPAND_MOVING ) {

                        int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
                        int bottomDis = mPageHeight - ( topDis + mCellHeight );
                        float radio = topDis * 1f / ( topDis + bottomDis );

                        int topMoved = (int) ( dy * radio );
                        if( topMoved < -topDis ) {
                              topMoved = -topDis;
                        }
                        if( topMoved > 0 ) {
                              topMoved = 0;
                        }

                        int bottomMoved = (int) ( dy - topMoved );
                        if( bottomMoved < -bottomDis ) {
                              bottomMoved = -bottomDis;
                        }
                        if( bottomMoved > 0 ) {
                              bottomMoved = 0;
                        }

                        mTopMoved = topMoved;
                        mBottomMoved = bottomMoved;
                        requestLayout();
                  } else if( mState == STATE_FOLDED_MOVING ) {

                        int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
                        int bottomDis = mPageHeight - ( topDis + mCellHeight );
                        float radio = topDis * 1f / ( topDis + bottomDis );

                        int topMoved = (int) ( dy * radio );
                        int bottomMoved = (int) ( dy - topMoved );

                        mTopMoved = -topDis + topMoved;
                        if( mTopMoved > 0 ) {
                              mTopMoved = 0;
                        }
                        if( mTopMoved < -topDis ) {
                              mTopMoved = -topDis;
                        }

                        mBottomMoved = -bottomDis + bottomMoved;
                        if( mBottomMoved > 0 ) {
                              mBottomMoved = 0;
                        }
                        if( mBottomMoved < -bottomDis ) {
                              mBottomMoved = -bottomDis;
                        }
                        requestLayout();
                  }
            }

            private void computeScroll ( ) {

                  int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
                  int bottomDis = mPageHeight - ( topDis + mCellHeight );

                  if( mTopMoved == 0 && mBottomMoved == 0 ) {

                        /* 已经展开 */
                        mState = STATE_EXPAND;
                        setChildrenExpandState();
                        ( (MonthLayout) getParent() ).onDateChanged( mDate, mPosition, true );
                        mTargetState = -1;
                  } else if( mTopMoved == -topDis && mBottomMoved == -bottomDis ) {

                        /* 已经折叠 */
                        mState = STATE_FOLDED;
                        setChildrenFoldState();
                        ( (MonthLayout) getParent() ).onDateChanged( mDate, mPosition, false );
                        mTargetState = -1;
                  } else {

                        /* 正在展开折叠,每次增加/减少mCellHeight / 5距离 */
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
            }

            private int getTopMoved ( ) {

                  return mTopMoved;
            }

            private int getBottomMoved ( ) {

                  return mBottomMoved;
            }

            private void calculateFoldMoved ( ) {

                  int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
                  int bottomDis = mPageHeight - ( topDis + mCellHeight );
                  mTopMoved = -topDis;
                  mBottomMoved = -bottomDis;
            }

            private void calculateExpandMoved ( ) {

                  mTopMoved = 0;
                  mBottomMoved = 0;
            }

            private int calculateMeasuredHeight ( int linesHeight ) {

                  return linesHeight + mTopMoved + mBottomMoved;
            }
      }
}