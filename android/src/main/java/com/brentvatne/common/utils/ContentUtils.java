package com.brentvatne.common.utils;

import com.brentvatne.common.interfaces.AdObject;

import java.util.ArrayList;

public class ContentUtils {
    private ArrayList<AdObject> adsBreakPoints;

    public ContentUtils(ArrayList<AdObject> adsBP) {
        adsBreakPoints = adsBP;
    }

    public long getContentTime(long time) {

        long contentTime = time;
        if (adsBreakPoints != null) {
            for (int i = 0; i < adsBreakPoints.size(); i++) {
                AdObject adObject = adsBreakPoints.get(i);
                final long adTime = adObject.adDuration * 1000;
                final long startTime = adObject.adStart * 1000;
                final long endTime = adObject.adEnd * 1000;

                if (endTime >= time && startTime <= time) {
                    contentTime = startTime;
                }

                if (startTime <= time) {
                    contentTime = contentTime - adTime;
                }
            }
        }

        return contentTime > 0 ? contentTime : 0;
    }

    public long getStreamTime(long time) {
        long contentTime = time;
        if (adsBreakPoints != null) {
            for (int i = 0; i < adsBreakPoints.size(); i++) {
                AdObject adObject = adsBreakPoints.get(i);
                final long adTime = adObject.adDuration * 1000;
                final long startTime = adObject.adStart * 1000;
                if (startTime <= contentTime) {
                    contentTime = contentTime + adTime + 1000;
                }
            }
        }
        return contentTime;
    }

}
