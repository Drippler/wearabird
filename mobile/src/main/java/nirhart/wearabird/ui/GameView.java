package nirhart.wearabird.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import nirhart.wearabird.R;
import nirhart.wearabird.ui.model.Clouds;
import nirhart.wearabird.ui.model.Coins;
import nirhart.wearabird.ui.model.Player;
import nirhart.wearabird.ui.model.components.Speed;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private Paint backgroundPaint;
	private GameLoopThread thread;
	private Player player;
	private Clouds clouds;
	private Coins coins;
	private TextView stateView;
	private boolean isConnected;
	private Handler mainThreadHandler;
	private long gameOverTime;

	public GameView(Context context) {
		super(context);
		init(context);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public GameView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	private void init(@SuppressWarnings("UnusedParameters") Context context) {
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.RGBA_8888);
		setKeepScreenOn(true);
		mainThreadHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new GameLoopThread(holder, this);
		thread.setRunning(true);
		thread.start();
		pause();
	}

	public void initDrawables(int width, int height) {

		Shader backgroundShader = new LinearGradient(0, 0, 0, height, Color.parseColor("#64CFEE"), Color.parseColor("#B8EFFF"), Shader.TileMode.CLAMP);
		backgroundPaint = new Paint();
		backgroundPaint.setDither(true);
		backgroundPaint.setShader(backgroundShader);

		Speed speed = new Speed(-1, 0, 0, 0);

		player = new Player(getContext(), new int[]{
				R.drawable.bird0000, R.drawable.bird0001, R.drawable.bird0002,
				R.drawable.bird0003, R.drawable.bird0004, R.drawable.bird0005,
				R.drawable.bird0006, R.drawable.bird0007, R.drawable.bird0008,
				R.drawable.bird0009, R.drawable.bird0010, R.drawable.bird0011,
				R.drawable.bird0012, R.drawable.bird0013, R.drawable.bird0014
		}, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics()), height);

		clouds = new Clouds(getContext(), new int[]{
				R.drawable.cloud1, R.drawable.cloud2, R.drawable.cloud3,
				R.drawable.cloud4, R.drawable.cloud5, R.drawable.cloud6,
				R.drawable.cloud7, R.drawable.cloud8, R.drawable.cloud9
		}, 15, speed, width, height);

		coins = new Coins(getContext(), new int[]{
				R.drawable.coin_0, R.drawable.coin_1, R.drawable.coin_2, R.drawable.coin_3,
				R.drawable.coin_4, R.drawable.coin_5, R.drawable.coin_6, R.drawable.coin_7,
				R.drawable.coin_8, R.drawable.coin_9, R.drawable.coin_10, R.drawable.coin_11
		}, speed, width, height, player);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException ignore) {
			}
		}
	}

	public void render(Canvas canvas) {
		canvas.drawPaint(backgroundPaint);
		clouds.draw(canvas);
		coins.draw(canvas);
		player.draw(canvas);
	}

	public void update() {
		player.update();
		coins.update();
		clouds.update();

		if (player.getY() < -player.getHeight() * 2 || player.getY() > getHeight() + player.getHeight()) {
			mainThreadHandler.post(new Runnable() {
				@Override
				public void run() {
					pause();
					gameOverTime = System.currentTimeMillis();
					stateView.setVisibility(VISIBLE);
					stateView.setText(String.format(Locale.US, getContext().getString(R.string.game_over), coins.getCoinsCollected()));
					stateView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.connected, 0, 0);
				}
			});
		}
	}

	public GameLoopThread getThread() {
		return thread;
	}

	public void wave() {
		if (System.currentTimeMillis() - gameOverTime > TimeUnit.SECONDS.toMillis(1)) {
			stateView.setVisibility(GONE);
			thread.unPause();
			player.getSpeed().setYv(-5);
		}
	}

	public void setStateView(TextView stateView) {
		this.stateView = stateView;
	}

	public void pause() {
		thread.pause();
		thread.init();
		stateView.setVisibility(VISIBLE);
		if (isConnected) {
			stateView.setText(R.string.wave_to_start);
			stateView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.connected, 0, 0);
		} else {
			stateView.setText(R.string.wearable_not_found);
			stateView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.connecting, 0, 0);
		}
	}

	public void ping() {
		if (!this.isConnected) {
			this.isConnected = true;
			if (thread != null && stateView.getVisibility() == VISIBLE) {
				pause();
			}
		}
	}
}