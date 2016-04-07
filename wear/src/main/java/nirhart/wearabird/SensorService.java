package nirhart.wearabird;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

public class SensorService extends Service implements SensorEventListener {

	SensorManager mSensorManager;

	private DeviceClient client;

	@Override
	public void onCreate() {
		super.onCreate();

		client = new DeviceClient(this);

		Notification.Builder builder = new Notification.Builder(this);
		builder.setContentTitle(getString(R.string.app_name));
		builder.setContentText(getString(R.string.foreground_service_description_text));
		builder.setSmallIcon(R.drawable.ic_launcher);

		startForeground(1, builder.build());

		startMeasurement();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopMeasurement();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	protected void startMeasurement() {
		mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
		Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		if (sensor != null) {
			mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	private void stopMeasurement() {
		if (mSensorManager != null) {
			mSensorManager.unregisterListener(this);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		client.sendSensorData(event.sensor.getType(), event.accuracy, event.timestamp, event.values);
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
