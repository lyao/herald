package org.church.volyn.parser;

import android.content.Context;
import android.util.Xml;

import org.church.volyn.App;
import org.church.volyn.Constants;
import org.church.volyn.data.database.DataManager;
import org.church.volyn.downloadHelper.ImageCache;
import org.church.volyn.downloadHelper.ImageManager;
import org.church.volyn.downloadHelper.NewsManager;
import org.church.volyn.entities.Category;
import org.church.volyn.entities.NewsItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 18.11.2014.
 */
public class XmlPullNewsParser {

    private static final String ns = null;
    public static final String CHANNEL = "channel";
    public static final String ITEM = "item";
    public static final String LINK = "link";
    public static final String PUB_DATE = "pubDate";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String CONTENT_ENCODED = "content:encoded";
    public static final String CATEGORY = "category";
    public static final String GUID = "guid";
    private static Context sContext;
    public static boolean isWorking = false;

//    public static void parseFeedItem(Context context, InputStream in) throws Exception {
   public static void parseFeedItem(Context context, String in) throws Exception {
        sContext = context;
        XmlPullNewsParser.readChannel(in);
    }


//    private static void readChannel(InputStream in) throws XmlPullParserException, IOException {
   private static void readChannel(String in) throws XmlPullParserException, IOException {
        isWorking = true;
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//        parser.setInput(in, null);
       parser.setInput(new StringReader(in));
        skipToTag(parser, CHANNEL);
        parser.require(XmlPullParser.START_TAG, ns, CHANNEL);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(ITEM)) {
                NewsItem newsItem = readNewsItem(parser);
                if (newsItem.getCategory() != null) {
                    NewsManager.getInstance().addNewsItemToCache(newsItem);
                    DataManager.getInstance().saveNews(newsItem);
                    DataManager.getInstance().saveGallery(newsItem);
                    if (newsItem.getImageUrl() != null && !ImageCache.getInstance(App.getContext()).isBitmapSaved(newsItem.getImageUrl())) {
                        ImageManager.startDownload(newsItem);
                    }
                }
            } else {
                skip(parser);
            }
        }
       DataManager.getInstance().prepareNewsForCleaning();
       isWorking = false;
    }

    private static NewsItem readNewsItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ITEM);
        String title = null;
        String description = null;
        String link = null;
        NewsItem newsItem = new NewsItem();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals(TITLE)) {
                title = readTitle(parser);
                newsItem.setTitle(title);
            } else if (name.equals(LINK)) {
                newsItem.setNewsLink(readLink(parser));
            } else if (name.equals(PUB_DATE)) {
                newsItem.setPubDate(readPubDate(parser));
            } else if (name.equals(CONTENT_ENCODED)) {
                newsItem.setRawNewsContent(readContent(parser));

            } else if (name.equals(CATEGORY)) {
                String categoryName = readCategory(parser);
                if (Constants.VALIDATION_FOR_CATEGORIES.indexOf(categoryName.trim()) >= 0) {
                    Category cat = DataManager.getInstance().findCategoryByName(categoryName);
                    newsItem.setCategory(cat);
                } else {
                    continue;
                }
            } else if (name.equals(GUID)) {
                newsItem.setGuid(readGuid(parser));
            } else {
                skip(parser);
            }
        }
        newsItem.setImageUrl(retrieveImageUrl(newsItem.getRawNewsContent()));
        if (newsItem.getRawNewsContent().contains("youtube-player")) {
            newsItem.setVideoUrl(retrieveVideoUrl(newsItem.getRawNewsContent()));
        } else {
            newsItem.setVideoUrl("");
        }
        newsItem.setNewsContent(new HtmlCompounder(sContext, newsItem).compound());
        return newsItem;
    }

    private static void skipToTag(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException {
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT
                && !tagName.equals(parser.getName())) {
            event = parser.next();
        }
    }
    private static String retrieveVideoUrl(String body) {
        Document doc;
        doc = Jsoup.parse(body);
        Elements elements = doc.select("iframe.youtube-player");
        if (elements.size() > 0) {
            try {
                return doc.select("iframe.youtube-player").get(0).attr("src").split("embed/")[1].split("\\?version")[0];
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    private static String retrieveImageUrl(String body) {

        String galleryHtml = "";
        String splited[] = body.split(RegExpressions.FRONT_IMAGE_FROM_GALLERY, 2);
        if (splited.length > 1) {
            body = splited[0];
            galleryHtml = splited[1];
        }

        String imgUrl = retrieveImageUrlByPattern(body, RegExpressions.FRONT_IMAGE_URL_A_IMG);
        if (imgUrl != null) {
            splited = imgUrl.split("\"");
            if (splited.length > 1) return splited[0];
            return imgUrl;
        }

        imgUrl = retrieveImageUrlByPattern(body, RegExpressions.FRONT_IMAGE_URL_IMG);
        if (imgUrl != null) {
            return imgUrl;
        }
        return retrieveImageUrlByPattern(galleryHtml, RegExpressions.FRONT_IMAGE_FROM_GALLERY);
    }

    private static String retrieveImageUrlByPattern(String body, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(body);
        String imageUrl = null;
        if (matcher.find()) {
            imageUrl = matcher.group(1);
        }
        return (isImageUrl(imageUrl)?imageUrl:null);
    }

    private static boolean isImageUrl(String imgUrl) {
        if (imgUrl == null) return false;
        if ((imgUrl.indexOf(".jpg") > 0) ||
                (imgUrl.indexOf(".jpeg") > 0) ||
                (imgUrl.indexOf(".png") > 0) ||
                (imgUrl.indexOf(".gif") > 0)) {
            return true;
        }
        return false;
    }

    private static String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, LINK);
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, LINK);
        return link;
    }

    private static long readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, PUB_DATE);
        String pubDate = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, PUB_DATE);
        return convertStringToDate(pubDate);
    }


    // Processes title tags in the feed.
    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TITLE);
        return title;
    }

    // Processes description tags in the feed.
    private static String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, DESCRIPTION);
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, DESCRIPTION);
        return description;
    }

    private static String readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, CONTENT_ENCODED);
        String content = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, CONTENT_ENCODED);
        return content;
    }

    private static String readCategory(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, CATEGORY);
        String content = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, CATEGORY);
        return content;
    }

    private static String readGuid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, GUID);
        String content = readText(parser).split("=")[1];
        parser.require(XmlPullParser.END_TAG, ns, GUID);
        return content;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

//    public static Date convertStringToDate(String strDate) {
//        Log.d("Dateq", "strDate: " + strDate);
//        Date date = new Date();
//        SimpleDateFormat format = new SimpleDateFormat("EEE, d LLL yyyy HH:mm:ss zzz");
//        try {
//            date = format.parse(strDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return date;
//    }

    public static long convertStringToDate(String strDate) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("EEE, d LLL yyyy HH:mm:ss zzz");
        try {
            date = format.parse(strDate);
        } catch (ParseException e) {
//            throw new Error("convertStringToDate");
        }
        return date.getTime();
    }

}
