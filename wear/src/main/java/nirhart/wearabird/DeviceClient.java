package nirhart.wearabird;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class DeviceClient {

	private final Context context;

	public DeviceClient(Context context) {
		this.context = context;
	}

	public void sendSensorData(final int sensorType, final int accuracy, final long timestamp, final float[] values) {
		ConnectionManager.getInstance(context).sendMessage(new ConnectionManager.ConnectionManagerRunnable(context) {
			@Override
			public void send(GoogleApiClient googleApiClient) {
				PutDataMapRequest dataMap = PutDataMapRequest.create("/sensors/" + sensorType);

				dataMap.getDataMap().putInt(DataMapKeys.ACCURACY, accuracy);
				dataMap.getDataMap().putLong(DataMapKeys.TIMESTAMP, timestamp);
				dataMap.getDataMap().putFloatArray(DataMapKeys.VALUES, values);

				PutDataRequest putDataRequest = dataMap.asPutDataRequest();

				Wearable.DataApi.putDataItem(googleApiClient, putDataRequest);
			}
		});
	}
}
