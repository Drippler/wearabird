package nirhart.wearabird;

import android.net.Uri;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class SensorReceiverService extends WearableListenerService {

	private int lastSensorValue = Integer.MIN_VALUE;
	private RemoteSensorManager remoteSensorManager;

	@Override
	public void onCreate() {
		super.onCreate();

		remoteSensorManager = RemoteSensorManager.getInstance(this);
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		for (DataEvent dataEvent : dataEvents) {
			if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
				DataItem dataItem = dataEvent.getDataItem();
				Uri uri = dataItem.getUri();
				String path = uri.getPath();

				if (path.startsWith("/sensors/")) {
					unpackSensorData(
							Integer.parseInt(uri.getLastPathSegment()),
							DataMapItem.fromDataItem(dataItem).getDataMap()
					);
				}
			}
		}
	}

	private void unpackSensorData(@SuppressWarnings("UnusedParameters") int sensorType, DataMap dataMap) {
		float[] values = dataMap.getFloatArray(DataMapKeys.VALUES);
		if (values.length >= 3) {
			int val = (int) values[2];

			if (Math.abs(val - lastSensorValue) > 1) {
				remoteSensorManager.wave();
			}

			lastSensorValue = val;
		}
		remoteSensorManager.ping();
	}
}
