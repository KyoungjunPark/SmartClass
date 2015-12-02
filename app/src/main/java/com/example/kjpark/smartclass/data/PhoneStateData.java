package com.example.kjpark.smartclass.data;

import android.graphics.Bitmap;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by parkk on 2015-11-17.
 */
public class PhoneStateData {

    public String email;
    public String status;

    public PhoneStateData(){}
    public PhoneStateData(String email, String status)
    {
        this.email = email;
        this.status = status;
    }
    //alphabetical order
    public static final Comparator<PhoneStateData> ALPHA_COMPARATOR = new Comparator<PhoneStateData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(PhoneStateData mListDate_1, PhoneStateData mListDate_2) {
            return sCollator.compare(mListDate_1.email, mListDate_2.email);
        }
    };
}
