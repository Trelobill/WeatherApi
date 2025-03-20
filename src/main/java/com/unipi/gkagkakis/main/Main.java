package com.unipi.gkagkakis.main;

import com.unipi.gkagkakis.GUI.GUI;
import com.unipi.gkagkakis.database.Database;

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
import java.util.List;
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
            try {
                System.out.print("\n" + RESET + UNDERLINE + ITALIC + ORANGE + "If you want statistics, type \"search\" before city! " + RESET);
                System.out.print("\n" + RESET + BOLD + PURPLE + "City Name: " + RESET);
                String option = scanner.nextLine();
                if (option.isEmpty()) {
                    System.out.println(RED + BOLD + "Please enter a city name!");
                    continue;
                }
                String[] words = option.split("\\s+");
                if (words[0].equalsIgnoreCase("search")) {
                    if (words.length > 1) {
                        String city = option.substring(option.indexOf(" ") + 1);
                        handleSearchStatistics(city);
                    } else {
                        System.out.println(RED + BOLD + "Please enter a city name after 'search'!");
                    }
                } else {
                    String formattedOption = option.trim().replaceAll("\\s+", "+");
                    makePOSTRequest(formattedOption, true);
                }
            } catch (Exception e) {
                System.out.println(RED + BOLD + "An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
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

    //χειρισμος των στατιστικων
    public static void handleSearchStatistics(String city) {
        //παιρνουμε τα data απο την βαση
        List<Map<String, String>> results = Database.getStatisticsOfTown(city);
        if (!results.isEmpty()) {
            //απλα για ομορφια του πινακα
            int maxSearchQueryLength = "Search Query".length();
            int maxDateLength = "Date".length();
            int maxTempLength = "Temperature".length();
            int maxHumidityLength = "Humidity".length();
            int maxWindSpeedLength = "Wind Speed".length();
            int maxUVIndexLength = "UV Index".length();
            int maxWeatherDescLength = "Weather Description".length();

            for (Map<String, String> weatherData : results) {
                maxSearchQueryLength = Math.max(maxSearchQueryLength, weatherData.get("Town").length());
                maxDateLength = Math.max(maxDateLength, weatherData.get("Date").length());
                maxTempLength = Math.max(maxTempLength, weatherData.get("Temperature").length());
                maxHumidityLength = Math.max(maxHumidityLength, weatherData.get("Humidity").length());
                maxWindSpeedLength = Math.max(maxWindSpeedLength, weatherData.get("Wind Speed").length());
                maxUVIndexLength = Math.max(maxUVIndexLength, weatherData.get("UV Index").length());
                maxWeatherDescLength = Math.max(maxWeatherDescLength, weatherData.get("Weather Description").length());
            }

            //συνολικο μηκος πινακα
            int totalWidth = maxSearchQueryLength + maxDateLength + maxTempLength + maxHumidityLength + maxWindSpeedLength + maxUVIndexLength + maxWeatherDescLength + 6 * 3 + 7;

            //κεντραρισμα του Header
            String header = "RESULTS FOR " + city.replace("+", " ").toUpperCase();
            int padding = (totalWidth - header.length()) / 2;
            String centeredHeader = " ".repeat(padding) + header + " ".repeat(totalWidth - header.length() - padding);

            String dashLine = "-".repeat(totalWidth);

            System.out.println("\n" + Main.BOLD + Main.YELLOW + dashLine + Main.RESET);
            System.out.println(Main.BOLD + Main.YELLOW + centeredHeader + Main.RESET);
            System.out.println(Main.BOLD + Main.YELLOW + dashLine + Main.RESET);

            //κεντραρισμα των τιτλων
            String searchQueryTitle = centerText("Search Query", maxSearchQueryLength);
            String dateTitle = centerText("Date", maxDateLength);
            String tempTitle = centerText("Temperature", maxTempLength);
            String humidityTitle = centerText("Humidity", maxHumidityLength);
            String windSpeedTitle = centerText("Wind Speed", maxWindSpeedLength);
            String uvIndexTitle = centerText("UV Index", maxUVIndexLength);
            String weatherDescTitle = centerText("Weather Description", maxWeatherDescLength);

            //εκτυπωση τιτλων
            String format = "| %-" + maxSearchQueryLength + "s | %-" + maxDateLength + "s | %-" + maxTempLength + "s | %-" + maxHumidityLength + "s | %-" + maxWindSpeedLength + "s | %-" + maxUVIndexLength + "s | %-" + maxWeatherDescLength + "s |%n";
            System.out.printf(Main.BOLD + Main.CYAN + format + Main.RESET,
                    searchQueryTitle, dateTitle, tempTitle, humidityTitle, windSpeedTitle, uvIndexTitle, weatherDescTitle);
            System.out.println(Main.RESET + "|-" + "-".repeat(maxSearchQueryLength) + "-|-" + "-".repeat(maxDateLength) + "-|-" + "-".repeat(maxTempLength) + "-|-" + "-".repeat(maxHumidityLength) + "-|-" + "-".repeat(maxWindSpeedLength) + "-|-" + "-".repeat(maxUVIndexLength) + "-|-" + "-".repeat(maxWeatherDescLength) + "-|");

            //εκτυπωση των στατιστικων
            for (Map<String, String> weatherData : results) {
                System.out.printf(format,
                        centerText(weatherData.get("Town"), maxSearchQueryLength), centerText(weatherData.get("Date"), maxDateLength), centerText(weatherData.get("Temperature"), maxTempLength),
                        centerText(weatherData.get("Humidity"), maxHumidityLength), centerText(weatherData.get("Wind Speed"), maxWindSpeedLength), centerText(weatherData.get("UV Index"), maxUVIndexLength),
                        centerText(weatherData.get("Weather Description"), maxWeatherDescLength));
            }
        } else {
            System.out.println(BOLD + RED + "No statistics found for " + city);
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
            String wind_speed_Kmph = weatherParts[2].substring(1);
            int uv_index = Integer.parseInt(weatherParts[3]);
            String country = getCountry(city);
            StringBuilder weather_Desc = new StringBuilder();
            for (int i = 4; i < weatherParts.length; i++) {
                weather_Desc.append(weatherParts[i]).append(" ");
            }
            weatherData.put("Temperature", temp_c);
            weatherData.put("Humidity", humidity);
            weatherData.put("Wind Speed", wind_speed_Kmph);
            weatherData.put("UV Index", String.valueOf(uv_index));
            weatherData.put("Weather Description", weather_Desc.toString().trim());
            weatherData.put("Country", country);

            if (terminal) {
                System.out.println(ITALIC + CYAN + "\nFound a city in country: " + ORANGE + BOLD + getCountry(city) + RESET + ITALIC + CYAN + ".");
                System.out.println("If it's wrong, please try typing the country next to city name!\n");
                System.out.println(RED + "Temperature: " + YELLOW + temp_c);
                System.out.println(RED + "Humidity: " + YELLOW + humidity);
                System.out.println(RED + "Wind Speed: " + YELLOW + wind_speed_Kmph);
                System.out.println(RED + "UV Index: " + YELLOW + uv_index);
                System.out.println(RED + "Weather Description: " + YELLOW + weather_Desc.toString().trim());
            }
            Database.insertNewWeatherSearch(city.replace("+", " "), new Timestamp(System.currentTimeMillis()), temp_c, humidity, wind_speed_Kmph, uv_index, weather_Desc.toString().trim());
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


    private static String centerText(String text, int width) {
        int padding = (width - text.trim().length()) / 2;
        return " ".repeat(padding) + text.trim() + " ".repeat(width - text.trim().length() - padding);
    }
}
