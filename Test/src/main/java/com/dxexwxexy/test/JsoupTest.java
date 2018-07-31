package com.dxexwxexy.test;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsoupTest {
    public static void main(String[] args) {
        new Thread(() -> {
            String url = "dfkjdshfj";
            Document document = null;
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException | IllegalArgumentException e) {
//                e.printStackTrace();
            }
            String question = document.select(".notranslate").first().text();
            Pattern pattern = Pattern.compile("\\$\\d+\\.\\d+");
            Matcher matcher = pattern.matcher(question);
            if (matcher.find()) {
                System.out.println(matcher.group(0));
            }
            System.out.println(question);
        }).start();
    }
}
