package inducesmile.com.opencvexample;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvSVM;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import inducesmile.com.opencvexample.utils.preprocess.ImagePreprocessor;
import inducesmile.com.opencvexample.utils.Constants;
import inducesmile.com.opencvexample.utils.FolderUtil;
import inducesmile.com.opencvexample.utils.Utilities;

import static inducesmile.com.opencvexample.Segmentaion.image;
import static org.opencv.BuildConfig.DEBUG;
import static org.opencv.core.Core.countNonZero;
import static org.opencv.core.Core.rectangle;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2RGB;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2GRAY;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.resize;
import static org.opencv.imgproc.Imgproc.threshold;
import static org.opencv.imgproc.Imgproc.warpAffine;

public class OpenCVCamera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2
{

    private static final String TAG = OpenCVCamera.class.getSimpleName();
    public static String State = "Wait";
    public static OpenCameraView cameraBridgeViewBase;
    private Mat colorRgba;
    private Mat colorGray;
    public static Mat image2;
    private Mat des, forward;
    private ImagePreprocessor preprocessor;
    float x1, x2, y1, y2;
    protected static ArrayList<Bitmap> xml = new ArrayList<Bitmap>();
    protected static ArrayList<Bitmap> xml_no = new ArrayList<>();
    ArrayList<Plate> output = new ArrayList<>();
    static Bitmap icon ;
    public static String outPicture = "";
    public static int FirstIterration = 0;
    TextView textView;
    public static ArrayList<Bitmap> OurListBaby = new ArrayList<>();
    public static int OurListIndexBaby = 0;
    private Handler handler = new Handler();
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                    cameraBridgeViewBase.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_open_cvcamera);


        Button button = (Button)findViewById(R.id.buttonset);
        textView =(TextView)findViewById(R.id.textinput);

        preprocessor = new ImagePreprocessor();

        cameraBridgeViewBase = (OpenCameraView) findViewById(R.id.camera_view);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        cameraBridgeViewBase.disableFpsMeter();



        /*OurListBaby.add(BitmapFactory.decodeResource(getResources(), R.drawable.s3n));
        OurListBaby.add(BitmapFactory.decodeResource(getResources(), R.drawable.s4));*/


        //A timer to be able to take a picture every X time //
        //Inside the onFinish() method we create a String which contains the file name of our image that we want to take //
        //We create a folder *ligne n-95 //
        //The we call takePicture() method with the cameraBridgeViewBase (an OpenCameraView object) //

      /*  new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish()
            {
                outPicture = Constants.SCAN_IMAGE_LOCATION + File.separator + Utilities.generateFilename();
                FolderUtil.createDefaultFolder(Constants.SCAN_IMAGE_LOCATION);

                //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.s6n);
               /* if(icon.getWidth()>800)
                {
                    if(icon.isMutable())
                    {icon.setWidth(800);}
                }*/

                /*PlateDetection plateDetection = new PlateDetection(icon);
                plateDetection.Segment();

                /*trainSVM trainSVM = new trainSVM(getApplicationContext());
                CvSVM cvSVM = trainSVM.train();
                ArrayList<Integer> result;
                result = trainSVM.predicat(Segmentaion.out, cvSVM);
                Toast.makeText(OpenCVCamera.this, "Picture has been taken ", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Path " + outPicture);
            }
        }.start();*/


    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    // We disable the view in case we pause the application //

    @Override
    public void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null)
            cameraBridgeViewBase.disableView();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    // When we resume our app we make sure that opencv library is connected too//

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    // We disable the view in case we close || stop the application //

    public void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null)
            cameraBridgeViewBase.disableView();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    //This method is invoked when camera preview has started. After this method is invoked the frames will start to be delivered to client via the onCameraFrame() method. //

    @Override
    public void onCameraViewStarted(int width, int height) {
        colorRgba = new Mat(height, width, CvType.CV_8UC4);
        colorGray = new Mat(height, width, CV_8UC1);

        des = new Mat(height, width, CvType.CV_8UC4);
        forward = new Mat(height, width, CvType.CV_8UC4);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    //This method is invoked when camera preview has been stopped for some reason, no frames will be delivered via onCameraFrame() callback after this method is called.

    @Override
    public void onCameraViewStopped() {
        colorRgba.release();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    //This method is invoked when delivery of the frame needs to be done,the returned values is a modified frame which needs to be displayed on the screen. //
    //The returned value is set to null because we do NOT want to display a preview of the camera //

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return null;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Called when the current Window of the activity gains or loses focus //

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void hideSystemUI() {
        // Enables regular immersive mode. //
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE. //
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY //
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Shows the system bars by removing all the flags //
// except for the ones that make the content appear under the system bars. //
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    //

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                if (x1 > x2) {
                    Intent cvIntent = new Intent(OpenCVCamera.this, Main2Activity.class);
                    startActivity(cvIntent);
                }

                break;
        }
        return false;
    }*/

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    // https://stackoverflow.com/questions/5383797/open-an-image-using-uri-in-androids-default-gallery-image-viewer#

    public ArrayList<Bitmap> getFromTrainData()
    {
        for (int i = 1; i <= 3; i++)//for (int i =000;i<100;i++)
        {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse("file://" + Environment.DIRECTORY_DOWNLOADS + i + ".jpg"), "image/*");
            //startActivity(intent);
            xml.add(BitmapFactory.decodeByteArray(intent.getByteArrayExtra(""), 0, intent.getByteArrayExtra("").length));
        }
        return xml;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean verifySizesForChar(Mat r) {
        //Char sizes 45x77
        //31x73
        float aspect = 45.0f / 77.0f;
        float charAspect = (float) r.cols() / (float) r.rows();
        float error = (float) 0.35;
        float minHeight = 15;
        float maxHeight = 28;
        //We have a different aspect ratio for number 1, and it can be ~0.2

        float minAspect = (float) 0.2;
        float maxAspect = aspect + aspect * error;

        //area of pixels
        if (r.channels() == 4) {
            cvtColor(r, r, COLOR_RGBA2GRAY);
        }


        float area = countNonZero(r);
        //bb area
        float bbArea = r.cols() * r.rows();
        //% of pixel in area
        float percPixels = area / bbArea;

        if (percPixels < 0.8 && charAspect > minAspect && charAspect < maxAspect && r.rows() >= minHeight && r.rows() < maxHeight)
            return true;
        else
            return false;

    }

    public void segmentation(View view)
    {
        Intent intent = new Intent(this,TakePictureService.class);
        startService(intent);

    }

}

