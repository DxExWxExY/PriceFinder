package dxexwxexy.pricefinder.Data;

import android.os.Handler;
import android.os.Message;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemDataFinder {

    private String url;
    private String store;
    private double initialPrice, currentPrice;
    private Handler handler;
    private boolean fetched;
    private static final String AMAZON_ID = ".a-section .a-spacing-none";
    private static final String AMAZON = "amazon";
    private static final String EBAY_ID = ".notranslate";
    private static final String EBAY = "ebay";

    ItemDataFinder(String url) {
        this.url = url;
        setStore();
        handler = new Handler(msg -> {
            if (fetched) {
                currentPrice = (double) msg.obj;
            } else {
                initialPrice = (double) msg.obj;
                currentPrice = initialPrice;
                fetched = true;
            }
            return true;
        });
        fetchPrices();
    }

    private void fetchPrices() {
        if (store.equals(AMAZON)) {
            getFromStore(AMAZON_ID);
        } else if (store.equals(EBAY)) {
            getFromStore(EBAY_ID);
        }
    }

    private void setStore() {
        if (url.matches("\\S+" + AMAZON + "\\S+")) {
            System.out.println("set to amazon");
            this.store = "amazon";
        } else if (url.matches("\\S+" + EBAY + "\\S+")) {
            System.out.println("set to ebay");
            this.store = "ebay";
        } else {
            this.store = "Unknown";
        }
    }

    public String getStore() {
        return store;
    }

    private void getFromStore(String identifier) {
        new Thread(() -> {
            Message message = new Message();
            try {
                Document document;
                document = Jsoup.connect(url).get();
                String question = document.select(identifier).first().text();
                Pattern pattern = Pattern.compile("\\$\\d+\\.\\d+");
                Matcher matcher = pattern.matcher(question);
                if (matcher.find()) {
                    message.obj = Double.parseDouble(matcher.group(0).substring(1));
                }
                handler.sendMessage(message);
            } catch (Exception e) {
                fetched = true;
                e.printStackTrace();
            }
        }).start();
    }

    public double getInitialPrice() {
        if (fetched) {
            return initialPrice;
        }
        return 0;
    }

    public double getCurrentPrice() {
        if (fetched) {
            fetchPrices();
            return currentPrice;
        }
        return 0;
    }

    public boolean isFetched() {
        return fetched;
    }
}
