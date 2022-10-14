package inducesmile.com.opencvexample;

import android.provider.Settings;
import android.support.v7.util.SortedList;
import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import inducesmile.com.opencvexample.tangible.Arrays;
import inducesmile.com.opencvexample.tangible.RandomNumbers;

import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Vector;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.opencv.android.NativeCameraView.TAG;
import static org.opencv.core.Core.KMEANS_PP_CENTERS;
import static org.opencv.core.Core.KMEANS_RANDOM_CENTERS;
import static org.opencv.core.Core.addWeighted;
import static org.opencv.core.Core.circle;
import static org.opencv.core.Core.line;
import static org.opencv.core.Core.merge;
import static org.opencv.core.Core.split;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.BORDER_DEFAULT;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2RGBA;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2BGR;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2RGB;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGRA;
import static org.opencv.imgproc.Imgproc.COLOR_HSV2BGR;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2BGR;
import static org.opencv.imgproc.Imgproc.COLOR_RGB2GRAY;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.Sobel;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_OTSU;
import static org.opencv.imgproc.Imgproc.blur;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.equalizeHist;
import static org.opencv.imgproc.Imgproc.findContours;
import static org.opencv.imgproc.Imgproc.floodFill;
import static org.opencv.imgproc.Imgproc.getRectSubPix;
import static org.opencv.imgproc.Imgproc.getRotationMatrix2D;
import static org.opencv.imgproc.Imgproc.getStructuringElement;
import static org.opencv.imgproc.Imgproc.minAreaRect;
import static org.opencv.imgproc.Imgproc.morphologyEx;
import static org.opencv.imgproc.Imgproc.resize;
import static org.opencv.imgproc.Imgproc.threshold;
import static org.opencv.imgproc.Imgproc.warpAffine;

public class Segmentaion
{
    static Mat image = new Mat();
    public static ArrayList<Mat> out ;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    static void ApplyFilter(Mat inFrame, Mat outFrame, double alpha)
    {
        Mat black = new Mat(inFrame.rows(), inFrame.cols(), inFrame.type(), new Scalar(0,0));
        double beta = (1.0 - alpha);
        addWeighted(inFrame, alpha, black, beta, 0.0, outFrame);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean verify_one(RotatedRect candidate)
    {
        float error= (float) 0.4;
        //Algeria car plate size: 52x11 aspect 4,7272
        final float aspect  = (float) 4.7272;

        //Set a min and max area. All other patches are discarded
        double min  = (int) (55*aspect*55); // minimum area
        double max  = (int) (70*aspect*70); // maximum area

        //Get only patches that match to a respect ratio.
        float rmin= aspect-aspect*error;
        float rmax= aspect+aspect*error;

        int area= (int) (candidate.size.height * candidate.size.width);
        float r= (float) (candidate.size.width / (float) candidate.size.height);

        if(r<1)
        { r= 1/r;  /*r= candidate.size.height / candidate.size.width */}

        if(( area < min || area > max ) || ( r < rmin || r > rmax ))
        { return false; }
        else
        { return true; }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean verify_two(RotatedRect candidate)
    {
        float error= (float) 0.4;
        //Algeria car plate size: 52x11 aspect 4,7272
        final float aspect  = (float) 4.7272;

        //Set a min and max area. All other patches are discarded
        double min  = (int) (70*aspect*70); // minimum area
        double max  = (int) (125*aspect*125); // maximum area

        //Get only patches that match to a respect ratio.
        float rmin= aspect-aspect*error;
        float rmax= aspect+aspect*error;

        int area= (int) (candidate.size.height * candidate.size.width);
        float r= (float) (candidate.size.width / (float) candidate.size.height);

        if(r<1)
        { r= 1/r;  /*r= candidate.size.height / candidate.size.width */}

        if(( area < min || area > max ) || ( r < rmin || r > rmax ))
        { return false; }
        else
        { return true; }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean verify_three(RotatedRect candidate)
    {
        float error= (float) 0.4;
        //Algeria car plate size: 52x11 aspect 4,7272
        final float aspect  = (float) 4.7272;

        //Set a min and max area. All other patches are discarded
        double min  = (int) (30*aspect*10); // minimum area
        double max  = (int) (170*aspect*170); // maximum area

        //Get only patches that match to a respect ratio.
        float rmin= aspect-aspect*error;
        float rmax= aspect+aspect*error;

        int area= (int) (candidate.size.height * candidate.size.width);
        float r= (float) (candidate.size.width / (float) candidate.size.height);

        if(r<1)
        { r= 1/r;  /*r= candidate.size.height / candidate.size.width */}

        if((/* area < min || area > max */area == min) || ( r < rmin || r > rmax ))
        { return false; }
        else
        { return true; }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static boolean verifySizes(RotatedRect candidate)
    {
        float error= (float) 0.4;
        //Algeria car plate size: 52x11 aspect 4,7272
        final float aspect  = (float) 4.7272;

        //Set a min and max area. All other patches are discarded
        double min  = (int) (192.5*aspect*192.5); // minimum area
        double max  = (int) (193*aspect*193); // maximum area

        //Get only patches that match to a respect ratio.
        float rmin= aspect-aspect*error;
        float rmax= aspect+aspect*error;

        int area= (int) (candidate.size.height * candidate.size.width);
        float r= (float) (candidate.size.width / (float) candidate.size.height);

        if(r<1)
        { r= 1/r;  /*r= candidate.size.height / candidate.size.width */}

        if(( area < min  || area > max ) || ( r < rmin || r > rmax ))
        { return false; }
        else
        { return true; }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Mat histeq (Mat in)
    {
        Mat out = new Mat(in.size(), in.type());
        if(in.channels()==3){
            Mat hsv = new Mat();
            Vector<Mat> hsvSplit = null;
            cvtColor(in, hsv, Imgproc.COLOR_BGR2HSV);
            split(hsv, hsvSplit);
            equalizeHist(hsvSplit.get(2), hsvSplit.get(2));
            merge(hsvSplit, hsv);
            cvtColor(hsv, out, COLOR_HSV2BGR);
        }else if(in.channels()==1){
            equalizeHist(in, out);
        }


        return out;

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static ArrayList<Mat> segment(Mat input)
    {
        ArrayList<Mat> output = new ArrayList<>();
        ArrayList<Rect> rectArrayList = new ArrayList<>();

        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////

        // set the channels of the image to 3 (from 4) // methods below accepts only 3 channels images //
        cvtColor(input,input,COLOR_BGRA2RGB);
        //ApplyFilter(input,input,3);
        /*Imgproc.dilate(input, input, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 3)));
        image = input;*/
//        return null;
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////

        //apply a Gaussian blur of 5 x 5 and remove noise
        Mat img_gray = new Mat();
        cvtColor(input, img_gray,COLOR_RGB2GRAY);

        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////

        Mat img_blur = new Mat();
        //blur(img_gray, img_gray, new Size(5,5));
        Imgproc.bilateralFilter(img_gray, img_blur, 50, 17, 17,Imgproc.BORDER_DEFAULT);
        image = img_gray;
        //Imgproc.GaussianBlur(img_gray, img_gray, new Size(5,5),0);
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////////////////

        //Find vertical edges. Car plates have high density of vertical lines
        Mat img_sobel = new Mat();
        Mat img_sobel_two = new Mat();
        //Sobel(img_blur, img_sobel, CvType.CV_8U, 1, 2, 3, 12, 0,BORDER_DEFAULT); //xorder=1,yorder=0,kernelsize=3
        //Imgproc.Canny(img_blur,img_sobel,40,320);
        Imgproc.Canny(img_blur,img_sobel,40,320);
        Imgproc.Canny(img_blur,img_sobel_two,40,150);
        image = img_sobel;
        //image = img_sobel_two;
        //return null;

        Imgproc.dilate(img_sobel, img_sobel, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 3)));
        Imgproc.erode(img_sobel, img_sobel, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 3)));

        Mat element = getStructuringElement(MORPH_RECT,new Size(17,3));
        Imgproc.morphologyEx(img_sobel, img_sobel,Imgproc.MORPH_CLOSE,element);



        Imgproc.dilate(img_sobel_two, img_sobel_two, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 3)));
        Imgproc.erode(img_sobel_two, img_sobel_two, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 3)));


        image = img_sobel_two;
        //return null;

       /////////////////////////////////////////////////////////////////////////////////////////////
       /////////////////////////////////////////////////////////////////////////////////////////////
       /////////////////////////////////////////////////////////////////////////////////////////////
       /////////////////////////////////////////////////////////////////////////////////////////////

        // find the center of the image
        double[] centers = {(double)img_sobel.width()/2, (double)img_sobel.height()/2};
        Point image_center = new Point(centers);

        // find the center of the image
        double[] centers_two = {(double)img_sobel_two.width()/2, (double)img_sobel_two.height()/2};
        Point image_center_two = new Point(centers_two);

        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////

        //Find contours of possibles plates

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        List<MatOfPoint> contours_two = new ArrayList<MatOfPoint>();

        findContours(img_sobel, contours,new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE); // all pixels of each contours
        findContours(img_sobel_two, contours_two,new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE); // all pixels of each contours

        //image = img_threshold;

        int m = contours.size();

        ListIterator<MatOfPoint> iterator = contours.listIterator();
        ListIterator<MatOfPoint> iterator_two = contours_two.listIterator();
        while (iterator.hasNext())
        {
            MatOfPoint contour = iterator.next();
            double area = Imgproc.contourArea(contour);

            if(area < 3000 )//|| !verify_two(mr))
            {
               iterator.remove();
            }
        }

        while (iterator_two.hasNext())
        {
            MatOfPoint contour_two = iterator_two.next();
            double area_two = Imgproc.contourArea(contour_two);

            if(area_two < 3000 )//|| !verify_two(mr))
            {
                iterator_two.remove();
            }
        }

        int a = contours.size();

        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////

        //Start to iterate to each contour founded
        ListIterator<MatOfPoint> itc = contours.listIterator();
        ListIterator<MatOfPoint> itc_two = contours_two.listIterator();

        while(itc_two.hasPrevious())
        {itc_two = (ListIterator<MatOfPoint>) itc_two.previous();}


        while(itc.hasPrevious())
        {itc = (ListIterator<MatOfPoint>) itc.previous();}

        ArrayList<RotatedRect> rects = new  ArrayList<RotatedRect>();

        //Remove patch that are no inside limits of aspect ratio and area.

        while (itc.hasNext())
        {
            //Create bounding rect of object
            MatOfPoint countour = itc.next();

            MatOfPoint2f mp2f = new MatOfPoint2f(countour.toArray());
            MatOfPoint2f mp2fb = new MatOfPoint2f();
            RotatedRect mr = minAreaRect(mp2f);

            double peri = Imgproc.arcLength(mp2f, true);
            Imgproc.approxPolyDP(mp2f, mp2fb, 0.02 * peri,true);
            if(verify_one(mr) || verify_two(mr) || (mp2fb.toList().size()== 4))
            {
                if(!rects.contains(mr))
                rects.add(mr);
            }
            else
            {
                itc.remove();
            }
        }

        int k = rects.size();

        while (itc_two.hasNext())
        {
            //Create bounding rect of object
            MatOfPoint countour = itc_two.next();

            MatOfPoint2f mp2f = new MatOfPoint2f(countour.toArray());
            MatOfPoint2f mp2fb = new MatOfPoint2f();
            RotatedRect mr = minAreaRect(mp2f);

            double peri = Imgproc.arcLength(mp2f, true);
            Imgproc.approxPolyDP(mp2f, mp2fb, 0.02 * peri,true);
            if(verify_one(mr) || verify_two(mr) || (mp2fb.toList().size()== 4))
            {
                if(!rects.contains(mr))
                    rects.add(mr);
            }
            else
            {
                itc_two.remove();
            }
        }

        int b = rects.size();

        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////


        Mat result = new Mat();
        input.copyTo(result);
         /*int ji = contours_two.size();

         double f = contours.get(0).size().area();Mat mat =new Mat();
         double ll = contours_two.get(0).size().area();

        List<MatOfPoint> contours_ = new ArrayList<MatOfPoint>();
        contours_.add(contours.get(0));*/

        drawContours(result, contours,-1,new Scalar(255,0,0),3);
        drawContours(result,contours_two,-1,new Scalar(255,0,0),3);
        image = result;

        //return null;
////////////////////////////////////////////////////////////////////////////////////////////////////

      // slicing the image for result region
        for (int i = 0; i < rects.size(); i++)
        {
            RotatedRect rect_min = rects.get(i);
            //Get rotation matrix
            float r = (float)rect_min.size.width / (float)rect_min.size.height;
            float angle = (float) rect_min.angle;
            if (r < 1)
            {
                angle = 90 + angle;
            }
            Mat rotmat = getRotationMatrix2D(rect_min.center, angle,1);

            //Create and rotate image
            Mat img_rotated = new Mat();
            warpAffine(input, img_rotated, rotmat, input.size(), Imgproc.INTER_CUBIC);

            //Crop image
            Size rect_size = rect_min.size;
            double hatba;
            if (r < 1)
            {
                hatba  = rect_size.width;
                rect_size.width  = rect_size.height;
                rect_size.height = hatba;
             }
            Mat img_crop = new Mat();
            getRectSubPix(img_rotated, rect_size, rect_min.center, img_crop);
            //image = img_crop;

            Mat resultResized = new Mat();
            resultResized.create(33,144, CV_8UC3);
            resize(img_crop, resultResized, resultResized.size(), 0, 0, Imgproc.INTER_CUBIC);
            //Equalize croped image
            Mat grayResult = new Mat();
            cvtColor(resultResized, grayResult, COLOR_BGR2GRAY);
            //image = grayResult;
            blur(grayResult, grayResult,new  Size(3,3));
            //equalizeHist(grayResult,grayResult);
            grayResult = histeq(grayResult);
            image = grayResult;
            output.add(grayResult);
        }
            out = output;
            return out;

////////////////////////////////////////////////////////////////////////////////////////////////////

    }
}