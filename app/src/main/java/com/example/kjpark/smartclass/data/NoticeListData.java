package com.example.kjpark.smartclass.data;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by parkk on 2015-11-17.
 */
public class NoticeListData {

    public Boolean isImportant;
    public String mTitle;
    public String mDate;
    public String mContent;
    public Boolean isSignNeed;

    public NoticeListData(){}
    public NoticeListData(String mTitle, String mDate, String mContent, Boolean isSignNeed,Boolean isImportant)
    {
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mContent = mContent;
        this.isSignNeed = isSignNeed;

        this.isImportant = isImportant;
    }
    //alphabetical order
    public static final Comparator<NoticeListData> ALPHA_COMPARATOR = new Comparator<NoticeListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(NoticeListData mListDate_1, NoticeListData mListDate_2) {
            return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
        }
    };
}
