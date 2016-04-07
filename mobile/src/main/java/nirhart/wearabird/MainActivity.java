package nirhart.wearabird;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import nirhart.wearabird.ui.GameView;

public class MainActivity extends Activity {

	private RemoteSensorManager remoteSensorManager;
	private GameView gameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(nirhart.wearabird.R.layout.activity_main);
		gameView = (GameView) findViewById(nirhart.wearabird.R.id.activity_main_gameview);
		gameView.setStateView((TextView) findViewById(nirhart.wearabird.R.id.activity_main_textview));

		remoteSensorManager = RemoteSensorManager.getInstance(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		remoteSensorManager.startMeasurement(new RemoteSensorManager.RemoteSensorManagerCallbacks() {
			@Override
			public void onWave() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						gameView.wave();
					}
				});
			}

			@Override
			public void ping() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						gameView.ping();
					}
				});
			}
		});

		hideSystemUI();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (gameView != null && gameView.getThread() != null) {
			gameView.pause();
		}
		remoteSensorManager.stopMeasurement();
	}


	private void hideSystemUI() {
		int windowVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			windowVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;
		}

		getWindow().getDecorView().setSystemUiVisibility(windowVisibility);
	}
}
