package inducesmile.com.opencvexample;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.ArrayList;


public class Ocr {
    public static String Ocr(Mat input)
    {
        OcrManager manager = new OcrManager();
        manager.initAPI();

        String output="";
        ArrayList<Mat> Chars=CharSegment.OcrSegmentation(input);

        for (int i = 0; i < Chars.size(); i++)
        {
            Mat Char=CharSegment.preprocessChar(Chars.get(i),2);
            Bitmap bitmap=Bitmap.createBitmap(Char.cols(),Char.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(Char,bitmap);

            String s=manager.startRecognize(bitmap);
            if(s.length()>1)
            {
                Mat image=CharSegment.preprocessChar(Chars.get(i),0);
                bitmap=Bitmap.createBitmap(image.cols(),image.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(image,bitmap);
                s=manager.startRecognize(bitmap);
            }
            output=output+s;
        }

        return output;
    }
}
