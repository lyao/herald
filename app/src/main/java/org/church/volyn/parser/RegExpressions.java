package org.church.volyn.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 02.03.2015.
 */
public class RegExpressions {
    public static final String FRONT_IMAGE_REPLACE_A_IMG = "<a.*<img class.*?\\/a>";
    public static final String FRONT_IMAGE_URL_IMG = "(?i)<img\\s+[^>]*src=\"([^\"]*)\"[^>]*>";
//    public static final String FRONT_IMAGE_URL_A_IMG = "(?is)<a.*?\\shref=\\\"(.*?)\\\">.?img.*?<\\/a>";
    public static final String FRONT_IMAGE_URL_A_IMG = "<a\\s*href=\"(.*)\">\\s{0,}.?img.*?<\\/a>";


    public static final String TAG_A_TEXT = "(?i)<a.*?>(.+?)<\\/a>";
    public static final String TAG_A = "(?i)(<a.*?>).+?(<\\/a>)";
//    public static final String FRONT_IMAGE_FROM_GALLERY = "(?i)<img\\s+[^>]*data-orig-file=\"([^\"]*)\"[^>]*>";
    public static final String FRONT_IMAGE_FROM_GALLERY = "(?i)<a.*<img\\s+[^>]*data-orig-file=\"([^\"]*)\"[^>]*>.*a>";
    public static final String YOUTUBE_REPLACE_SPAN_TAG = "<p.*<span.*class\\s*=\\s*[\"'].*embed-youtube.*[\"']\\s*>(.*)<\\/span><\\/p>";


    private static String matchImageUrl(String body, String regEx) {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(body);
        String imageUrl = null;
        if (matcher.find()) {
            String imgTag = matcher.group();
            imageUrl = imgTag.split("\"", 3)[1];
        }
        return imageUrl;
    }

}
