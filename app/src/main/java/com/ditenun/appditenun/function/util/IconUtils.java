    package com.ditenun.appditenun.function.util;

import android.content.Context;

import com.ditenun.appditenun.R;
import com.joanzapata.iconify.Icon;
import com.joanzapata.iconify.IconDrawable;

    /**
     * Using this file must on behalf of Institut Teknologi Del & Piksel
     */

    public class IconUtils {
    //    public static IconDrawable getIconDrawable(Context context, Icon iconUpload) {
    //        return new IconDrawable(context, iconUpload)
    //                .colorRes(R.color.colorIcon)
    //                .actionBarSize();
    //    }

        public static IconDrawable getIconDrawable(Context context, Icon iconName) {
            return new IconDrawable(context, iconName)
                    .colorRes(R.color.colorIcon)
                    .actionBarSize();
        }

        public static IconDrawable getIconDrawable(Context context, Icon iconName, int sizeDp) {
            return new IconDrawable(context, iconName)
                    .colorRes(R.color.colorIcon)
                    .sizeDp(sizeDp);
        }

        public static IconDrawable getIconDrawableWithColor(Context context, Icon iconName, int colorRes) {
            return new IconDrawable(context, iconName)
                    .color(colorRes)
                    .actionBarSize();
        }
    }
