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
public class MonthDayView extends View {

      /**
       * 未选中状态,
       */
      public static final int   UNSELECTED = 11;
      /**
       * 选中状态
       */
      public static final int   SELECTED   = 12;
      /**
       * 显示的日期
       */
      private             Date  mDate;
      /**
       * 绘制笔
       */
      private             Paint mPaint;
      /**
       * 当前状态 {@link #UNSELECTED}{@link #SELECTED}
       */
      private             int   mState;

      /**
       * 选中时颜色
       */
      private int mSelectColor = Color.parseColor( "#C94B87B5" );

      public MonthDayView ( Context context ) {

            this( context, null, 0 );
      }

      public MonthDayView ( Context context, @Nullable AttributeSet attrs ) {

            this( context, attrs, 0 );
      }

      public MonthDayView ( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {

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

      /**
       * 更改显示状态
       *
       * @param state 新的状态
       */
      void setState ( int state ) {

            if( mState != state ) {
                  mState = state;
                  invalidate();
            }
      }

      @Override
      protected void onDraw ( Canvas canvas ) {

            if( mState == UNSELECTED ) {
                  drawUnSelected( canvas );
                  return;
            }

            drawSelected( canvas );
      }

      /**
       * 设置数据
       *
       * @param date 数据
       */
      void bind ( Date date ) {

            if( date != mDate ) {

                  mDate = date;
                  invalidate();
            }
      }

      Date getDate ( ) {

            return mDate;
      }

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

            mPaint.setColor( mSelectColor );
            float radius = Math.min( height - 20, width - 20 ) >> 1;
            canvas.drawCircle( width >> 1, height >> 1, radius, mPaint );

            float bigTextSize = Math.min( width, height ) * 0.4f;

            mPaint.setTextSize( bigTextSize );
            mPaint.setTextAlign( Align.CENTER );
            mPaint.setColor( Color.BLACK );
            int day = CalendarUtils.getDayOfMonth( mDate );
            canvas.drawText( String.valueOf( day ), widthCenter, height * 0.5f + BaseLineUtils.getBaselineOffset( mPaint ),
                             mPaint
            );
      }
}
