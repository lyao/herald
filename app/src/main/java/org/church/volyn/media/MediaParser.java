package org.church.volyn.media;

import org.church.volyn.App;
import org.church.volyn.utils.ResourcesUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 23.09.2015.
 */
public class MediaParser {

    public static final String ID = "id";
    public static final String STRING_ID = "stringId";
    public static final String TYPE = "t";
    public static final String MEDIAS = "medias";
    public static final String CAT_ID = "catId";
    public static final String YEAR = "y";
    public static final String MONTH = "m";
    public static final String DAY = "d";
    public static final String YOUTUBE_ID = "yid";

    public static void parse(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("categories");
            MediaManager.init(jsonArray.length());

            JSONObject jsonMediaContainer;

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonMediaContainer = jsonArray.getJSONObject(i);
                MediaContainer mc = new MediaContainer(jsonMediaContainer.getInt(ID) - 1, jsonMediaContainer.getString(STRING_ID));
                String strId = jsonMediaContainer.getString(STRING_ID);
                mc.setDrawableId(strId);
                mc.setTitle(ResourcesUtils.getStringByName(App.getContext(), strId));
                mc.setType(jsonMediaContainer.optInt(TYPE, MediaContainer.TYPE_AUDIO));
                MediaManager.getInstance().addMediaContainer(mc);
            }

            JSONArray jsonMedias = jsonObject.getJSONArray(MEDIAS);
            JSONObject jsonMedia;
            for (int i = 0; i < jsonMedias.length(); i++) {
                jsonMedia = jsonMedias.getJSONObject(i);
                MediaManager.getInstance().addMedia(createMediaFromJson(jsonMedia));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static MediaElement createMediaFromJson(JSONObject jsonObject) {
        MediaElement me = new MediaElement();
        try {
            int catId = jsonObject.getInt(CAT_ID);
            Date date = getDate(jsonObject.getInt(YEAR), jsonObject.getInt(MONTH), jsonObject.getInt(DAY));
            me.setCatId(catId);
            me.setYid(jsonObject.getString(YOUTUBE_ID));
            me.setDate(date);
            me.setTitle(MediaManager.getInstance().getMediaContainers().get(catId - 1).getTitle() + "\n" + date.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return me;
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
