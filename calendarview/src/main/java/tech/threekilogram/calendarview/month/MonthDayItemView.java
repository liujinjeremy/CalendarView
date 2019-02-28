package tech.threekilogram.calendarview.month;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.Date;
import tech.threekilogram.calendarview.BaseLineUtils;
import tech.threekilogram.calendarview.CalendarUtils;

/**
 * @author Liujin 2019/2/25:18:09:25
 */
public class MonthDayItemView extends View {

      public static final int IN_MONTH_UNSELECTED = 11;
      public static final int IN_MONTH_SELECTED   = 12;

      private Date  mDate;
      private Paint mPaint;
      private int   mState;

      public MonthDayItemView ( Context context ) {

            this( context, null, 0 );
      }

      public MonthDayItemView ( Context context, @Nullable AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public MonthDayItemView ( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {

            super( context, attrs, defStyleAttr );
            init();
      }

      private void init ( ) {

            mPaint = new Paint( Paint.ANTI_ALIAS_FLAG );
      }

      @Override
      protected void onMeasure ( int widthMeasureSpec, int heightMeasureSpec ) {

            int widthSize = MeasureSpec.getSize( widthMeasureSpec );
            int heightSize = MeasureSpec.getSize( heightMeasureSpec );
            setMeasuredDimension( widthSize, heightSize );
      }

      public void setState ( int state ) {

            if( mState != state ) {
                  mState = state;
                  invalidate();
            }
      }

      @Override
      protected void onDraw ( Canvas canvas ) {

            if( mState == IN_MONTH_UNSELECTED ) {
                  drawUnSelected( canvas );
                  return;
            }

            drawSelected( canvas );
      }

      public void bind ( Date date ) {

            if( date != mDate ) {

                  mDate = date;
                  invalidate();
            }
      }

      /**
       * drawable nothing
       */
      protected void drawUnSelected ( Canvas canvas ) {

            int height = canvas.getHeight();
            int width = canvas.getWidth();
            int widthCenter = width / 2;

            float bigTextSize = Math.min( width, height ) * 0.4f;

            mPaint.setTextSize( bigTextSize );
            mPaint.setTextAlign( Align.CENTER );
            mPaint.setColor( Color.BLACK );
            int day = CalendarUtils.getDayOfMonth( mDate );
            canvas.drawText( String.valueOf( day ), widthCenter, height * 0.5f + BaseLineUtils.getBaselineOffset( mPaint ),
                             mPaint
            );
      }

      protected void drawSelected ( Canvas canvas ) {

            int height = canvas.getHeight();
            int width = canvas.getWidth();

            int widthCenter = width / 2;
            float paddingV = height * 0.1f;
            float paddingH = width * 0.1f;

            mPaint.setColor( Color.argb( 32, 0, 0, 0 ) );
            canvas.drawRect( paddingH, paddingV, width - paddingH, height - paddingV, mPaint );

            float bigTextSize = Math.min( width, height ) * 0.4f;

            mPaint.setTextSize( bigTextSize );
            mPaint.setTextAlign( Align.CENTER );
            mPaint.setColor( Color.BLACK );
            int day = CalendarUtils.getDayOfMonth( mDate );
            canvas.drawText( String.valueOf( day ), widthCenter, height * 0.5f + BaseLineUtils.getBaselineOffset( mPaint ),
                             mPaint
            );
      }

      public Date getDate ( ) {

            return mDate;
      }
}
