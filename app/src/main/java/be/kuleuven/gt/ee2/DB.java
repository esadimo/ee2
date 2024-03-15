package be.kuleuven.gt.ee2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.*;

public class DB {

    public String makeGETRequest(String urlName){
        BufferedReader rd = null;
        StringBuilder sb = null;
        String line = null;
        try {
            URL url = new URL(urlName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuilder();
            while ((line = rd.readLine()) != null)
            {
                sb.append(line + '\n');
            }
            conn.disconnect();
            return sb.toString();
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        catch (ProtocolException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return "";

    }

    public void parseJSON(String jsonString){
        try {
            JSONArray array = new JSONArray(jsonString);
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject curObject = array.getJSONObject(i);
                System.out.println("The coach for the " + curObject.getString("Date") + " session is " + curObject.getString("Coach"));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String[] getInfo(String jsonString) {
        String result[]=new String[3] ;
        try {
            JSONArray array = new JSONArray(jsonString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject curObject = array.getJSONObject(i);
                result[0] = curObject.getString("Id");
                result[1] = curObject.getString("Table Number");
                result[2] = curObject.getString("Time");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        DB rc = new DB();
        String response = rc.makeGETRequest("https://studev.groept.be/api/a21ib2demo/all" );
        rc.parseJSON(response);
    }
}



