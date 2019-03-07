package tech.threekilogram.calendarview.month;

import static tech.threekilogram.calendarview.month.MonthDayView.SELECTED;
import static tech.threekilogram.calendarview.month.MonthDayView.UNSELECTED;

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
      private static final int STATE_EXPAND  = 0;
      /**
       * 当前状态之一:已经折叠
       */
      private static final int STATE_FOLDED  = 1;
      /**
       * 当前状态之一:正在折叠/展开
       */
      private static final int STATE_MOVING  = 2;
      /**
       * 当前状态之一:使用动画在手指离开后,折叠或者展开
       */
      private static final int STATE_ANIMATE = 3;

      /**
       * 记录当前页面日期
       */
      private Date    mDate;
      /**
       * 记录当前页面位于{@link MonthLayout#getAdapter()}中的位置
       */
      private int     mPosition;
      /**
       * 记录当前页面第一天是否是周一
       */
      private boolean isFirstDayMonday;
      /**
       * {@link #mDate}代表的日期在当前布局中的位置索引
       */
      private int     mCurrentSelectedPosition;
      /**
       * {@link #mDate}代表的月份的总天数
       */
      private int     mMonthDayCount;
      /**
       * {@link #mDate}位于的月份第一天在布局中的位置索引
       */
      private int     mFirstDayOffset;
      /**
       * 当前状态
       */
      public  int     mState;

      /**
       * 显示天的view的宽度
       */
      private int        mCellWidth  = -1;
      /**
       * 显示天的view的高度
       */
      private int        mCellHeight = -1;
      /**
       * 根据总天数计算的页面高度
       */
      private int        mPageHeight;
      /**
       * 辅助类用于界面展开折叠
       */
      private MoveHelper mMoveHelper;

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

            mMoveHelper = new MoveHelper();
      }

      /**
       * 创建子view
       *
       * @return 子view
       */
      protected View generateItemView ( ) {

            return new MonthDayView( getContext() );
      }

      Date getDate ( ) {

            return mDate;
      }

      int getPosition ( ) {

            return mPosition;
      }

      /**
       * 设置页面显示信息
       *
       * @param isFirstDayMonday 每周第一天是周一或者周日,true:周一
       * @param monthMode true:月显示模式,false:周显示模式
       * @param date 显示日期
       * @param position 位于pager的位置
       */
      void setInfo ( Date date, int position, boolean isFirstDayMonday, boolean monthMode ) {

            mDate = date;
            mPosition = position;
            this.isFirstDayMonday = isFirstDayMonday;

            if( monthMode ) {
                  mState = STATE_EXPAND;
            } else {
                  mState = STATE_FOLDED;
            }

            calculateMonthInfo( isFirstDayMonday, date );
            bindChildrenDate();
            requestLayout();
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
      private void bindChildrenDate ( ) {

            int childCount = getChildCount();
            int offset = -mFirstDayOffset;

            Date firstDayOfMonth = CalendarUtils.getFirstDayOfMonth( mDate );

            for( int i = 0; i < childCount; i++ ) {
                  MonthDayView child = (MonthDayView) getChildAt( i );
                  Date day = CalendarUtils.getDateByAddDay( firstDayOfMonth, offset );
                  child.bind( day );

                  if( mDate.equals( day ) ) {
                        child.setState( SELECTED );
                        mCurrentSelectedPosition = i;
                  } else {
                        child.setState( UNSELECTED );
                  }

                  offset++;
            }
      }

      /**
       * 展开时,如果日期不处于这个月,那么设为不可见
       */
      private void setChildrenExpandFoldState ( ) {

            int childCount = getChildCount();
            int offset = -mFirstDayOffset;

            boolean visible = mMoveHelper.isCurrentAtFoldState();

            for( int i = 0; i < childCount; i++ ) {
                  MonthDayView child = (MonthDayView) getChildAt( i );

                  if( offset < 0 || offset > mMonthDayCount - 1 ) {

                        if( visible ) {
                              child.setVisibility( VISIBLE );
                        } else {
                              child.setVisibility( INVISIBLE );
                        }
                  } else {

                        child.setVisibility( VISIBLE );
                  }
                  offset++;
            }
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );

            MonthLayout parent = (MonthLayout) getParent();
            mCellWidth = parent.getCellWidth();
            mCellHeight = parent.getCellHeight();

            /* 如果需要测量,那么测量所有child */
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

            /* 设置children可见性 */
            setChildrenExpandFoldState();

            /* 设置高度信息 */
            int count = mMonthDayCount + mFirstDayOffset;
            int lines = count % 7 == 0 ? count / 7 : count / 7 + 1;
            int resultHeight = lines * mCellHeight;
            mPageHeight = resultHeight;

            /* 当折叠或者展开时,计算偏移量 */
            if( mState == STATE_FOLDED ) {
                  mMoveHelper.calculateFoldMoved();
            } else if( mState == STATE_EXPAND ) {
                  mMoveHelper.calculateExpandMoved();
            }

            int measuredHeight = mMoveHelper.calculateMeasuredHeight( resultHeight );
            setMeasuredDimension( widthSize, measuredHeight );
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            View child = getChildAt( 0 );
            int cellWidth = child.getMeasuredWidth();
            int cellHeight = child.getMeasuredHeight();

            int count = getChildCount();
            int topMoved = (int) mMoveHelper.mTopMoved;
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
       * 用于手势释放时,展开或者折叠到最终状态
       */
      @Override
      public void computeScroll ( ) {

            super.computeScroll();
            mMoveHelper.animateIfNeed();
      }

      /**
       * 子view点击事件
       */
      @Override
      public void onClick ( View v ) {

            if( mState == STATE_MOVING || mState == STATE_ANIMATE ) {
                  return;
            }

            MonthDayView itemView = (MonthDayView) v;
            Date date = itemView.getDate();
            /* 日期变化了 */
            if( !date.equals( mDate ) ) {
                  MonthLayout parent = (MonthLayout) getParent();
                  parent.onNewDateClicked( date, mPosition );
            }
      }

      /**
       * 滑动一段距离,直至展开至月显示模式,或者折叠到周显示模式
       *
       * @param dy 距离
       */
      public void onVerticalMoveBy ( float dy ) {

            mMoveHelper.move( dy );
      }

      void moveToExpand ( ) {

            mMoveHelper.setAnimateState( 1 );
      }

      void moveToFold ( ) {

            mMoveHelper.setAnimateState( -1 );
      }

      void onDownTouchEvent ( ) {

            mMoveHelper.forceStopAnimateIfRunning();
      }

      boolean isMoving ( ) {

            return mState == STATE_MOVING || mState == STATE_ANIMATE;
      }

      void onUpTouchEvent ( float totalDy, boolean isMonthMode ) {

            if( totalDy > 0 ) {
                  moveToExpand();
                  return;
            }

            if( totalDy < 0 ) {
                  moveToFold();
                  return;
            }

            if( totalDy == 0 ) {
                  mMoveHelper.checkAnimateState( isMonthMode );
            }
      }

      private class MoveHelper {

            /**
             * 所有子view的top偏移
             */
            private float mTopMoved;
            /**
             * 当前页面bottom偏移
             */
            private float mBottomMoved;
            /**
             * 手势释放后,需要收缩或者折叠时,用于计算方向
             */
            private int   mDirection = 0;

            @Override
            public String toString ( ) {

                  return "MoveHelper{" +
                      "mTopMoved=" + mTopMoved +
                      ", mBottomMoved=" + mBottomMoved +
                      ", mDirection=" + mDirection +
                      ", mState=" + mState +
                      ", page=" + MonthPage.this +
                      '}';
            }

            /**
             * 将页面移动一段距离
             *
             * @param dy 手势滑动的距离
             */
            private void move ( float dy ) {

                  mState = STATE_MOVING;
                  if( calculateMovedByDy( dy ) ) {
                        requestLayout();
                        ( (MonthLayout) getParent() ).onCurrentItemVerticalMove( mTopMoved + mBottomMoved );
                  }
            }

            private boolean calculateMovedByDy ( float dy ) {

                  /* 记录原始尺寸,用于后续决定是否需要重新布局 */
                  float topMoved = mTopMoved;
                  float bottomMoved = mBottomMoved;

                  /* 可以移动的距离 */
                  int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
                  int bottomDis = mPageHeight - ( topDis + mCellHeight );
                  /* 上下可移动距离之比 */
                  float topRadio = topDis * 1f / ( topDis + bottomDis );
                  /* 根据比例分配移动的距离 */
                  float topNeedMove = dy * topRadio;
                  float bottomNeedMove = dy - topNeedMove;

                  /* 重新计算上部移动的距离 */
                  mTopMoved += topNeedMove;
                  if( mTopMoved < -topDis ) {
                        mTopMoved = -topDis;
                  }
                  if( mTopMoved > 0 ) {
                        mTopMoved = 0;
                  }

                  /* 重新计算下部移动的距离 */
                  mBottomMoved += bottomNeedMove;
                  if( mBottomMoved < -bottomDis ) {
                        mBottomMoved = -bottomDis;
                  }
                  if( mBottomMoved > 0 ) {
                        mBottomMoved = 0;
                  }

                  /* 判断是否需要重新布局 */
                  return topMoved != mTopMoved || bottomMoved != mBottomMoved;
            }

            /**
             * 计算折叠时需要移动的尺寸
             */
            private void calculateFoldMoved ( ) {

                  int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
                  int bottomDis = mPageHeight - ( topDis + mCellHeight );
                  mTopMoved = -topDis;
                  mBottomMoved = -bottomDis;
            }

            /**
             * 计算展开时需要移动的尺寸
             */
            private void calculateExpandMoved ( ) {

                  mTopMoved = 0;
                  mBottomMoved = 0;
            }

            /**
             * 手势操作时高度
             *
             * @param linesHeight 页面显示完整需要的高度
             *
             * @return 修正后的高度
             */
            private int calculateMeasuredHeight ( int linesHeight ) {

                  return (int) ( linesHeight + mTopMoved + mBottomMoved );
            }

            private boolean isCurrentAtFoldState ( ) {

                  int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
                  int bottomDis = mPageHeight - ( topDis + mCellHeight );
                  return mTopMoved == -topDis && mBottomMoved == -bottomDis;
            }

            private void setAnimateState ( int direction ) {

                  mDirection = direction;
                  mState = STATE_ANIMATE;

                  animateIfNeed();
            }

            private void animateIfNeed ( ) {

                  if( needMockMove() ) {
                        if( calculateMovedByDy( mDirection * mCellHeight / 5f ) ) {
                              requestLayout();
                              ( (MonthLayout) getParent() ).onCurrentItemVerticalMove( mTopMoved + mBottomMoved );
                        }
                  }
            }

            private boolean needMockMove ( ) {

                  if( mState == STATE_ANIMATE ) {
                        if( mDirection == 1 ) {
                              boolean result = mTopMoved != 0 || mBottomMoved != 0;
                              if( !result ) {
                                    mDirection = 0;
                                    mState = STATE_EXPAND;
                                    MonthLayout parent = (MonthLayout) getParent();
                                    parent.onMonthModeChange( mDate, mPosition, true );
                              }
                              return result;
                        }

                        if( mDirection == -1 ) {
                              int topDis = mCurrentSelectedPosition / 7 * mCellHeight;
                              int bottomDis = mPageHeight - ( topDis + mCellHeight );
                              boolean result = mTopMoved != -topDis || mBottomMoved != -bottomDis;
                              if( !result ) {
                                    mDirection = 0;
                                    mState = STATE_FOLDED;
                                    MonthLayout parent = (MonthLayout) getParent();
                                    parent.onMonthModeChange( mDate, mPosition, false );
                              }
                              return result;
                        }
                  }

                  return false;
            }

            private void forceStopAnimateIfRunning ( ) {

                  if( mState == STATE_ANIMATE ) {
                        mState = STATE_MOVING;
                  }
            }

            private void checkAnimateState ( boolean isMonthMode ) {

                  if( mDirection != 0 ) {
                        setAnimateState( mDirection );
                        return;
                  }

                  if( isMonthMode ) {
                        setAnimateState( 1 );
                  } else {
                        setAnimateState( -1 );
                  }
            }
      }
}