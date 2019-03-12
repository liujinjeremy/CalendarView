package tech.threekilogram.calendar.month;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.Date;
import java.util.LinkedList;
import tech.threekilogram.calendar.CalendarView;
import tech.threekilogram.calendar.CalendarView.OnDateChangeListener;
import tech.threekilogram.calendar.util.CalendarUtils;

/**
 * @author Liujin 2019/2/21:13:00:25
 */
@SuppressLint("ViewConstructor")
public class MonthLayout extends ViewPager {

      /**
       * 父布局
       */
      private CalendarView         mParent;
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
       * 为{@link MonthPage}生成天界面
       */
      private MonthDayViewFactory  mMonthDayViewFactory;

      /**
       * 只能new出来不能再布局中使用
       */
      public MonthLayout ( @NonNull Context context, CalendarView parent ) {

            super( context );
            init( parent );
      }

      /**
       * 初始化
       */
      private void init ( CalendarView parent ) {

            mParent = parent;

            int position = Integer.MAX_VALUE >> 1;
            mSource = new DateSource( new Date(), position );

            PagerMonthAdapter adapter = new PagerMonthAdapter();
            setAdapter( adapter );
            setCurrentItem( position );

            mScroller = new OnPageScroller( this );
            addOnPageChangeListener( mScroller );

            mExpandFoldPage = new ExpandFoldPage();
            mCellSize = new CellSize();
            mMonthDayViewFactory = new DefaultItemFactory();
      }

      public void setMonthDayViewFactory ( MonthDayViewFactory monthDayViewFactory ) {

            mMonthDayViewFactory = monthDayViewFactory;
      }

      public void setDate ( Date date ) {

            onDateChanged( date, mSource.mBasePosition, mSource.isMonthMode, true );
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

            onDateChanged( mSource.mBaseDate, mSource.mBasePosition, isMonthMode, true );
      }

      public void expandToMonthMode ( ) {

            //noinspection ConstantConditions
            getCurrentPage().animateExpand();
      }

      public void foldToWeekMode ( ) {

            //noinspection ConstantConditions
            getCurrentPage().animateFold();
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

      public void notifyFirstDayIsMondayChanged ( boolean isFirstDayMonday ) {

            onDateChanged( mSource.mBaseDate, mSource.mBasePosition, mSource.isMonthMode, isFirstDayMonday, true );
      }

      public void setOnDateChangeListener ( OnDateChangeListener onDateChangeListener ) {

            mOnDateChangeListener = onDateChangeListener;
      }

      public OnDateChangeListener getOnDateChangeListener ( ) {

            return mOnDateChangeListener;
      }

      public int getCellWidth ( ) {

            return mCellSize.mCellWidth;
      }

      public int getCellHeight ( ) {

            return mCellSize.mCellHeight;
      }

      public void setOnPagerScrollHeightChangeStrategy ( OnPagerScrollHeightChangeStrategy strategy ) {

            mScroller.mStrategy = strategy;
      }

      public void setOnCurrentPageExpandFoldStrategy ( OnCurrentPageExpandFoldStrategy strategy ) {

            mExpandFoldPage.mStrategy = strategy;
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            if( mScroller.isScrolling() ) {
                  if( mScroller.tryMeasure( widthMeasureSpec, heightMeasureSpec ) ) {
                        return;
                  }
            }

            if( mExpandFoldPage.isCurrentPageAnimateOrMoving() ) {
                  if( mExpandFoldPage.tryMeasure( widthMeasureSpec, heightMeasureSpec ) ) {
                        return;
                  }
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

            if( mScroller.isScrolling() ) {
                  if( mScroller.tryLayout( l, t, r, b ) ) {
                        return;
                  }
            }

            if( mExpandFoldPage.isCurrentPageAnimateOrMoving() ) {
                  if( mExpandFoldPage.tryLayout( l, t, r, b ) ) {
                        return;
                  }
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
       * 重新设置基础日期
       *
       * @param date 新的基础日期
       * @param position 基准日期对应的基准位置
       * @param monthMode 是否是月显示模式
       */
      private void onDateChanged ( Date date, int position, boolean monthMode, boolean needRequestLayout ) {

            onDateChanged( date, position, monthMode, mParent.isFirstDayMonday(), needRequestLayout );
      }

      /**
       * 重新设置基础日期
       *
       * @param date 新的基础日期
       * @param position 基准日期对应的基准位置
       * @param monthMode 是否是月显示模式
       */
      private void onDateChanged (
          Date date, int position, boolean monthMode, boolean firstDayMonday, boolean needRequestLayout ) {

            mSource.resetDate( date, position );
            mSource.isMonthMode = monthMode;
            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  MonthPage child = (MonthPage) getChildAt( i );
                  int childPosition = child.getPosition();
                  child.setInfo( mSource.getDate( childPosition ), childPosition, firstDayMonday, monthMode );
                  if( needRequestLayout ) {
                        child.requestLayout();
                  }
            }
      }

      void onNewPageSelected ( int position ) {

            if( mOnDateChangeListener != null ) {
                  mOnDateChangeListener.onNewPageSelected( mSource.getDate( position ) );
            }
      }

      void onNewDateClicked ( Date date, int position ) {

            onDateChanged( date, position, mSource.isMonthMode, false );

            if( mOnDateChangeListener != null ) {
                  mOnDateChangeListener.onNewDateClick( date );
            }
      }

      void onMonthModeChange ( Date date, int position, boolean monthMode ) {

            onDateChanged( date, position, monthMode, true );
      }

      void onCurrentPageExpandFolding ( int currentPageMovedHeight ) {

            mExpandFoldPage.onCurrentPageHeightChange( currentPageMovedHeight );
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

      public interface MonthDayViewFactory {

            /**
             * 创建子view
             *
             * @return 子view
             */
            public MonthDayView generateItemView ( Context context );
      }

      private class DefaultItemFactory implements MonthDayViewFactory {

            @Override
            public MonthDayView generateItemView ( Context context ) {

                  return new MonthDayView( context );
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
                        page = new MonthPage( container.getContext(), MonthLayout.this, mMonthDayViewFactory );
                  } else {
                        page = (MonthPage) mReUsed.pollFirst();
                  }

                  Date date = mSource.getDate( position );
                  page.setInfo( date, position, mParent.isFirstDayMonday(), mSource.isMonthMode );

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
       * 滚动时改变页面高度
       */
      private class OnPageScroller extends ViewPagerScrollListener {

            private OnPagerScrollHeightChangeStrategy mStrategy = new RelayoutScrollHeightChangeStrategy();

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
                        changeHeightWhenScroll( current, current + 1, offset );
                        return;
                  }

                  if( offset > 0 ) {
                        changeHeightWhenScroll( current, current - 1, offset );
                        return;
                  }

                  MonthPage currentPage = getCurrentPage();
                  if( currentPage != null ) {
                        int height = currentPage.getMeasuredHeight();
                        mStrategy.onHeightChange( height, height, 0, height );
                  }
            }

            private boolean isScrolling ( ) {

                  return mState != SCROLL_STATE_IDLE;
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

                  int height = (int) ( currentHeight + ( nextHeight - currentHeight ) * Math.abs( offset ) );
                  mStrategy.onHeightChange( currentHeight, nextHeight, offset, height );
            }

            private void setStrategy ( OnPagerScrollHeightChangeStrategy strategy ) {

                  mStrategy = strategy;
            }

            private boolean tryMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

                  return mStrategy.onMeasureWhenScrolling( widthMeasureSpec, heightMeasureSpec );
            }

            private boolean tryLayout ( int parentLeft, int parentTop, int parentRight, int parentBottom ) {

                  return mStrategy.onLayoutWhenScrolling( parentLeft, parentTop, parentRight, parentBottom );
            }
      }

      /**
       * 用于页面滚动时需要改变页面高度时,使用的策略
       */
      public interface OnPagerScrollHeightChangeStrategy {

            void onHeightChange ( int currentPageHeight, int targetPageHeight, float offset, int calculateHeight );

            boolean onMeasureWhenScrolling ( int parentWidthSpec, int parentHeightSpec );

            boolean onLayoutWhenScrolling ( int parentLeft, int parentTop, int parentRight, int parentBottom );
      }

      private class RelayoutScrollHeightChangeStrategy implements OnPagerScrollHeightChangeStrategy {

            private int mCalculateHeight = -1;

            @Override
            public void onHeightChange ( int currentPageHeight, int targetPageHeight, float offset, int calculateHeight ) {

                  if( calculateHeight != currentPageHeight ) {
                        mCalculateHeight = calculateHeight;
                        requestLayout();
                  } else {
                        mCalculateHeight = -1;
                  }
            }

            @Override
            public boolean onMeasureWhenScrolling ( int parentWidthSpec, int parentHeightSpec ) {

                  int widthSize = MeasureSpec.getSize( parentWidthSpec );
                  if( mCalculateHeight > 0 ) {
                        setMeasuredDimension( widthSize, mCalculateHeight );
                        return true;
                  }
                  return false;
            }

            @Override
            public boolean onLayoutWhenScrolling ( int parentLeft, int parentTop, int parentRight, int parentBottom ) {

                  return true;
            }
      }

//      private class ChangeHeight {
//
//            private boolean trySetDimension ( int widthMeasureSpec, int heightMeasureSpec ) {
//
//                  MonthPage currentPage = getCurrentPage();
//                  if( currentPage != null ) {
//                        if( currentPage.isAnimateOrMoving() ) {
//                              currentPage.measure( widthMeasureSpec, heightMeasureSpec );
//                              setMeasuredDimension( widthSize, currentPage.getMeasuredHeight() );
//                              return true;
//                        }
//                  }
//                  return false;
//            }
//
//            private boolean tryLayout ( ) {
//
//                  MonthPage currentPage = getCurrentPage();
//                  if( currentPage != null ) {
//                        if( currentPage.isAnimateOrMoving() ) {
//                              int measuredHeight = getMeasuredHeight();
//                              int childCount = getChildCount();
//                              for( int i = 0; i < childCount; i++ ) {
//                                    View child = getChildAt( i );
//                                    child.layout( child.getLeft(), child.getTop(), child.getRight(),
//                                                  child.getTop() + measuredHeight
//                                    );
//                              }
//                              return true;
//                        }
//                  }
//                  return false;
//            }
//      }

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

            private OnCurrentPageExpandFoldStrategy mStrategy = new RelayoutCurrentPageExpandFoldStrategy();

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
                                    if( monthPage.isAnimateOrMoving() ) {
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
                                          monthPage.onMoveTouchEvent( dy );
                                          return true;
                                    }
                              }

                              return superDispatchTouchEvent( ev );

                        default:
                              //x = ev.getRawX();
                              y = ev.getRawY();

                              if( isVerticalMove ) {
                                    if( monthPage != null ) {
                                          monthPage.onTouchEventRelease( y - mDownY, mSource.isMonthMode );
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

            private void onCurrentPageHeightChange ( int currentPageHeight ) {

                  mStrategy.onCurrentPageHeightChange( currentPageHeight );
            }

            private boolean isCurrentPageAnimateOrMoving ( ) {

                  MonthPage currentPage = getCurrentPage();
                  if( currentPage != null ) {
                        return currentPage.isAnimateOrMoving();
                  }
                  return false;
            }

            private boolean tryMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

                  return mStrategy.onMeasureWhenCurrentPageExpandFold( widthMeasureSpec, heightMeasureSpec );
            }

            private boolean tryLayout ( int l, int t, int r, int b ) {

                  return mStrategy.onLayoutWhenCurrentPageExpandFold( l, t, r, b );
            }
      }

      public interface OnCurrentPageExpandFoldStrategy {

            void onCurrentPageHeightChange ( int currentPageHeight );

            boolean onMeasureWhenCurrentPageExpandFold ( int parentWidthSpec, int parentHeightSpec );

            boolean onLayoutWhenCurrentPageExpandFold ( int parentLeft, int parentTop, int parentRight, int parentBottom );
      }

      private class RelayoutCurrentPageExpandFoldStrategy implements OnCurrentPageExpandFoldStrategy {

            private int mCurrentPageHeight;

            @Override
            public void onCurrentPageHeightChange ( int currentPageHeight ) {

                  if( mCurrentPageHeight != currentPageHeight ) {
                        mCurrentPageHeight = currentPageHeight;
                        getCurrentPage().requestLayout();
                  }
            }

            @Override
            public boolean onMeasureWhenCurrentPageExpandFold ( int parentWidthSpec, int parentHeightSpec ) {

                  MonthPage currentPage = getCurrentPage();
                  currentPage.measure( parentWidthSpec, parentHeightSpec );
                  setMeasuredDimension( MeasureSpec.getSize( parentWidthSpec ), currentPage.getMeasuredHeight() );
                  return true;
            }

            @Override
            public boolean onLayoutWhenCurrentPageExpandFold (
                int parentLeft, int parentTop, int parentRight, int parentBottom ) {

                  MonthPage currentPage = getCurrentPage();
                  if( currentPage != null ) {
                        int measuredHeight = getMeasuredHeight();
                        currentPage.layout(
                            currentPage.getLeft(),
                            currentPage.getTop(),
                            currentPage.getRight(),
                            currentPage.getTop() + measuredHeight
                        );
                        return true;
                  }
                  return false;
            }
      }
}