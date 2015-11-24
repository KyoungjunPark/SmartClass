package com.example.kjpark.smartclass.data;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by parkk on 2015-11-17.
 */
public class SettingListData {

    public String mTitle;
    public String mInfo;
    public Boolean mIsConcentrationMode;

    //alphabetical order
    public static final Comparator<SettingListData> ALPHA_COMPARATOR = new Comparator<SettingListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(SettingListData mListDate_1, SettingListData mListDate_2) {
            return sCollator.compare(mListDate_1.mTitle, mListDate_2.mTitle);
        }
    };
}
