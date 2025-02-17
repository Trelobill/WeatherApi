package com.unipi.gkagkakis.main;

import com.unipi.gkagkakis.GUI.GUI;
import com.unipi.gkagkakis.database.Database;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String ORANGE = "\u001B[38;5;208m";
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String ITALIC = "\u001B[3m";

    public static void main(String[] args) {
        GUIorConsole();
    }

    //συναρτηση που τρεχει συνεχεια αν δωσω terminal app
    private static void displayMenu() {
        Database.createTable();
        System.out.println("\n" + BLUE + BOLD + ITALIC + UNDERLINE + "Welcome to our terminal weather app! \nPlease enter a city to get detailed weather information!");
        while (true) {
            System.out.print("\n" + RESET + BOLD + PURPLE + "City Name: " + RESET);
            String option = scanner.nextLine();
            if (option.isEmpty()) {
                System.out.println(RED + BOLD + "Please enter a city name!");
                continue;
            }
            String formattedOption = option.replaceAll("\\s+", "+");
            makePOSTRequest(formattedOption, true);
        }
    }

    //στην αρχη ο χρηστης διαλεγει αν θελει GUI ή console app
    private static void GUIorConsole() {
        System.out.println("\n" + BLUE + BOLD + "Press 1 for GUI or 2 for terminal app!");
        String option = scanner.nextLine();
        switch (option) {
            case "1":
                Database.createTable();
                new GUI();
                System.out.println("\n" + YELLOW + BOLD + "Application starting...");
                break;
            case "2":
                displayMenu();
                break;
            default:
                System.out.println(BOLD + RED + "Invalid option. Please try again.");
                GUIorConsole();
                break;
        }
    }

    //γινεται το request για τα data μεσω json
    //χειρισμος και επιστροφη τιμων
    public static Map<String, String> makePOSTRequest(String city, boolean terminal) {
        Map<String, String> weatherData = new HashMap<>();
        try {
            String format = URLEncoder.encode("%t %h %w %u %C", StandardCharsets.UTF_8);
            String endpoint = "https://wttr.in/" + city + "?format=" + format;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String[] weatherParts = response.body().split(" ");
            String temp_c = weatherParts[0];
            String humidity = weatherParts[1];
            String wind_speed_Kmph = weatherParts[2].substring(1); // Remove the arrow symbol
            int uv_index = Integer.parseInt(weatherParts[3]);
            StringBuilder weather_Desc = new StringBuilder();
            for (int i = 4; i < weatherParts.length; i++) {
                weather_Desc.append(weatherParts[i]).append(" ");
            }
            weatherData.put("Temperature", temp_c);
            weatherData.put("Humidity", humidity);
            weatherData.put("Wind Speed", wind_speed_Kmph);
            weatherData.put("UV Index", String.valueOf(uv_index));
            weatherData.put("Weather Description", weather_Desc.toString().trim());

            if (terminal) {
                System.out.println(ITALIC + CYAN + "\nFound a city in country: " + ORANGE + BOLD + getCountry(city) + RESET + ITALIC + CYAN + ".");
                System.out.println("If it's wrong, please try typing the country next to city name!\n");
                System.out.println(RED + "Temperature: " + YELLOW + temp_c);
                System.out.println(RED + "Humidity: " + YELLOW + humidity);
                System.out.println(RED + "Wind Speed: " + YELLOW + wind_speed_Kmph);
                System.out.println(RED + "UV Index: " + YELLOW + uv_index);
                System.out.println(RED + "Weather Description: " + YELLOW + weather_Desc.toString().trim());
            }
            Database.insertNewWeatherSearch(city, new Timestamp(System.currentTimeMillis()), temp_c, humidity, wind_speed_Kmph, uv_index, weather_Desc.toString().trim());
        } catch (Exception e) {
            System.out.println(RED + BOLD + "An error occurred while processing the request: " + e.getMessage());
            e.printStackTrace();
        }
        return weatherData;
    }

    private static String getCountry(String city) {
        try {
            String countryOnly = "https://wttr.in/" + city + "?format=j2";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(countryOnly))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(response.body(), JsonObject.class);
            return jsonObject.getAsJsonArray("nearest_area").get(0).getAsJsonObject().get("country").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
        } catch (Exception e) {
            System.out.println(RED + BOLD + "An error occurred while trying to get country: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
