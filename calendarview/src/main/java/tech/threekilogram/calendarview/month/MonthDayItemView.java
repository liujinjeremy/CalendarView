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
public class MonthDayItemView extends View implements IMonthDayItem {

      private Date mDate;
      private Paint mPaint;
      private int mState;

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

      @Override
      protected void onDraw ( Canvas canvas ) {

            onDrawState( mState, canvas );
      }

      @Override
      public View getView ( ) {

            return this;
      }

      @Override
      public void bind ( Date date ) {

            if( date != mDate ) {

                  mDate = date;
                  invalidate();
            }
      }

      @Override
      public void setState ( int state ) {

            if( state != mState ) {
                  mState = state;
                  invalidate();
            }
      }

      @Override
      public void onDrawState ( int state, Canvas canvas ) {

            if( state == OUT_MONTH ) {
                  drawOutMonth( canvas );
                  return;
            }

            if( state == IN_MONTH_UNSELECTED ) {
                  drawInMonthUnSelected( canvas );
                  return;
            }

            drawSelected( canvas );
      }

      /**
       * drawable nothing
       */
      protected void drawOutMonth ( Canvas canvas ) { }

      /**
       * drawable nothing
       */
      protected void drawInMonthUnSelected ( Canvas canvas ) {

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
