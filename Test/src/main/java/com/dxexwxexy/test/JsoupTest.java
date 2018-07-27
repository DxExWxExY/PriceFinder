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
            String url = "https://www.amazon.com/Apple-iPhone-Unlocked-Certified-Refurbished/dp/B00YD547Q6/ref=sr_1_1?s=wireless&ie=UTF8&qid=1532727070&sr=1-1&keywords=iphone+6";
            Document document = null;
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String question = document.select(".sims-fbt-checkbox-label").first().text();
            Pattern pattern = Pattern.compile("\\$\\d+\\.\\d+");
            Matcher matcher = pattern.matcher(question);
            if (matcher.find()) {
                System.out.println(matcher.group(0));
            }
            System.out.println(question);
        }).start();
    }
}
