package org.church.volyn.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by user on 07.11.2015.
 */
public class ResourcesUtils {
    public static String getStringByName(Context context, String name) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "string",
                context.getPackageName());
        return resources.getString(resourceId);

    }
}
