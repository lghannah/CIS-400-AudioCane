package com.getvoice.speech.restapi.ttsdemo;

import android.util.Log;

import com.getvoice.speech.restapi.common.DemoException;
import com.getvoice.speech.restapi.common.ConnUtil;
import com.getvoice.speech.restapi.common.TokenHolder;

import org.json.JSONException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class TtsMain {

    public static void main(String[] args) throws IOException, DemoException, JSONException {
        (new TtsMain()).run();
    }
    String TAG = "Developer-------------";
    private final String appKey = "dpPme5E74rbKqScUnlNDI1Bn";
    private final String secretKey = "UqSyRHe1O1Sl5paiqX6POZ72iwpk0GEt";
    public String text = "Hello,here!";
    private final int per = 0;
    private final int spd = 5;
    private final int pit = 5;
    private final int vol = 5;
    private final int aue = 6;

    public final String url = "https://tsn.baidu.com/text2audio";

    private String cuid = "1234567JAVA";

    public byte[] run() throws IOException, DemoException, JSONException {
        TokenHolder holder = new TokenHolder(appKey, secretKey, TokenHolder.ASR_SCOPE);
        holder.refresh();
        String token = holder.getToken();
        String params = "tex=" + ConnUtil.urlEncode(ConnUtil.urlEncode(text));
        params += "&per=" + per;
        params += "&spd=" + spd;
        params += "&pit=" + pit;
        params += "&vol=" + vol;
        params += "&cuid=" + cuid;
        params += "&tok=" + token;
        params += "&aue=" + aue;
        params += "&lan=zh&ctp=1";
        Log.e(TAG,url + "?" + params);
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
        printWriter.write(params);
        printWriter.close();
        String contentType = conn.getContentType();
        if (contentType.contains("audio/")) {
            byte[] bytes = ConnUtil.getResponseBytes(conn);
            String format = getFormat(aue);
            return bytes;
        } else {
            System.err.println("ERROR: content-type= " + contentType);
            String res = ConnUtil.getResponseString(conn);
            System.err.println(res);
            return null;
        }
    }
    private String getFormat(int aue) {
        String[] formats = {"mp3", "pcm", "pcm", "wav"};
        return formats[aue - 3];
    }
}
