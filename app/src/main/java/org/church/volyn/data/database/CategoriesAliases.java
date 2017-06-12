package org.church.volyn.data.database;

import java.util.HashMap;

import org.church.volyn.App;
import org.church.volyn.R;

public class CategoriesAliases {
    static HashMap<String, String> catAliases = new HashMap<String, String>();
    static {
        String[] categories = App.getContext().getResources().getStringArray(R.array.categories);
        for (String cat:  categories) {
            catAliases.put(cat.split(":")[0], cat.split(":")[1]);
        }
    }

    public static String getAlias(String catName) {
        return catAliases.get(catName);
    }

}
