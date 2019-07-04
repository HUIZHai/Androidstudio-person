package juheApi;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;



public class GetJSON {
    private String JSON;
    public void sendRequestWithHttpURLConnection() {
        new Thread() {
            @Override
            public void run() {
                URL url;
                HttpURLConnection connection = null;
                try {
                    // url = new
                    // URL("http://10.2.5.119:8080/Server/getData.json");
                    String cityName = URLEncoder.encode("杭州", "utf-8");
                    url = new URL(
                            "http://v.juhe.cn/weather/index?format=2&cityname="
                                    + cityName
                                    + "&key=a1832ceb40074a1bee7dd4ec953ae525");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    // 下面对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    System.out.println("response=" + response.toString());
                    //parseWithJSON(response.toString());
                    parseWeatherWithJSON(response.toString());
                    JSON = response.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }.start();

    }

    protected void parseWeatherWithJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String resultcode = jsonObject.getString("resultcode");
            if (resultcode.equals("200")) {
                JSONObject resultObject = jsonObject.getJSONObject("result");
                JSONObject todayObject = resultObject.getJSONObject("today");
                String date_y = todayObject.getString("date_y");
                String week = todayObject.getString("week");
                String temperature = todayObject.getString("temperature");
                Log.d("MainActivity", "date_y=" + date_y + "week=" + week + "temp=" + temperature);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void parseWithJSON(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String version = jsonObject.getString("version");
                Log.d("MainActivity", "id=" + id + "name=" + name + "version="
                        + version);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getJSON(){
        while(JSON==null);
        return JSON;
    }
}

