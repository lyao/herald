package org.church.volyn.parser;

import android.content.Context;

import org.church.volyn.downloadHelper.DiskCache;
import org.church.volyn.entities.NewsItem;

/**
 * Created by Admin on 27.02.2015.
 */
public class HtmlCompounder {

    private NewsItem mNewsItem;
    private Context mContext;
    public HtmlCompounder(Context context, NewsItem newsItem) {
        this.mNewsItem = newsItem;
        this.mContext = context;
    }

    public String compound() {
        String body = mNewsItem.getRawNewsContent();
        StringBuilder sb = new StringBuilder();
        sb.append(HtmlTags.HTML_HEADER_BEGIN);
        sb.append(HtmlTags.CLASS_FRONT_IMAGE_CONTAINER);
        sb.append(HtmlTags.CLASS_NEWS_TITLE);
        sb.append(HtmlTags.CLASS_FRONT_IMAGE);
        sb.append(HtmlTags.HTML_HEADER_END);
     //   sb.append(createTitleTag());
    //    sb.append(createFrontImageTag());
        sb.append(removeImageTag(body));
        sb.append(HtmlTags.HTML_FOOTER);
        return sb.toString();
    }

    private String removeImageTag(String body) {
        String imgTag = "";
        boolean removed = false;

        if (body.matches(RegExpressions.FRONT_IMAGE_REPLACE_A_IMG)) {
            removed = true;
            body = body.replaceAll(RegExpressions.FRONT_IMAGE_REPLACE_A_IMG, imgTag);
            return body;
        }

        if (!removed)
            body = body.replaceAll(RegExpressions.FRONT_IMAGE_URL_IMG, imgTag);

        body = body.replaceAll(RegExpressions.YOUTUBE_REPLACE_SPAN_TAG, imgTag);

        return body;
    }

    private String createFrontImageTag() {
        String imgTagTemplate = "<div class=\"front_image_container\"><img class=\"front_image\" src=\"%s\"/></div>";
        String imgTag = "";
        if (mNewsItem.getImageUrl() != null) {
            imgTag = String.format(imgTagTemplate, "file://" + DiskCache.getInstance(mContext).getFilesDir().toString() + "/" +DiskCache.getFileName(mNewsItem.getImageUrl()));
        }

        return imgTag;
    }

    private String createTitleTag() {
        String titleTagTemplate = "<div class=\"news_title\">%s</div>";
        String titleTag = "";
        if (mNewsItem.getTitle() != null) {
            titleTag = String.format(titleTagTemplate, mNewsItem.getTitle());
        }
        return titleTag;
    }

}
