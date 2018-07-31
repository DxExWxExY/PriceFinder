package com.dxexwxexy.test;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsoupTest {
    public static void main(String[] args) {
        new Thread(() -> {
            String url = "https://www.google.com/";
            try {
                Document document = Jsoup.connect(url).get();
                String question = document.select(".notranslate").first().text();
                Pattern pattern = Pattern.compile("\\$\\d+\\.\\d+");
                Matcher matcher = pattern.matcher(question);
                if (matcher.find()) {
                    System.out.println(matcher.group(0));
                }
                System.out.println(question);
            } catch (IllegalArgumentException e) {
                System.out.println("malformed url");
            } catch (NullPointerException e) {
                System.out.println("not found");
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }).start();
    }
}
