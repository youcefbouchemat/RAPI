package inducesmile.com.opencvexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.ml.CvSVM;
import org.opencv.ml.Ml;

import java.io.File;
import java.util.ArrayList;

import static org.opencv.core.CvType.CV_32FC1;
import static org.opencv.core.TermCriteria.MAX_ITER;
import static org.opencv.highgui.Highgui.imread;
import static org.opencv.imgproc.Imgproc.COLOR_BGRA2BGR;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.ml.CvSVM.C_SVC;

public class Classification
{
    protected static int num_files = 100; // 100 pictures
    protected static int img_area = 144*33;// size of the image
    protected static Mat training_mat= new Mat(num_files, img_area, CV_32FC1);
    private static Mat labels =  new Mat(Classification.num_files,1, CV_32FC1);


    public static ArrayList<Plate> Train(ArrayList<Plate> possible_regions)
    {
        ArrayList<Plate> plates = new ArrayList<>();

        // The first loop is for the folder that contains plate images //
        for (int file_num=0; file_num<OpenCVCamera.xml.size();file_num++) // xml is an array list that contains our images in a bitmap format //
        {
            Mat img_mat = new Mat();
            Utils.bitmapToMat(OpenCVCamera.xml.get(file_num),img_mat);
            cvtColor(img_mat,img_mat,COLOR_BGRA2BGR); //set the channels of the image to 3 (from 4)
           // img_mat = reshapeMat(img_mat);
            int ii = 0; // Current column in training_mat

            for (int i = 0; i < img_mat.rows(); i++)
            {
                for (int j = 0; j < img_mat.cols(); j++)
                {
                    training_mat.put(file_num,ii,img_mat.get(i,j));
                    ii++;
                }
            }

            labels.put(file_num,1,CV_32FC1);
        }

       // The second loop is for the folder that do not contains plate images //
        for (int file_num=0; file_num<OpenCVCamera.xml_no.size();file_num++)
        {
            Mat img_mat_no = new Mat();
            Utils.bitmapToMat(OpenCVCamera.xml_no.get(file_num),img_mat_no);
            cvtColor(img_mat_no,img_mat_no,COLOR_BGRA2BGR); //set the channels of the image to 3 (from 4)
            //img_mat_no = reshapeMat(img_mat_no);
            int ii = 0; // Current column in training_mat

            for (int i = 0; i < img_mat_no.rows(); i++)
            {
                for (int j = 0; j < img_mat_no.cols(); j++)
                {
                    training_mat.put(file_num,ii,img_mat_no.get(i,j));
                    ii++;
                }
            }

            labels.put(file_num,0,CV_32FC1);
        }

        //Set SVM params
        CvSVM svm = new CvSVM();
        org.opencv.ml.CvSVMParams svmParams = new org.opencv.ml.CvSVMParams();
        svmParams.set_svm_type(C_SVC);
        svmParams.set_kernel_type(CvSVM.LINEAR);
      /*  svmParams.set_degree(0);
        svmParams.set_gamma(1);
        svmParams.set_coef0(0);
        svmParams.set_C(1);
        svmParams.set_nu(0);
        svmParams.set_p(0);
        TermCriteria termCriteria = new TermCriteria(TermCriteria.MAX_ITER, 1000, 0.01); //http://answers.opencv.org/question/6052/android-java-versions-of-cv_termcrit_eps-cv_termcrit_iter/
        svmParams.set_term_crit(termCriteria);
     */
        //train our classifier
        svm.train(training_mat,labels,new Mat(),new Mat(),svmParams);
        File datasetFile = new File(Environment.getExternalStorageDirectory(), "dataset.xml");
        svm.save(datasetFile.getAbsolutePath());


        Size a = labels.size();
        String m = a.toString();

        //The following code is a part of main application, that is called online processing
        //For each possible plate, classify with svm if it's a plate or no

        for(int i=0; i< possible_regions.size(); i++)
        {
            Mat img=possible_regions.get(i).plateImg;
            //Mat p= img.reshape(1, 1);//convert img to 1 row m features
            Mat p = reshapeMat(img);
            p.convertTo(p, CV_32FC1);
            int response = (int)svm.predict(p);
            if(response == 1) {plates.add(possible_regions.get(i));}
        }
        return plates;
    }

    public static Mat reshapeMat(Mat input)
    {
        int outputIndex=0;
        Mat output=new Mat(1,144*33, CV_32FC1);
        for (int i = 0; i<input.rows(); i++) {
            for (int j = 0; j < input.cols(); j++) {
                output.put(0,outputIndex,input.get(i,j));
                outputIndex++;
            }
        }
        return  output;
    }

}


