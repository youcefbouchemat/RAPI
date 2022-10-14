package inducesmile.com.opencvexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.ml.CvSVM;

import java.lang.reflect.Field;
import java.util.ArrayList;


import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.resize;

import org.opencv.core.Mat;
import org.opencv.ml.CvSVM;


import java.util.ArrayList;

import static org.opencv.core.CvType.CV_32FC1;

public class trainSVM {


    public trainSVM(Context context) {
        this.context = context;
    }

    private Context context;

    public static Mat img = new Mat();

    public static String path_Plates = "plate";
    public static String path_No_Plates = "no_plate";
    public static int numPlates = 155;
    public static int numNoPlates = 206;
    public static int imageWidth = 144;
    public static int imageHeight = 33;


    public static Mat classes = new Mat(numPlates + numNoPlates, 1, CV_32FC1);//(numPlates+numNoPlates, 1, CV_32FC1);
    public static Mat trainingData = new Mat(numPlates + numNoPlates, imageWidth * imageHeight, CV_32FC1);//(numPlates+numNoPlates, imageWidth*imageHeight, CV_32FC1 );

    Mat trainingImages = new Mat();
    ArrayList<Integer> trainingLabels = new ArrayList<>();
    int classesIndex = 0;

    public CvSVM train() {
        for (int i = 0; i < numPlates; i++) {
            int j = i + 1;
            String pic_path = path_Plates + j;
            Mat img = DrawbleToMat(pic_path);
            if (img.get(0, 0).length == 3) {
                cvtColor(img, img, COLOR_BGR2GRAY);
            }

            if (img.get(0, 0).length == 4) {
                cvtColor(img, img, COLOR_BGRA2GRAY);
            }
            resize(img, img, new Size(144, 33));
            img = reshapeMat(img);
            trainingImages.push_back(img);
            int result = classes.put(classesIndex, 0, 1);
            classesIndex++;
        }


        for (int i = 0; i < numNoPlates; i++) {
            int j = i + 1;
            String pic_path = path_No_Plates + j;
            Mat img = DrawbleToMat(pic_path);
            if (img.get(0, 0).length == 3) {
                cvtColor(img, img, COLOR_BGR2GRAY);
            }

            if (img.get(0, 0).length == 4) {
                cvtColor(img, img, COLOR_BGRA2GRAY);
            }
            resize(img, img, new Size(144, 33));
            img = reshapeMat(img);
            trainingImages.push_back(img);
            int result = classes.put(classesIndex, 0, 0);
            classesIndex++;

        }

        //trainingLabels.copyTo(classes);
        trainingImages.copyTo(trainingData);
        //trainingData = trainingData.reshape(1,trainingData.rows);
        trainingData.convertTo(trainingData, CV_32FC1);
        org.opencv.ml.CvSVMParams svmParams = new org.opencv.ml.CvSVMParams();
        svmParams.set_kernel_type(CvSVM.LINEAR);

        CvSVM svmClassifier = new CvSVM(trainingData, classes, new Mat(), new Mat(), svmParams);

        return svmClassifier;

    }


    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Mat DrawbleToMat(String input) {
        int resID = getResId(input, R.drawable.class); // or other resource class
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
        Mat output = new Mat();
        Utils.bitmapToMat(bitmap, output);
        return output;
    }

    public Mat reshapeMat(Mat input) {
        int outputIndex = 0;
        Mat output = new Mat(1, imageWidth * imageHeight, CV_32FC1);
        for (int i = 0; i < input.rows(); i++) {
            for (int j = 0; j < input.cols(); j++) {
                output.put(0, outputIndex, input.get(i, j));
                outputIndex++;
            }
        }
        return output;
    }

    public ArrayList<Integer> predicat(ArrayList<Mat> input, CvSVM svmClassifier) {
        ArrayList<Integer> plates = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            Mat img = input.get(i);
            Mat p = reshapeMat(img);//convert img to 1 row m features
            int response = (int) svmClassifier.predict(p);
            if (response == 1)
                plates.add(i);
        }
        if (plates.size() != 0) {
            img = input.get(plates.get(0));
        }
        return plates;
    }
}