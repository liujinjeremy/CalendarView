package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.Date;
import java.util.LinkedList;
import tech.threekilogram.calendarview.CalendarView;
import tech.threekilogram.calendarview.CalendarView.OnDateChangeListener;
import tech.threekilogram.calendarview.CalendarView.ViewComponent;
import tech.threekilogram.calendarview.util.CalendarUtils;

/**
 * @author Liujin 2019/2/21:13:00:25
 */
public class MonthLayout extends ViewPager implements ViewComponent {

      private static final String TAG = MonthLayout.class.getSimpleName();

      /**
       * 父布局
       */
      private CalendarView         mCalendarView;
      /**
       * 提供数据
       */
      private DateSource           mSource;
      /**
       * 页面滚动时改变高度
       */
      private OnPageScroller       mScroller;
      /**
       * 展开折叠页面
       */
      private ExpandFoldPage       mExpandFoldPage;
      /**
       * 监听日期变化
       */
      private OnDateChangeListener mOnDateChangeListener;
      /**
       * 计算页面需要使用的基础尺寸
       */
      private CellSize             mCellSize;
      /**
       * 用于滚动时改变高度,或者页面展开折叠时改变高度
       */
      private ChangeHeight         mChangeHeight;

      /**
       * 只能new出来不能再布局中使用
       */
      public MonthLayout ( @NonNull Context context ) {

            super( context );
      }

      @Override
      public View getView ( ) {

            return this;
      }

      public void setDate ( Date date ) {

            onDateChanged( date, mSource.mBasePosition, mSource.isMonthMode );
            if( mOnDateChangeListener != null ) {
                  mOnDateChangeListener.onNewDateClick( getCurrentPage().getDate() );
            }
      }

      public Date getDate ( ) {

            return mSource.mBaseDate;
      }

      public Date getCurrentPageDate ( ) {

            return getCurrentPage().getDate();
      }

      public boolean isMonthMode ( ) {

            return mSource.isMonthMode;
      }

      public void setMonthMode ( boolean isMonthMode ) {

            onDateChanged( mSource.mBaseDate, mSource.mBasePosition, isMonthMode );
      }

      public void expandToMonthMode ( ) {

            //noinspection ConstantConditions
            getCurrentPage().moveToExpand();
      }

      public void foldToWeekMode ( ) {

            //noinspection ConstantConditions
            getCurrentPage().moveToFold();
      }

      @Override
      public void bindParent ( CalendarView calendarView ) {

            mCalendarView = calendarView;

            int position = Integer.MAX_VALUE >> 1;
            mSource = new DateSource( new Date(), position );

            PagerMonthAdapter adapter = new PagerMonthAdapter();
            setAdapter( adapter );
            setCurrentItem( position );

            mScroller = new OnPageScroller( this );
            addOnPageChangeListener( mScroller );

            mExpandFoldPage = new ExpandFoldPage();
            mCellSize = new CellSize();
            mChangeHeight = new ChangeHeight();
      }

      @Override
      public void notifyFirstDayIsMondayChanged ( boolean isFirstDayMonday ) {

            onDateChanged( mSource.mBaseDate, mSource.mBasePosition, mSource.isMonthMode );
      }

      public void setOnDateChangeListener ( OnDateChangeListener onDateChangeListener ) {

            mOnDateChangeListener = onDateChangeListener;
      }

      public OnDateChangeListener getOnDateChangeListener ( ) {

            return mOnDateChangeListener;
      }

      int getCellWidth ( ) {

            return mCellSize.mCellWidth;
      }

      int getCellHeight ( ) {

            return mCellSize.mCellHeight;
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            if( mChangeHeight.trySetDimension( widthMeasureSpec, heightMeasureSpec ) ) {
                  return;
            }

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );
            mCellSize.calculateCellSize( widthSize, heightSize );

            super.onMeasure( widthMeasureSpec, heightMeasureSpec );

            /* 将当前页面的高度设置为pager高度 */
            int childCount = getChildCount();
            int currentItem = getCurrentItem();
            for( int i = 0; i < childCount; i++ ) {
                  MonthPage view = (MonthPage) getChildAt( i );
                  if( currentItem == view.getPosition() ) {
                        /* 将当前页面的高度设置为pager高度 */
                        int measuredHeight = view.getMeasuredHeight();
                        setMeasuredDimension( widthSize, measuredHeight );
                        break;
                  }
            }
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            if( mChangeHeight.tryLayout() ) {
                  return;
            }
            super.onLayout( changed, l, t, r, b );
      }

      @Override
      public void computeScroll ( ) {

            super.computeScroll();
            mCellSize.decideCellSize();
      }

      @Override
      public boolean dispatchTouchEvent ( MotionEvent ev ) {

            return mExpandFoldPage.handleMotionEvent( ev );
      }

      /**
       * 工具方法,获取当前页面
       *
       * @return 当前页面
       */
      public MonthPage getCurrentPage ( ) {

            int currentItem = getCurrentItem();
            int count = getChildCount();
            for( int i = 0; i < count; i++ ) {
                  MonthPage child = (MonthPage) getChildAt( i );
                  if( child.getPosition() == currentItem ) {
                        return child;
                  }
            }
            return null;
      }

      /**
       * 重新设置基础日期
       *
       * @param date 新的基础日期
       * @param position 基准日期对应的基准位置
       * @param monthMode 是否是月显示模式
       */
      private void onDateChanged ( Date date, int position, boolean monthMode ) {

            mSource.resetDate( date, position );
            mSource.isMonthMode = monthMode;
            int childCount = getChildCount();
            boolean firstDayMonday = mCalendarView.isFirstDayMonday();
            for( int i = 0; i < childCount; i++ ) {
                  MonthPage child = (MonthPage) getChildAt( i );
                  int childPosition = child.getPosition();
                  child.setInfo( mSource.getDate( childPosition ), childPosition, firstDayMonday, monthMode );
                  child.requestLayout();
            }
      }

      private void onNewPageSelected ( int position ) {

            if( mOnDateChangeListener != null ) {
                  mOnDateChangeListener.onNewPageSelected( mSource.getDate( position ) );
            }
      }

      void onNewDateClicked ( Date date, int position ) {

            onDateChanged( date, position, mSource.isMonthMode );

            if( mOnDateChangeListener != null ) {
                  mOnDateChangeListener.onNewDateClick( date );
            }
      }

      void onMonthModeChange ( Date date, int position, boolean monthMode ) {

            onDateChanged( date, position, monthMode );
      }

      void onCurrentItemVerticalMove ( float moved ) {

            mChangeHeight.mSetHeight = moved;
      }

      private class ChangeHeight {

            private float mSetHeight;

            private boolean trySetDimension ( int widthMeasureSpec, int heightMeasureSpec ) {

                  int widthSize = MeasureSpec.getSize( widthMeasureSpec );
                  if( mScroller.isScrolling() ) {
                        if( mSetHeight > 0 ) {
                              setMeasuredDimension( widthSize, (int) mSetHeight );
                              return true;
                        }
                  }

                  MonthPage currentPage = getCurrentPage();
                  if( currentPage != null ) {
                        if( currentPage.isMoving() ) {
                              currentPage.measure( widthMeasureSpec, heightMeasureSpec );
                              setMeasuredDimension( widthSize, currentPage.getMeasuredHeight() );
                              return true;
                        }
                  }
                  return false;
            }

            private boolean tryLayout ( ) {

                  if( mScroller.isScrolling() ) {
                        return true;
                  }

                  MonthPage currentPage = getCurrentPage();
                  if( currentPage != null ) {
                        if( currentPage.isMoving() ) {
                              int measuredHeight = getMeasuredHeight();
                              int childCount = getChildCount();
                              for( int i = 0; i < childCount; i++ ) {
                                    View child = getChildAt( i );
                                    child.layout( child.getLeft(), child.getTop(), child.getRight(),
                                                  child.getTop() + measuredHeight
                                    );
                              }
                              return true;
                        }
                  }
                  return false;
            }

            private void changeHeightWhenScroll ( int currentPosition, int nextPosition, float offset ) {

                  int currentHeight = 0;
                  int nextHeight = 0;
                  int childCount = getChildCount();
                  for( int i = 0; i < childCount; i++ ) {
                        MonthPage child = (MonthPage) getChildAt( i );
                        if( child.getPosition() == currentPosition ) {
                              currentHeight = child.getMeasuredHeight();

                              if( currentHeight != 0 && nextHeight != 0 ) {
                                    break;
                              } else {
                                    continue;
                              }
                        }
                        if( child.getPosition() == nextPosition ) {
                              nextHeight = child.getMeasuredHeight();
                              if( currentHeight != 0 && nextHeight != 0 ) {
                                    break;
                              }
                        }
                  }

                  float height = currentHeight + ( nextHeight - currentHeight ) * Math.abs( offset );
                  if( currentHeight != height ) {
                        mSetHeight = (int) height;
                        requestLayout();
                  }
            }
      }

      /**
       * 此类用于将原始尺寸分成7*6份,并且计算每份的尺寸,每一页通过该类获取尺寸,以保证统一
       */
      private class CellSize {

            /**
             * 显示天的view的宽度
             */
            private int     mCellWidth       = -1;
            /**
             * 显示天的view的高度
             */
            private int     mCellHeight      = -1;
            /**
             * cellWidth 和 cellHeight 是否已经确定,当子view布局过了,就确定了
             */
            private boolean isCellSizeDecide = false;

            /**
             * 用于{@link #onMeasure(int, int)}中通过获得的尺寸计算基础尺寸
             *
             * @param widthSize 获得的宽度
             * @param heightSize 获得的高度
             */
            private void calculateCellSize ( int widthSize, int heightSize ) {

                  if( !isCellSizeDecide ) {
                        mCellWidth = widthSize / 7;
                        mCellHeight = heightSize / 6;
                  }
            }

            /**
             * 因为布局时需要多次测量,所以在{@link #onLayout(boolean, int, int, int, int)}后可以确定测量已经完成,
             * 此时基础尺寸可以计算出,然后通知子view使用确定的基础尺寸重新布局一下,(可以防止5.1以上布局高度不正确)
             * 注意:{@link #onLayout(boolean, int, int, int, int)}中调用{@link View#requestLayout()}不会起作用,
             * 需要在{@link View#computeScroll()}中调用
             */
            private void decideCellSize ( ) {

                  if( !mCellSize.isCellSizeDecide ) {
                        mCellSize.isCellSizeDecide = true;
                        notifyCellSizeDecide();
                  }
            }

            /**
             * 通知子view重新布局
             */
            private void notifyCellSizeDecide ( ) {

                  int childCount = getChildCount();
                  for( int i = 0; i < childCount; i++ ) {
                        getChildAt( i ).requestLayout();
                  }
            }
      }

      /**
       * 用于根据页面之间的位置信息/显示模式计算日期
       */
      private class DateSource {

            /**
             * 基准日期
             */
            private Date    mBaseDate;
            private int     mBasePosition;
            /**
             * 是否是月模式
             */
            private boolean isMonthMode = true;

            private DateSource ( Date baseDate, int basePosition ) {

                  mBaseDate = baseDate;
                  mBasePosition = basePosition;
            }

            private void resetDate ( Date date, int position ) {

                  mBaseDate = date;
                  mBasePosition = position;
            }

            private Date getDate ( int position ) {

                  if( isMonthMode ) {
                        return getMonthDate( position );
                  } else {
                        return getWeekDate( position );
                  }
            }

            /**
             * 获取该位置的日期,每次增加或者减少一个月
             */
            private Date getMonthDate ( int position ) {

                  int step = position - mBasePosition;
                  return CalendarUtils.getDateByAddMonth( mBaseDate, step );
            }

            /**
             * 获取该位置的日期,每次增加或者减少一周
             */
            private Date getWeekDate ( int position ) {

                  int step = position - mBasePosition;
                  return CalendarUtils.getDateByAddWeek( mBaseDate, step );
            }
      }

      /**
       * adapter 设置页面
       */
      private class PagerMonthAdapter extends PagerAdapter {

            /**
             * 回收页面
             */
            private LinkedList<View> mReUsed = new LinkedList<>();

            @Override
            public int getCount ( ) {
                  /* 日期的个数是无限的 */
                  return Integer.MAX_VALUE;
            }

            @NonNull
            @Override
            public Object instantiateItem ( @NonNull ViewGroup container, int position ) {

                  MonthPage page;
                  if( mReUsed.isEmpty() ) {
                        page = new MonthPage( container.getContext() );
                  } else {
                        page = (MonthPage) mReUsed.pollFirst();
                  }

                  Date date = mSource.getDate( position );
                  page.setInfo( date, position, mCalendarView.isFirstDayMonday(), mSource.isMonthMode );

                  container.addView( page );
                  return page;
            }

            @Override
            public boolean isViewFromObject ( @NonNull View view, @NonNull Object object ) {

                  return object == view;
            }

            @Override
            public void destroyItem ( @NonNull ViewGroup container, int position, @NonNull Object object ) {

                  View view = (View) object;
                  container.removeView( view );
                  mReUsed.add( view );
            }
      }

      /**
       * 滚动时改变页面高度
       */
      private class OnPageScroller extends ViewPagerScrollListener {

            /**
             * 创建
             *
             * @param pager pager
             */
            private OnPageScroller ( ViewPager pager ) {

                  super( pager );
            }

            @Override
            public void onPageSelected ( int position ) {

                  super.onPageSelected( position );
                  onNewPageSelected( position );
            }

            @Override
            protected void onScrolled ( int state, int current, float offset, int offsetPixels ) {

                  if( offset < 0 ) {
                        mChangeHeight.changeHeightWhenScroll( current, current + 1, offset );
                  }

                  if( offset > 0 ) {
                        mChangeHeight.changeHeightWhenScroll( current, current - 1, offset );
                  }
            }

            private boolean isScrolling ( ) {

                  return mState != SCROLL_STATE_IDLE;
            }
      }

      /**
       * 用于竖直滑动时展开折叠当前页面
       */
      private class ExpandFoldPage {

            /**
             * 按下时位置信息
             */
            private float mDownX;
            private float mDownY;
            private float mLastX;
            private float mLastY;

            private boolean isHorizontalMove;
            private boolean isVerticalMove;

            /**
             * 拦截垂直滑动事件,并且通知给当前页面,竖直滑动距离
             *
             * @param ev 滑动事件
             *
             * @return 是否已经消费事件, true:已经消费
             */
            private boolean handleMotionEvent ( MotionEvent ev ) {

                  float x;
                  float y;
                  MonthPage monthPage = getCurrentPage();

                  switch( ev.getAction() ) {
                        case MotionEvent.ACTION_DOWN:
                              x = ev.getRawX();
                              y = ev.getRawY();
                              mLastX = mDownX = x;
                              mLastY = mDownY = y;

                              if( monthPage != null ) {
                                    if( monthPage.isMoving() ) {
                                          monthPage.onDownTouchEvent();
                                          isVerticalMove = true;
                                    }
                              }
                              return superDispatchTouchEvent( ev );

                        case MotionEvent.ACTION_MOVE:

                              x = ev.getRawX();
                              y = ev.getRawY();

                              float dx = x - mLastX;
                              float dy = y - mLastY;
                              mLastX = x;
                              mLastY = y;

                              if( mScroller.isScrolling() ) {
                                    isHorizontalMove = true;
                                    isVerticalMove = false;
                              }

                              if( !isHorizontalMove && !isVerticalMove ) {
                                    float absX = Math.abs( dx );
                                    float absY = Math.abs( dy );

                                    /* 此时刚触发滑动事件 */
                                    if( absY > absX ) {
                                          isVerticalMove = true;
                                          isHorizontalMove = false;
                                    }
                              }

                              if( isVerticalMove ) {
                                    if( monthPage != null ) {
                                          monthPage.onVerticalMoveBy( dy );
                                          return true;
                                    }
                              }

                              return superDispatchTouchEvent( ev );

                        default:
                              //x = ev.getRawX();
                              y = ev.getRawY();

                              if( isVerticalMove ) {
                                    if( monthPage != null ) {
                                          monthPage.onUpTouchEvent( y - mDownY, mSource.isMonthMode );
                                          isHorizontalMove = isVerticalMove = false;
                                          return true;
                                    }
                              }

                              isHorizontalMove = isVerticalMove = false;
                              return superDispatchTouchEvent( ev );
                  }
            }

            private boolean superDispatchTouchEvent ( MotionEvent ev ) {

                  return MonthLayout.super.dispatchTouchEvent( ev );
            }
      }
}