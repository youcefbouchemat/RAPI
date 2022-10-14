package inducesmile.com.opencvexample;

import android.app.Service;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.opencv.ml.CvSVM;

import java.util.ArrayList;

public class ClassificationService extends Service
{

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Intent intentEndSeg = new Intent(this,SegmentationService.class);
        stopService(intentEndSeg);
        trainSVM trainSVM = new trainSVM(this);
        CvSVM cvSVM = trainSVM.train();
        ArrayList<Integer> result;
        result = trainSVM.predicat(PlateDetection.Plates, cvSVM);
        OpenCVCamera.State = "Done";
        //PlateDetection.Plates = PlateDetection.Classify(PlateDetection.Plates);
        Intent intentRecog = new Intent(this,RecognitionService.class);
        startService(intentRecog);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

}
