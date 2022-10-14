package inducesmile.com.opencvexample;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class SegmentationService extends Service
{

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Intent intentEndTPic = new Intent(this,TakePictureService.class);
        stopService(intentEndTPic);
        //PlateDetection plateDetection = new PlateDetection(OpenCameraView.OurImage);
        PlateDetection plateDetection = new PlateDetection(BitmapFactory.decodeResource(getResources(), R.drawable.s6n));
        //PlateDetection plateDetection = new PlateDetection(OpenCVCamera.OurListBaby.get(OpenCVCamera.OurListIndexBaby));
        //OpenCVCamera.OurListIndexBaby ++;
        PlateDetection.Plates = plateDetection.Segment();
        Intent intentClass = new Intent(this,ClassificationService.class);
        startService(intentClass);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
