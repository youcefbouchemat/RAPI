package inducesmile.com.opencvexample;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import org.opencv.core.Point;
import org.opencv.core.Rect;

import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;


import static java.lang.StrictMath.max;
import static org.opencv.core.Core.addWeighted;
import static org.opencv.core.Core.countNonZero;
import static org.opencv.core.Core.rectangle;
import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_8UC1;

import static org.opencv.core.Mat.eye;
import static org.opencv.imgproc.Imgproc.BORDER_CONSTANT;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.drawContours;
import static org.opencv.imgproc.Imgproc.findContours;


import static org.opencv.imgproc.Imgproc.resize;
import static org.opencv.imgproc.Imgproc.threshold;
import static org.opencv.imgproc.Imgproc.warpAffine;

class CharSegment
{
    private static List<MatOfPoint> Contours;

    //
    static ArrayList<Mat> OcrSegmentation(Mat input) {
        ArrayList<Mat> output=new ArrayList<>();

        Appfil(input,input,2);

        Mat img_threshold = new Mat();
        threshold(input, img_threshold, 120, 500,THRESH_BINARY_INV);

        Mat img_contours =copy(img_threshold);



//Find contours of possibles characters
        Contours=new ArrayList<>();


        findContours(img_contours, Contours,new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE); // all pixels of each contours
        Contours=sort(Contours);


        Contours=Cut(img_threshold);

// Draw blue contours on a white image
        Mat result=copy(img_threshold);
        cvtColor(result, result, COLOR_GRAY2BGR);
        drawContours(result,Contours,
                -1, // draw all contours
                new Scalar(0,0,255), // in blue
                1); // with a thickness of 1




//Start to iterate to each contour founded

//Remove patch that are no inside limits of aspect ratio and area.
        for (MatOfPoint Contour : Contours) {
//Create bounding rect of object
            MatOfPoint mp = new MatOfPoint(Contour.toArray());
            Rect mr = boundingRect(mp);
            rectangle(result, new Point(mr.x, mr.y), new Point(mr.x + mr.width, mr.y + mr.height), new Scalar(255, 0, 0));

            Mat auxRoi = new Mat(img_threshold, mr);

            if (OCR_verifySizes(auxRoi)) {
                output.add(auxRoi);
            }
        }
        return output;
    }


    private static Boolean OCR_verifySizes(Mat input)
    {
//Char sizes
//31x73
        float aspect=31.0f/73.0f;
        float minAspect=0.1f; //ratio minimum des caractères
        float error=0.6f;// pourcentage d'erreur
        float maxAspect=aspect+aspect*error; //ratio maximum des caractères
        float charAspect=(float)input.cols()/(float)input.rows(); // ratio de caractère

        float minHeight=2.0f; // la longueur minimale des caractères
        float maxHeight=14.0f; // la longueur maximale des caractères

        float area=(float)countNonZero(input); // nombre de pixel blanc

        float bbArea=input.cols()*input.rows(); // nombre de pixel

        float percPixels=area/bbArea; // pourcentage de pixel blanc dans l'image


        return percPixels < 0.9 && charAspect >= minAspect && charAspect <= maxAspect && input.cols() >= minHeight && input.cols() <= maxHeight;
    }


    private static Mat copy(Mat input)
    {
        Mat output=new Mat(input.rows(),input.cols(),CV_8UC1);

        for (int i = 0; i < input.rows(); i++) {
            for (int j = 0; j < input.cols(); j++) {
                output.put(i,j,input.get(i,j));
            }
        }

        return output;
    }


    private static List<MatOfPoint> Cut (Mat img_threshold)
    {
        List<MatOfPoint> output=new ArrayList<>();
        ListIterator<MatOfPoint> itc = Contours.listIterator();


        int Index=0;
//Remove patch that are no inside limits of aspect ratio and area.
        while (itc.hasNext())
        {
//Create bounding rect of object
            MatOfPoint mp = new MatOfPoint(itc.next().toArray());
            Rect mr = boundingRect(mp);

            Mat auxRoi=new Mat(img_threshold,mr);

            int x1=mr.x;
            int x2=x1+mr.width;

            if(!((x1<=3 || x2>=142) && OCR_verifySizes(auxRoi)))
            {
                output.add(Contours.get(Index));
            }
            Index++;
        }
        return output;
    }


    private static List<MatOfPoint> sort(List<MatOfPoint> contours)
    {
        ArrayList<Rect> mr=new ArrayList<>();
        ListIterator<MatOfPoint> itc = contours.listIterator();
        while (itc.hasNext())
        {
            MatOfPoint mp = new MatOfPoint(itc.next().toArray());
            mr.add(boundingRect(mp));
        }


        for (int i = 0; i < mr.size(); i++)
        {
            for (int j = i+1; j <mr.size() ; j++)
            {
                if(mr.get(i).x>mr.get(j).x)
                {
                    Collections.swap(mr, i, j);
                    Collections.swap(contours, i, j);
                }
            }
        }

        return contours;
    }

    static Mat preprocessChar(Mat in,int plus){
//Remap image
        int h=in.rows();
        int w=in.cols();
        Mat transformMat=eye(2,3,CV_32F);

        int m=max(w,h);
        transformMat.put(0,2,m/2 - w/2);
        transformMat.put(1,2,m/2 - h/2);


        Mat warpImage=new Mat(m+plus,m+plus, in.type());
        warpAffine(in, warpImage, transformMat, warpImage.size(), INTER_LINEAR, BORDER_CONSTANT,new Scalar(0) );


        Mat out=new Mat();
        resize(warpImage, out,new Size(20,20) );


        return out;
    }

    static void Appfil (Mat in,Mat out,double alpha)
    {
        Mat black=new Mat(in.rows(),in.cols(),in.type(),new Scalar(0,0));
        double beta=(1.0-alpha);
        addWeighted(in,alpha,black,beta,0.0,out);
    }

}