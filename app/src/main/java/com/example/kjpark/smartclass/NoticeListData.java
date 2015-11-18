package com.example.kjpark.smartclass;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by parkk on 2015-11-17.
 */
public class NoticeListData {

    public Drawable mIcon;
    public String mTitle;
    public String mDate;
    public Drawable mSign;

    //alphabetical order
    public static final Comparator<NoticeListData> ALPHA_COMPARATOR = new Comparator<NoticeListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(NoticeListData mListDate_1, NoticeListData mListDate_2) {
            return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
        }
    };
}
