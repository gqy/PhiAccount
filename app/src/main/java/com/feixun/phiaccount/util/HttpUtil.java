package com.feixun.phiaccount.util;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qinyang.ge on 2016/3/7.
 */
public class HttpUtil {
    public static String get(String urlString) {
        String result = "";
        try {
            URL url = new URL(urlString);
            Log.i("url ", url.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setConnectTimeout(5000);

            InputStream input = con.getInputStream();
            byte[] bytes = new byte[1024];
            int length = 0;

            while ((length = input.read(bytes)) != -1) {
                result += new String(bytes, "UTF-8");
            }

            input.close();

            Log.i("result1 ", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  result;
    }
}
