package sg.edu.rp.webservices.lesson9ps;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class MyService extends Service {
    public MyService() {
    }

    boolean started;
    private FusedLocationProviderClient client;
    private LocationCallback mLocationCallback;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("Service", "onCreate");
        super.onCreate();

        client = LocationServices.getFusedLocationProviderClient(this);

        createLocationCallback();

        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
        File folder = new File(folderLocation);
        if (folder.exists() == false){
            boolean result = folder.mkdir();
            if (result == false){
                Toast.makeText(MyService.this, "Folder cannot be created in External memory, " + "Service exiting", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onCreate");
        if (checkPermission() == true) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setSmallestDisplacement(100);

            client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
        else {
            stopSelf();
        }

        return Service.START_STICKY;
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location locationData = locationResult.getLastLocation();

                    String data = locationData.getLatitude() + " , " + locationData.getLongitude();
                    Log.d("Service - Location changed", data);

                    String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
                    File targetFile = new File(folderLocation, "data.txt");

                    try {
                        FileWriter writer = new FileWriter(targetFile, true);
                        writer.write(data +"\n");
                        writer.flush();
                        writer.close();

                    } catch (Exception e) {
                        Toast.makeText(MyService.this, "Failed to Write!",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        };
    }

}
