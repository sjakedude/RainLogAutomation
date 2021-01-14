import jdk.nashorn.internal.runtime.JSONFunctions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class Launch {

    private static String apiKey = "a77d00cf8cb244f4801195048211101";

    public static void main(String[] args) throws IOException {

        String date = "2021-01-12";
        Scanner scan = new Scanner(new File("data/sloane_site_locations.dat"));

        while (scan.hasNextLine()) {

            String rawLine = scan.nextLine();
            String[] tokens = rawLine.split("\\|");

            String siteName = tokens[0];
            String siteLocation = tokens[1];

            String weatherData = getWeatherData(siteLocation, date);

            JSONObject obj = new JSONObject(weatherData);
            JSONObject forecast = obj.getJSONObject("forecast");
            JSONArray level = forecast.getJSONArray("forecastday");
            JSONObject a = level.getJSONObject(0);
            JSONObject day = a.getJSONObject("day");
            double precip = day.getDouble("totalprecip_in");

            System.out.println(siteName + "|" + precip);

        }
    }

    public static String getWeatherData(String siteLocation, String date) throws IOException {
        URL url = new URL("http://api.weatherapi.com/v1/history.json?key=" + apiKey + "&q=" + siteLocation + "&dt=" + date);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        if (status == 200) {
            return content.toString();
        } else {
            System.out.println("ERROR: NON 200 STATUS CODE");
            System.exit(1);
            return null;
        }
    }
}
