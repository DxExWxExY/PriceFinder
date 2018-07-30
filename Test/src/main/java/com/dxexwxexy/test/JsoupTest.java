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
            String url = "https://www.ebay.com/itm/iPhone-7-Plus-32GB-Verizon/253785012080?hash=item3b16c3f770%3Ag%3AYP8AAOSw0URbXmo~&LH_Auction=1&_sacat=0&_nkw=iphone&_from=R40&rt=nc";
            Document document = null;
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
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
