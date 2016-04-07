package nirhart.wearabird;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created on 06/04/2016.
 */
public class ConnectionManager {

	private static final int CLIENT_CONNECTION_TIMEOUT = 15000;
	private static ConnectionManager instance;
	private GoogleApiClient googleApiClient;
	private ExecutorService executorService;

	private ConnectionManager(Context context) {
		googleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
		executorService = Executors.newCachedThreadPool();
	}

	static public synchronized ConnectionManager getInstance(Context context) {
		if (instance == null) {
			instance = new ConnectionManager(context.getApplicationContext());
		}

		return instance;
	}

	private boolean validateConnection() {
		if (googleApiClient.isConnected()) {
			return true;
		}

		ConnectionResult result = googleApiClient.blockingConnect(CLIENT_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);

		return result.isSuccess();
	}

	public void sendMessage(final ConnectionManagerRunnable msg) {
		executorService.execute(msg);
	}

	static public abstract class ConnectionManagerRunnable implements Runnable {

		private final ConnectionManager connectionManager;

		public ConnectionManagerRunnable(Context context) {
			this.connectionManager = ConnectionManager.getInstance(context);
		}

		abstract public void send(GoogleApiClient googleApiClient);

		@Override
		final public void run() {
			if (connectionManager.validateConnection()) {
				send(connectionManager.googleApiClient);
			}
		}
	}
}
