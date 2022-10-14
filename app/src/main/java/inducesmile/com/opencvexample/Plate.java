package inducesmile.com.opencvexample;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;

public class Plate
{
    public static Mat getPlateImg() {
        return plateImg;
    }

    protected static Mat plateImg;
    protected static Rect position;
    protected static ArrayList<Character> chars;
    protected static ArrayList<Rect> charsPos;


    public Plate()
    { }


    public Plate(Mat img, Rect pos)
    {
        plateImg = img;
        position = pos;
    }

   /* public String str()
    {
        String result = "";
        //Order numbers
        ArrayList<Integer> orderIndex = new ArrayList<Integer>();
        ArrayList<Integer> xpositions = new ArrayList<Integer>();
        for (int i = 0; i < charsPos.size(); i++)
        {
            orderIndex.add(i);
            xpositions.add(charsPos.get(i).x);
        }
        float min = xpositions.get(0);
        int minIdx = 0;
        for (int i = 0; i < xpositions.size(); i++)
        {
            min = xpositions.get(i);
            minIdx = i;
            for (int j = i; j < xpositions.size(); j++)
            {
                if (xpositions.get(j) < min)
                {
                    min = xpositions.get(j);
                    minIdx = j;
                }
            }
            int aux_i = orderIndex.get(i);
            int aux_min = orderIndex.get(minIdx);
            orderIndex.set(i, aux_min);
            orderIndex.set(minIdx, aux_i);

            float aux_xi = xpositions.get(i);
            float aux_xmin = xpositions.get(minIdx);
            xpositions.set(i, (int) aux_xmin);
            xpositions.set(minIdx, (int) aux_xi);
        }
        for (int i = 0; i < orderIndex.size(); i++)
        {
            result = result + chars.get(orderIndex.get(i));
        }
        return result;
    }*/
}
