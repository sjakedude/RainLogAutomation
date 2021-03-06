import com.mysql.cj.jdbc.MysqlDataSource;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;

public class Launch {

    private static String apiKey = "a77d00cf8cb244f4801195048211101";

    public static void main(String[] args) throws IOException, SQLException {

        String date = "2021-01-22";
        Scanner scan = new Scanner(new File("data/sloane_site_locations.dat"));

        while (scan.hasNextLine()) {

            String rawLine = scan.nextLine();
            String[] tokens = rawLine.split("\\|");

            String siteName = tokens[0];
            String siteLocation = tokens[1];

            String weatherData = getWeatherData(siteLocation, date);

            //printTotalPrecipitation(weatherData, siteName);
            printHourByHour(weatherData);
            System.exit(0);
        }
    }

    private static void printHourByHour(String weatherData) {
        JSONObject obj = new JSONObject(weatherData);
        JSONObject forecast = obj.getJSONObject("forecast");
        JSONArray level = forecast.getJSONArray("forecastday");
        JSONObject a = level.getJSONObject(0);
        JSONArray hours = a.getJSONArray("hour");
        for (int i = 0; i < hours.length(); i++) {
            double aa = hours.getJSONObject(i).getDouble("precip_in");
            System.out.println(i + "|" + aa);
        }


    }

    private static void printTotalPrecipitation(String weatherData, String siteName) {
        JSONObject obj = new JSONObject(weatherData);
        JSONObject forecast = obj.getJSONObject("forecast");
        JSONArray level = forecast.getJSONArray("forecastday");
        JSONObject a = level.getJSONObject(0);
        JSONObject day = a.getJSONObject("day");
        double precip = day.getDouble("totalprecip_in");

        System.out.println(siteName + "|" + precip);
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

    public static void populateDatabase() throws SQLException {

        // Connecting to the mysql database
        Connection  conn = DriverManager.getConnection(
                "jdbc:mysql://10.0.0.66:3306/rainlog", "javauser", "pathlightpro1234"
        );

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SHOW TABLES");
        while (rs.next()) {
            System.out.println(rs.getString(1));

        }
    }
}
