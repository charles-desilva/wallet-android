package Process_Classes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncData extends Service {
    public SyncData() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
