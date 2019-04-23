package tech.liujin.calendar.week;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import tech.threekilogram.calendar.CalendarView;

/**
 * @author Liujin 2019/2/21:12:16:29
 */
public class LinearWeekBar extends ViewGroup {

      /**
       * parent 用于通信
       */
      private CalendarView       mParent;
      private WeekBarItemFactory mWeekBarItemFactory;

      public LinearWeekBar ( Context context, CalendarView parent ) {

            super( context );
            mParent = parent;

            init();
      }

      private void init ( ) {

            if( mWeekBarItemFactory == null ) {
                  mWeekBarItemFactory = new TextWeekBarItemFactory();
            }

            boolean firstDayMonday = mParent.isFirstDayMonday();
            for( int i = 0; i < 7; i++ ) {
                  int index;
                  if( !firstDayMonday ) {
                        index = i;
                  } else {
                        index = ( i + 1 ) % 7;
                  }
                  View view = mWeekBarItemFactory.generateItemView( getContext() );
                  mWeekBarItemFactory.bindWeek( view, index );
                  addView( view );
            }
      }

      /**
       * 宽度均分为7份,高度包裹自己
       */
      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            int widthCellSize = widthSize / 7;

            int widthCellSpec = MeasureSpec.makeMeasureSpec( widthCellSize, MeasureSpec.EXACTLY );
            int heightCellSpec = MeasureSpec.makeMeasureSpec( heightSize, MeasureSpec.AT_MOST );

            int childCount = getChildCount();
            int heightResult = 0;
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );

                  child.measure( widthCellSpec, heightCellSpec );
                  int measuredHeight = child.getMeasuredHeight();
                  if( measuredHeight > heightResult ) {
                        heightResult = measuredHeight;
                  }
            }
            setMeasuredDimension( widthSize, heightResult );
      }

      /**
       * 从左到右依次布局
       */
      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            int widthUsed = 0;
            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  int width = child.getMeasuredWidth();
                  child.layout( widthUsed, 0, widthUsed + width, child.getMeasuredHeight() );
                  widthUsed += width;
            }
      }

      public void notifyFirstDayIsMondayChanged ( boolean isFirstDayMonday ) {

            for( int i = 0; i < 7; i++ ) {
                  int index;
                  if( !isFirstDayMonday ) {
                        index = i;
                  } else {
                        index = ( i + 1 ) % 7;
                  }
                  mWeekBarItemFactory.bindWeek( getChildAt( i ), index );
            }
      }

      public interface WeekBarItemFactory {

            int SUNDAY    = 0;
            int MONDAY    = 1;
            int TUESDAY   = 2;
            int WEDNESDAY = 3;
            int THURSDAY  = 4;
            int FRIDAY    = 5;
            int SATURDAY  = 6;

            View generateItemView ( Context context );

            void bindWeek ( View item, int week );
      }

      private static class TextWeekBarItemFactory implements WeekBarItemFactory {

            /**
             * 每周第一天是周一时,显示的text
             */
            private static String[] sTexts = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };

            @Override
            public View generateItemView ( Context context ) {

                  TextView textView = new TextView( context );
                  textView.setGravity( Gravity.CENTER );
                  textView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 12 );
                  textView.setPadding( 0, 10, 0, 10 );
                  return textView;
            }

            @Override
            public void bindWeek ( View item, int week ) {

                  ( (TextView) item ).setText( sTexts[ week ] );
            }
      }
}
