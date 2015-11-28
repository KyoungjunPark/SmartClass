package com.example.kjpark.smartclass.data;

import android.graphics.Bitmap;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by parkk on 2015-11-17.
 */
public class SignListData {

    public String mName;
    public int mIsSigned;
    public Bitmap mSignImage;

    public SignListData(){}
    public SignListData(String mName, int mIsSigned, Bitmap mSignImage)
    {
        this.mName = mName;
        this.mIsSigned = mIsSigned;
        this.mSignImage = mSignImage;
    }
    //alphabetical order
    public static final Comparator<SignListData> ALPHA_COMPARATOR = new Comparator<SignListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(SignListData mListDate_1, SignListData mListDate_2) {
            return sCollator.compare(mListDate_1.mName, mListDate_2.mName);
        }
    };
}
