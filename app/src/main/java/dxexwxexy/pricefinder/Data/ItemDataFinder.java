package dxexwxexy.pricefinder.Data;

import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemDataFinder {

    private String url;
    private String store;
    private double initialPrice, currentPrice;
    private Handler handler;
    private boolean fetched;
    private static final String AMAZON_ID = ".sims-fbt-checkbox-label";
    private static final String AMAZON = "amazon";

    ItemDataFinder(String url) {
        this.url = url;
        // TODO: 7/27/2018 undo hardcode
        store = AMAZON;
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
//        setStore();
        fetchPrices();
    }

    private void fetchPrices() {
        switch (store) {
            case AMAZON:
                getFromStore(AMAZON_ID);
                break;
        }
    }

    private void setStore() {
        if (url.matches(AMAZON)) {
            System.out.println(url);
            store = AMAZON;
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public double getInitialPrice() {
        if (fetched) {
            return initialPrice;
        }
        return -1.0;
    }

    public double getCurrentPrice() {
        if (fetched) {
            return currentPrice;
        }
        return -1.0;
    }

    public boolean isFetched() {
        return fetched;
    }
}
