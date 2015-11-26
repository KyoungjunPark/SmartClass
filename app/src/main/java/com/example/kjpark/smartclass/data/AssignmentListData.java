package com.example.kjpark.smartclass.data;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by parkk on 2015-11-17.
 */
public class AssignmentListData {

    public String mTitle;
    public String mContent;
    public String mStart_date;
    public String mEnd_date;
    public Boolean isImportant;

    public AssignmentListData(){}
    public AssignmentListData(String mTitle, String mContent, String mStart_date, String mEnd_date, Boolean isImportant)
    {
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.mStart_date = mStart_date;
        this.mEnd_date = mEnd_date;
        this.isImportant = isImportant;
    }
    //alphabetical order
    public static final Comparator<AssignmentListData> ALPHA_COMPARATOR = new Comparator<AssignmentListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AssignmentListData mListDate_1, AssignmentListData mListDate_2) {
            return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
        }
    };
}
