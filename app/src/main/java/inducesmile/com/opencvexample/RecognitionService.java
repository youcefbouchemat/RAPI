package inducesmile.com.opencvexample;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class RecognitionService extends Service
{

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Intent intentEndClass = new Intent(this,ClassificationService.class);
        stopService(intentEndClass);
        PlateRecognition plateRecognition = new PlateRecognition();
        PlateRecognition.PlateCaracters = plateRecognition.Recognition(PlateDetection.Plates);
        OpenCVCamera.FirstIterration++;
        int a = PlateRecognition.PlateCaracters.size();
        //OpenCVCamera.setText();
        if(checkAvailable())
        {
            if(checkWritable())
            {
                Intent intentTP = new Intent(this,TakePictureService.class);
                startService(intentTP);

            }
            else
            {
                Toast.makeText(this,"Storage FULL", Toast.LENGTH_LONG).show();
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean checkAvailable() {

        // Retrieving the external storage state
        String state = Environment.getExternalStorageState();

        // Check if available
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }

        return false;
    }

    public static boolean checkWritable() {

        // Retrieving the external storage state
        String state = Environment.getExternalStorageState();

        // Check if writable
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        return false;

    }
}
