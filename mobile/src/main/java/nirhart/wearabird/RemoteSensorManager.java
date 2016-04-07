package nirhart.wearabird;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class RemoteSensorManager {
	private static RemoteSensorManager instance;
	private final Context context;

	private RemoteSensorManagerCallbacks callbacks;

	private RemoteSensorManager(Context context) {
		this.context = context.getApplicationContext();
	}

	public static synchronized RemoteSensorManager getInstance(Context context) {
		if (instance == null) {
			instance = new RemoteSensorManager(context.getApplicationContext());
		}

		return instance;
	}

	public void startMeasurement(RemoteSensorManagerCallbacks callbacks) {
		ConnectionManager.getInstance(context).sendMessage(new ConnectionManager.ConnectionManagerRunnable(context) {
			@Override
			public void send(GoogleApiClient googleApiClient) {
				controlMeasurementInBackground(googleApiClient, ClientPaths.START_MEASUREMENT);
			}
		});
		this.callbacks = callbacks;
	}

	public void stopMeasurement() {
		ConnectionManager.getInstance(context).sendMessage(new ConnectionManager.ConnectionManagerRunnable(context) {
			@Override
			public void send(GoogleApiClient googleApiClient) {
				controlMeasurementInBackground(googleApiClient, ClientPaths.STOP_MEASUREMENT);
			}
		});
		this.callbacks = null;
	}

	private void controlMeasurementInBackground(GoogleApiClient googleApiClient, final String path) {
		List<Node> nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await().getNodes();

		for (Node node : nodes) {
			Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, null);
		}
	}

	public void wave() {
		if (this.callbacks != null) {
			this.callbacks.onWave();
		}
	}

	public void ping() {
		if (this.callbacks != null) {
			this.callbacks.ping();
		}
	}

	public interface RemoteSensorManagerCallbacks {
		void onWave();

		void ping();
	}
}
