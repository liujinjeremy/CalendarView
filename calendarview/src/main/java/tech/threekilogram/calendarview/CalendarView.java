package tech.threekilogram.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedList;

/**
 * @author Liujin 2019/2/20:15:09:44
 */
public class CalendarView extends FrameLayout {

      private static final String TAG = CalendarView.class.getSimpleName();

      public static final int SUNDAY    = 0;
      public static final int MONDAY    = 1;
      public static final int TUESDAY   = 2;
      public static final int WEDNESDAY = 3;
      public static final int THURSDAY  = 4;
      public static final int FRIDAY    = 5;
      public static final int SATURDAY  = 6;

      private static final int[] colorsTest = {
          Color.RED,
          Color.YELLOW,
          Color.BLUE
      };

      /**
       * 用于显示头部的星期
       */
      protected LinearLayout mWeekBar;
      /**
       * 用于显示月份日期
       */
      protected ViewPager    mMonthPager;
      /**
       * 辅助构建界面
       */
      protected Adapter      mAdapter;

      /**
       * 定义常量星期一至星期日
       */
      @IntDef(value = { SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY })
      @Target(value = { ElementType.PARAMETER, ElementType.FIELD })
      @Retention(RetentionPolicy.SOURCE)
      public @interface WeekDay { }

      public CalendarView ( @androidx.annotation.NonNull Context context ) {

            this( context, null, 0 );
      }

      public CalendarView (
          @androidx.annotation.NonNull Context context,
          @androidx.annotation.Nullable AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public CalendarView (
          @androidx.annotation.NonNull Context context,
          @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init( context );
      }

      protected void init ( Context context ) {

            mMonthPager = new ViewPager( context );
            addView( mMonthPager, 0 );
            mMonthPager.setAdapter( new MonthPagerAdapter() );

            mWeekBar = new LinearLayout( context );
            mWeekBar.setOrientation( LinearLayout.HORIZONTAL );
            addView( mWeekBar, 1 );

            setAdapter( new CommonAdapter() );
      }

      /**
       * 设置adapter
       *
       * @param adapter 构建界面的adapter
       */
      public void setAdapter ( Adapter adapter ) {

            if( adapter != mAdapter ) {
                  mAdapter = adapter;
                  if( mWeekBar.getChildCount() != 0 ) {
                        mWeekBar.removeAllViews();
                  }
                  addWeekBarItem( adapter, getContext() );
            }
      }

      /**
       * @return 获取设置的adapter
       */
      public Adapter getAdapter ( ) {

            return mAdapter;
      }

      /**
       * 为 weekBar 添加子view
       */
      private void addWeekBarItem ( Adapter adapter, Context context ) {

            for( int i = 0; i < 7; i++ ) {
                  View child = adapter.generateWeekBarItem( context, mWeekBar, i );
                  mWeekBar.addView(
                      child,
                      i,
                      generateWeekBarLayoutParams( child.getLayoutParams() )
                  );
            }
      }

      /**
       * 将weekBar宽度均分为7分
       *
       * @param layoutParams child的layoutParams
       *
       * @return 均分后的params
       */
      protected LinearLayout.LayoutParams generateWeekBarLayoutParams (
          ViewGroup.LayoutParams layoutParams ) {

            LinearLayout.LayoutParams params;
            if( layoutParams != null ) {

                  params = new LinearLayout.LayoutParams( layoutParams );
                  params.width = 0;
                  params.weight = 1;
            } else {

                  params = new LinearLayout.LayoutParams(
                      0,
                      ViewGroup.LayoutParams.WRAP_CONTENT,
                      1
                  );
            }
            return params;
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthMode = MeasureSpec.getMode( widthMeasureSpec );
            int widthSize = MeasureSpec.getSize( widthMeasureSpec );

            int heightMode = MeasureSpec.getMode( heightMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            int widthExactSpec = MeasureSpec.makeMeasureSpec( widthSize, MeasureSpec.EXACTLY );

            // measureSelf weekBar
            int weekBarHeightSpec = MeasureSpec.makeMeasureSpec( heightSize, MeasureSpec.AT_MOST );
            measureChild( mWeekBar, widthExactSpec, weekBarHeightSpec );
            int weekBarMeasuredHeight = mWeekBar.getMeasuredHeight();

            // monthPager 高度最大可以使用weekBar之后剩余的高度
            int monthPagerHeightSpec = MeasureSpec
                .makeMeasureSpec( ( heightSize - weekBarMeasuredHeight ), MeasureSpec.AT_MOST );
            measureChild( mMonthPager, widthExactSpec, monthPagerHeightSpec );

            Log.i( TAG, "onMeasure: " + mMonthPager.getMeasuredHeight() );

            setMeasuredDimension(
                widthSize, weekBarMeasuredHeight + mMonthPager.getMeasuredHeight() );
      }

      @Override
      protected void onLayout ( boolean changed, int left, int top, int right, int bottom ) {

            int height = mWeekBar.getMeasuredHeight();
            mWeekBar
                .layout( 0, 0, mWeekBar.getMeasuredWidth(), height );

            mMonthPager.layout(
                0,
                height,
                mMonthPager.getMeasuredWidth(),
                mMonthPager.getMeasuredHeight() + height
            );
      }

      public static abstract class Adapter {

            /**
             * 生成weekBar的一个条目
             *
             * @param context context
             * @param weekDay week day (eg: sunday)
             *
             * @return weekBar 的一个条目
             */
            protected abstract View generateWeekBarItem (
                Context context, ViewGroup container, @WeekDay int weekDay );

            /**
             * 生成月份中天的布局条目
             *
             * @param context context
             * @param viewGroup parent
             * @param day that day
             *
             * @return 布局用来显示某天的信息
             */
            protected abstract View generateDayItem (
                Context context, ViewGroup viewGroup, long day );
      }

      public static class CommonAdapter extends Adapter {

            @Override
            public View generateWeekBarItem (
                Context context,
                ViewGroup container,
                @WeekDay int weekDay ) {

                  TextView textView = (TextView) LayoutInflater.from( context )
                                                               .inflate(
                                                                   R.layout.test_text,
                                                                   container,
                                                                   false
                                                               );
                  textView.setText( String.valueOf( weekDay ) );

                  int i = weekDay % colorsTest.length;
                  textView.setBackgroundColor( colorsTest[ i ] );

                  return textView;
            }

            @Override
            protected View generateDayItem ( Context context, ViewGroup viewGroup, long day ) {

                  return null;
            }
      }

      public static class MonthPagerAdapter extends PagerAdapter {

            private static final String TAG = MonthPagerAdapter.class.getSimpleName();

            private LinkedList<View> mReUsed = new LinkedList<>();

            @Override
            public int getCount ( ) {

                  return Integer.MAX_VALUE;
            }

            @NonNull
            @Override
            public Object instantiateItem ( @NonNull ViewGroup container, int position ) {

                  View view;
                  if( mReUsed.isEmpty() ) {
                        view = new TextView( container.getContext() );
                        view.setLayoutParams( new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        ) );

                        ( (TextView) view ).setGravity( Gravity.CENTER );
                  } else {
                        view = mReUsed.pollFirst();
                  }

                  int i = position % colorsTest.length;
                  view.setBackgroundColor( colorsTest[ i ] );

                  ( (TextView) view ).setText( String.valueOf( position ) );

                  container.addView( view );
                  return view;
            }

            @Override
            public boolean isViewFromObject (
                @NonNull View view, @NonNull Object object ) {

                  return object == view;
            }

            @Override
            public void destroyItem (
                @NonNull ViewGroup container, int position, @NonNull Object object ) {

                  View view = (View) object;
                  container.removeView( view );
                  mReUsed.add( view );
            }
      }
}
