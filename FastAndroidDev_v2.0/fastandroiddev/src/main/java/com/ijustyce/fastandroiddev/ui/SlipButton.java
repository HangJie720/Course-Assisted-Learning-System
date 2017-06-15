package com.ijustyce.fastandroiddev.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.ijustyce.fastandroiddev.R;


public class SlipButton extends View implements OnTouchListener {

	private boolean NowChoose = false;// default value of SlipButton
	private boolean isChecked;
	private boolean OnSlip = false;// whether user is sliding
	private float DownX, NowX;
	private Rect Btn_On, Btn_Off;
	private boolean isChgLsnOn = false;
	private OnChangedListener ChgLsn;
	private Bitmap bg_on, bg_off, slip_btn;

	private Matrix matrix;
	private Paint paint;

	public SlipButton(Context context) {
		super(context);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlipButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {

//		bg_on = BitmapFactory.decodeResource(getResources(),
//				R.mipmap.split_left_1);
		bg_off = BitmapFactory.decodeResource(getResources(),
				R.mipmap.split_right_1);
		slip_btn = BitmapFactory.decodeResource(getResources(),
				R.mipmap.split_1);

		Btn_On = new Rect(0, 0, slip_btn.getWidth(), slip_btn.getHeight());
		Btn_Off = new Rect(bg_off.getWidth() - slip_btn.getWidth(), 0,
				bg_off.getWidth(), slip_btn.getHeight());
		setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		if (matrix == null) {
			matrix = new Matrix();
		}
		if (paint == null) {
			paint = new Paint();
		}
		float x;

		if (NowX < (bg_on.getWidth() / 2)) {
			x = NowX - slip_btn.getWidth() / 2;
			canvas.drawBitmap(bg_off, matrix, paint); // draw background of
														// close
		}

		else {
			x = bg_on.getWidth() - slip_btn.getWidth() / 2;
			canvas.drawBitmap(bg_on, matrix, paint); // draw background of open
		}

		if (OnSlip) // if is sliding

		{
			if (NowX >= bg_on.getWidth())

				x = bg_on.getWidth() - slip_btn.getWidth() / 2;

			else if (NowX < 0) {
				x = 0;
			} else {
				x = NowX - slip_btn.getWidth() / 2;
			}
		} else {

			if (NowChoose) {
				x = Btn_Off.left;
				canvas.drawBitmap(bg_on, matrix, paint);
			} else
				x = Btn_On.left;
		}
		if (isChecked) {
			canvas.drawBitmap(bg_on, matrix, paint);
			x = Btn_Off.left;
			isChecked = !isChecked;
		}

		if (x < 0)
			x = 0;
		else if (x > bg_on.getWidth() - slip_btn.getWidth())
			x = bg_on.getWidth() - slip_btn.getWidth();
		canvas.drawBitmap(slip_btn, x, 0, paint);

	}

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction())

		{
		case MotionEvent.ACTION_MOVE:
			NowX = event.getX();
			break;

		case MotionEvent.ACTION_DOWN:

			if (event.getX() > bg_on.getWidth()
					|| event.getY() > bg_on.getHeight())
				return false;
			OnSlip = true;
			DownX = event.getX();
			NowX = DownX;
			break;

		case MotionEvent.ACTION_CANCEL:

			OnSlip = false;
			boolean choose = NowChoose;
			if (NowX >= (bg_on.getWidth() / 2)) {
				NowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				NowChoose = true;
			} else {
				NowX = NowX - slip_btn.getWidth() / 2;
				NowChoose = false;
			}
			if (isChgLsnOn && (choose != NowChoose))
				ChgLsn.OnChanged(v.getId() , NowChoose);
			break;
		case MotionEvent.ACTION_UP:

			OnSlip = false;
			boolean LastChoose = NowChoose;

			if (event.getX() >= (bg_on.getWidth() / 2)) {
				NowX = bg_on.getWidth() - slip_btn.getWidth() / 2;
				NowChoose = true;
			}

			else {
				NowX = NowX - slip_btn.getWidth() / 2;
				NowChoose = false;
			}

			if (isChgLsnOn && (LastChoose != NowChoose))

				ChgLsn.OnChanged(v.getId() , NowChoose);
			break;
		default:
		}
		invalidate();
		return true;
	}

	public void SetOnChangedListener(OnChangedListener l) {
		isChgLsnOn = true;
		ChgLsn = l;
	}

	public interface OnChangedListener {
		abstract void OnChanged(int id, boolean CheckState);
	}

	public void setCheck(boolean isChecked) {
		this.isChecked = isChecked;
		NowChoose = isChecked;
	}
}