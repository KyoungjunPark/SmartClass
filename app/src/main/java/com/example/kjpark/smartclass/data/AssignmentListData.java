package com.example.kjpark.smartclass.data;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by parkk on 2015-11-17.
 */
public class AssignmentListData {

    public Drawable mIcon;

    public String mTitle;

    public String mDate;

    //alphabetical order
    public static final Comparator<AssignmentListData> ALPHA_COMPARATOR = new Comparator<AssignmentListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AssignmentListData mListDate_1, AssignmentListData mListDate_2) {
            return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
        }
    };
}
