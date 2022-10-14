package inducesmile.com.opencvexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import inducesmile.com.opencvexample.utils.Utilities;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2BGR;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.Sobel;
import static org.opencv.imgproc.Imgproc.blur;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static org.opencv.imgproc.Imgproc.threshold;

public class OpenCameraView extends JavaCameraView implements Camera.PictureCallback
{

    private static final String TAG = OpenCameraView.class.getSimpleName();
    static File root = new File(Environment.getExternalStorageDirectory() + "/LWK/");
    private String mPictureFileName;
    public static int minWidthQuality = 400;
    private Context context;
    public static Bitmap OurImage;
    private Mat mat ;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    public OpenCameraView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////*/

    // In this method we take a picture with a pre-definite methods  //

    public void takePicture(final String fileName)
    {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        mCamera.setPreviewCallback(null);
        mCamera.takePicture(null, null, this);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Called when image data is available after a picture is taken in takePicture() //
    // In this method we simply save the image //

    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Uri uri = Uri.parse(mPictureFileName);

        Log.d(TAG, "selectedImage: " + uri);
        OurImage = null;
        OurImage = rotate(bitmap, 90);


        // Bitmap bmp = Bitmap.createBitmap(144, 33, Bitmap.Config.ARGB_8888);


        //Mat mat = Segmentaion.out.get(0);
        //Mat mat = Segmentaion.image;
        //Mat mat = trainSVM.img;
        //Bitmap bmp=Bitmap.createBitmap(trainSVM.imageWidth  ,trainSVM.imageHeight,Bitmap.Config.ARGB_8888);
        //Bitmap bmp=Bitmap.createBitmap(mat.width() ,mat.height(),Bitmap.Config.ARGB_8888);

        // Utils.matToBitmap(PlateDetection.output.get(0).getPlateImg(),bmp);
        //Utils.matToBitmap(mat,bmp); //joighuijuov

        //Bitmap bmp1 = BitmapFactory.decodeResource(getResources(),R.drawable.nb);


        // Write the image in a file (in jpeg format)
        /*try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);
            OurImage.compress(Bitmap.CompressFormat.JPEG, 80, fos); //Bm is the Bitmap that contains our image //
            fos.close();
            Log.d(TAG, "copyyyyyy " + uri);

        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }*/
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

//rotate the captured image to normal orientation since OpenCV camera has a default image orientation of 270 degrees.

    private static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return bmOut;
        }
        return bm;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Bitmap toBit(Drawable drawable)
    {
        if(drawable instanceof BitmapDrawable)
        {return ((BitmapDrawable)drawable).getBitmap();}

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

}
