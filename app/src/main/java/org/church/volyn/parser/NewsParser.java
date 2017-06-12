package org.church.volyn.parser;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 03.12.2014.
 */
public class NewsParser {
    public static String getNewsTextWithTags(String source){
        StringBuilder sb = new StringBuilder();
        String[] s1 = source.split("post-meta");
        String[] s2 = s1[1].split("<p", 2);
        String[] s3 = s2[1].split("<div class=\"sharedaddy sd-sharing-enabled");
        sb.append("<p").append(s3[0]);
        return sb.toString();
    }

    public static String getImageUrl(String response) {
        String imageUrl = "";

        String[] s = response.split("<div data-carousel-extra=");
        Pattern pattern = Pattern.compile("<a.*<img.*</a>");
        Matcher matcher = pattern.matcher(s[0]);
        if (matcher.find()) {
            String imgTag = matcher.group();
            imageUrl = imgTag.split("\"", 3)[1];
            return imageUrl;
        }
        if (s.length > 1) {
            Pattern galleryImagePattern = Pattern.compile("data-orig-file=\".+?\"");
            matcher = galleryImagePattern.matcher(s[1]);

            if (matcher.find()) {
                String imgTag = matcher.group();
//                imageUrl = imgTag.split("\"", 3)[1];
                return imageUrl;
            }

        }
        return imageUrl;
    }

public static void logd(String TAG, String veryLongString){
    int maxLogSize = 1000;
//    TAG = "Source";
    for(int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
        int start = i * maxLogSize;
        int end = (i+1) * maxLogSize;
        end = end > veryLongString.length() ? veryLongString.length() : end;
    }

}
}
