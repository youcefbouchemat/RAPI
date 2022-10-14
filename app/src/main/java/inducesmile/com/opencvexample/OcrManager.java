package inducesmile.com.opencvexample;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;


/**
 * Created by vu.tien.tung on 4/24/17.
 */

public class OcrManager  {

    TessBaseAPI baseAPI = null;
    public void initAPI()
    {
        baseAPI = new TessBaseAPI();
        // after copy, my path to trainned data is getExternalFilesDir(null)+"/tessdata/"+"eng.traineddata";
        // but init() function just need parent folder path of "tessdata", so it is getExternalFilesDir(null)
        String dataPath = MainApplication.instance.getTessDataParentDirectory();
        baseAPI.init(dataPath,"eng");
        baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        baseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!?@#$%&*()<>_-+=/:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        baseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789");
        baseAPI.setVariable("classify_bln_numeric_mode", "1");

        // language code is name of trainned data file, except extendsion part
        // "vie.traineddata" => language code is "vie"

        // first param is datapath which is  part to the your trainned data, second is language code
        // now, your trainned data stored in assets folder, we need to copy it to another external storage folder.
        // It is better do this work when application start firt time
    }

    public String startRecognize(Bitmap bitmap)
    {
        if(baseAPI ==null)
            initAPI();
        baseAPI.setImage(bitmap);
        return baseAPI.getUTF8Text();
    }
}




