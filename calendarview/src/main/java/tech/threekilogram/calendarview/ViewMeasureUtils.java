package tech.threekilogram.calendarview;

import android.view.View;
import android.view.View.MeasureSpec;

/**
 * @author: Liujin
 * @version: V1.0
 * @date: 2018-07-18
 * @time: 13:53
 */
public class ViewMeasureUtils {

      public static int[] measureView (
          View view,
          int widthMeasureSpec,
          int heightMeasureSpec,
          int minContentWidth,
          int minContentHeight ) {

            int widthMode = MeasureSpec.getMode( widthMeasureSpec );
            int widthSize = MeasureSpec.getSize( widthMeasureSpec );

            int heightMode = MeasureSpec.getMode( heightMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );

            int paddingLeft = view.getPaddingLeft();
            int paddingTop = view.getPaddingTop();
            int paddingRight = view.getPaddingRight();
            int paddingBottom = view.getPaddingBottom();

            if( minContentHeight < 0 ) {
                  minContentHeight = heightSize;
            }

            if( minContentWidth < 0 ) {
                  minContentWidth = widthSize;
            }

            int finalWidth = 0;
            int finalHeight = 0;

            if( widthMode == MeasureSpec.EXACTLY ) {

                  finalWidth = widthSize;
            } else if( widthMode == MeasureSpec.AT_MOST ) {

                  finalWidth = Math.min( ( minContentWidth + paddingLeft + paddingRight ), widthSize );
            } else {

                  finalWidth = minContentWidth + paddingLeft + paddingRight;
            }

            if( heightMode == MeasureSpec.EXACTLY ) {

                  finalHeight = heightSize;
            } else if( widthMode == MeasureSpec.AT_MOST ) {

                  finalHeight = Math
                      .min( ( minContentHeight + paddingTop + paddingBottom ), heightSize );
            } else {

                  finalHeight = minContentHeight + paddingTop + paddingBottom;
            }

            int[] result = new int[ 2 ];
            result[ 0 ] = finalWidth;
            result[ 1 ] = finalHeight;

            return result;
      }
}
