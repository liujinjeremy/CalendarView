package tech.threekilogram.calendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Liujin 2019/2/21:21:32:09
 */
public class MonthPage extends ViewGroup {

      private static final String TAG = MonthPage.class.getSimpleName();

      public MonthPage ( Context context ) {

            this( context, null, 0 );
      }

      public MonthPage ( Context context, AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public MonthPage ( Context context, AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init( context );
      }

      private void init ( Context context ) {

            for( int i = 0; i < 42; i++ ) {
                  addView( generateItemView( i ) );
            }
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            int cellWidth = widthSize / 7;
            int cellHeight = heightSize / 6;

            int cellWidthSpec = MeasureSpec.makeMeasureSpec( cellWidth, MeasureSpec.EXACTLY );
            int cellHeightSpec = MeasureSpec.makeMeasureSpec( cellHeight, MeasureSpec.EXACTLY );

            int childCount = getChildCount();
            for( int i = 0; i < childCount; i++ ) {
                  View child = getChildAt( i );
                  child.measure( cellWidthSpec, cellHeightSpec );
            }

            setMeasuredDimension( widthSize, heightSize );
      }

      @Override
      protected void onLayout ( boolean changed, int l, int t, int r, int b ) {

            View child = getChildAt( 0 );
            int cellWidth = child.getMeasuredWidth();
            int cellHeight = child.getMeasuredHeight();

            for( int i = 0; i < 6; i++ ) {
                  for( int j = 0; j < 7; j++ ) {
                        int index = j + i * 7;
                        View view = getChildAt( index );
                        int left = cellWidth * j;
                        view.layout( left, cellHeight * i, left + cellWidth, cellHeight * i + cellHeight );
                  }
            }
      }

      protected View generateItemView ( int dayOfMonth ) {

            TextView textView = new TextView( getContext() );
            textView.setGravity( Gravity.CENTER );
            textView.setText( String.valueOf( dayOfMonth ) );
            textView.setBackgroundResource( R.drawable.rect );
            return textView;
      }
}
