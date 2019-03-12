package tech.threekilogram.mockevent;

import android.app.Activity;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Liujin 2019/3/11:20:14:44
 */
public class MockEvent {

      private float mDownX;
      private float mDownY;
      private float mCurrentX;
      private float mCurrentY;
      private float mUpX;
      private float mUpY;
      private long  mDownTime;

      public void dispatchDown ( View view ) {

            dispatchDown( view, 0, 0 );
      }

      public void dispatchDown ( View view, float downX, float downY ) {

            long downTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(
                downTime,
                downTime,
                MotionEvent.ACTION_DOWN,
                downX,
                downY,
                0
            );

            mDownTime = downTime;
            mDownX = mCurrentX = downX;
            mDownY = mCurrentY = downY;

            view.dispatchTouchEvent( event );
            event.recycle();
      }

      public void dispatchMoveBy ( View view, float dX, float dY ) {

            dispatchMove( view, mCurrentX + dX, mCurrentY + dY );
      }

      public void dispatchMove ( View view, float moveX, float moveY ) {

            long eventTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(
                mDownTime,
                eventTime,
                MotionEvent.ACTION_MOVE,
                moveX,
                moveY,
                0
            );

            mCurrentX = moveX;
            mCurrentY = moveY;

            view.dispatchTouchEvent( event );
            event.recycle();
      }

      public void dispatchUp ( View view ) {

            dispatchUp( view, mCurrentX, mCurrentY );
      }

      public void dispatchUp ( View view, float upX, float upY ) {

            long eventTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(
                mDownTime,
                eventTime,
                MotionEvent.ACTION_UP,
                upX,
                upY,
                0
            );

            mUpX = mCurrentX = upX;
            mUpY = mCurrentY = upY;

            view.dispatchTouchEvent( event );
            event.recycle();
      }

      /*==================================================================================*/

      public void dispatchDown ( Activity activity ) {

            dispatchDown( activity, 0, 0 );
      }

      public void dispatchDown ( Activity activity, float downX, float downY ) {

            long downTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(
                downTime,
                downTime,
                MotionEvent.ACTION_DOWN,
                downX,
                downY,
                0
            );

            mDownTime = downTime;
            mDownX = mCurrentX = downX;
            mDownY = mCurrentY = downY;

            activity.dispatchTouchEvent( event );
            event.recycle();
      }

      public void dispatchMoveBy ( Activity activity, float dX, float dY ) {

            dispatchMove( activity, mCurrentX + dX, mCurrentY + dY );
      }

      public void dispatchMove ( Activity activity, float moveX, float moveY ) {

            long eventTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(
                mDownTime,
                eventTime,
                MotionEvent.ACTION_MOVE,
                moveX,
                moveY,
                0
            );

            mCurrentX = moveX;
            mCurrentY = moveY;

            activity.dispatchTouchEvent( event );
            event.recycle();
      }

      public void dispatchUp ( Activity activity ) {

            dispatchUp( activity, mCurrentX, mCurrentY );
      }

      public void dispatchUp ( Activity activity, float upX, float upY ) {

            long eventTime = SystemClock.uptimeMillis();
            MotionEvent event = MotionEvent.obtain(
                mDownTime,
                eventTime,
                MotionEvent.ACTION_UP,
                upX,
                upY,
                0
            );

            mUpX = mCurrentX = upX;
            mUpY = mCurrentY = upY;

            activity.dispatchTouchEvent( event );
            event.recycle();
      }
}
