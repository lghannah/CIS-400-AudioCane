package com.getvoice.speech.restapi.common;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class TokenHolder {
    public static final String ASR_SCOPE = "audio_voice_assistant_get";
    public static final String TTS_SCOPE = "audio_tts_post";
    private static final String URL = "https://openapi.baidu.com/oauth/2.0/token";
    private String scope;
    private String apiKey;
    private String secretKey;
    private String token;
    private long expiresAt;

    public TokenHolder(String apiKey, String secretKey, String scope) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.scope = scope;
    }


    public String getToken() {
        return token;
    }


    public long getExpiresAt() {
        return expiresAt;
    }

    public void refresh() throws IOException, DemoException, JSONException {
        String getTokenURL = URL + "?grant_type=client_credentials"
                + "&client_id=" + ConnUtil.urlEncode(apiKey) + "&client_secret=" + ConnUtil.urlEncode(secretKey);
        System.out.println("token URL:" + getTokenURL);

        URL urlconn = new URL(getTokenURL);
        HttpURLConnection conn = (HttpURLConnection) urlconn.openConnection();
        conn.setConnectTimeout(5000);
        String result = ConnUtil.getResponseString(conn);
        System.out.println("Token result json:" + result);
        parseJson(result);
    }



    private void parseJson(String result) throws DemoException, JSONException {
        JSONObject json = new JSONObject(result);
        if (!json.has("access_token")) {
            throw new DemoException("access_token not obtained, " + result);
        }
        if (!json.has("scope")) {
            throw new DemoException("scope not obtained, " + result);
        }
        if (!json.getString("scope").contains(scope)) {
            throw new DemoException("scope not exist, " + scope + "," + result);
        }
        token = json.getString("access_token");
        expiresAt = System.currentTimeMillis() + json.getLong("expires_in") * 1000;
    }
}
