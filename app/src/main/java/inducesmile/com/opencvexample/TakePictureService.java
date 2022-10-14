package inducesmile.com.opencvexample;

import android.app.Service;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

import org.opencv.android.CameraBridgeViewBase;

public class TakePictureService extends Service
{

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(OpenCVCamera.FirstIterration != 0)
        {
            Intent intentEndRecog = new Intent(this, RecognitionService.class);
            stopService(intentEndRecog);
        }

        OpenCVCamera.cameraBridgeViewBase.takePicture(OpenCVCamera.outPicture);
        Intent intentSeg = new Intent(this, SegmentationService.class);
        startService(intentSeg);

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
