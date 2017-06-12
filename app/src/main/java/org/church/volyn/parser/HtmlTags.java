package org.church.volyn.parser;

/**
 * Created by Admin on 27.02.2015.
 */
public class HtmlTags {
    public static final String HTML_HEADER_BEGIN = "<html>\n" +
            "<head>\n" +
            "  <style type=\"text/css\">\n";

    public static final String HTML_HEADER_END = "  </style>\n" +
            "</head>\n" +
            "<body>\n";

    public static final String HTML_FOOTER = "</body>\n" +
            "</html>";

    public static final String CLASS_FRONT_IMAGE_CONTAINER = ".front_image_container {\n" +
            "width: 100%; \n" +
           // "height: 280px;\n" +
            "overflow: hidden;}\n";

    public static final String CLASS_FRONT_IMAGE = ".front_image {\n" +
          //  "height: 280px;\n" +
            "    width: 100%;}\n";

    public static final String CLASS_NEWS_TITLE = ".news_title {\n" +
            "padding-top:20px;\n" +
            "padding-bottom: 20px;\n" +
            "font-size: 16pt;\n" +
            "font-family: Roboto;\n" +
            "text-color: #000000;\n" +
            "}";

}
