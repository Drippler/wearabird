package nirhart.wearabird;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageReceiverService extends WearableListenerService {

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		if (messageEvent.getPath().equals(ClientPaths.START_MEASUREMENT)) {
			startService(new Intent(this, SensorService.class));
		}

		if (messageEvent.getPath().equals(ClientPaths.STOP_MEASUREMENT)) {
			stopService(new Intent(this, SensorService.class));
		}
	}
}
