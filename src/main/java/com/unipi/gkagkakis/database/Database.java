package com.unipi.gkagkakis.database;

import com.unipi.gkagkakis.main.Main;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    public static Connection connect() {
        //ονομα βασης: weather_app
        String connectionString = "jdbc:sqlite:weather_app.db";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectionString);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    //δημιουργια table στη βαση
    public static void createTable() {
        try {
            Connection connection = connect();
            Statement statement = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS WEATHER_INFO"
                    + "(AA INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "TOWN VARCHAR(30) NOT NULL,"
                    + "TIMESTAMP TIMESTAMP NOT NULL,"
                    + "TEMP_C VARCHAR(20) NOT NULL,"
                    + "HUMIDITY VARCHAR(20) NOT NULL,"
                    + "WIND_SPEED_KMPH VARCHAR(20) NOT NULL,"
                    + "UV_INDEX INTEGER NOT NULL,"
                    + "WEATHER_DESC TEXT NOT NULL)";
            String checkTableExistsSQL = "SELECT name FROM sqlite_master WHERE type='table' AND name='WEATHER_INFO'";
            ResultSet resultSet = statement.executeQuery(checkTableExistsSQL);
            //αν υπαρχει ηδη το table δεν κανει τιποτα αλλιω το δημιουργει
            if (!resultSet.next()) {
                statement.executeUpdate(createTableSQL);
                coloredDebugLog("Database table created successfully...");
            } else {
                coloredDebugLog("Database table already exists...");
            }
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //συναρτηση για insert του search που εγινε
    public static void insertNewWeatherSearch(String town, Timestamp timestamp, String temp_c, String humidity, String wind_speed_kmph, Integer uv_index, String weather_desc) {
        try {
            Connection connection = connect();
            String insertSQL = "INSERT INTO WEATHER_INFO (TOWN, TIMESTAMP, TEMP_C, HUMIDITY, WIND_SPEED_KMPH, UV_INDEX, WEATHER_DESC) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setString(1, town);
            preparedStatement.setTimestamp(2, timestamp);
            preparedStatement.setString(3, temp_c);
            preparedStatement.setString(4, humidity);
            preparedStatement.setString(5, wind_speed_kmph);
            preparedStatement.setInt(6, uv_index);
            preparedStatement.setString(7, weather_desc);
            int count = preparedStatement.executeUpdate();
            if (count > 0) {
                coloredDebugLog("1 new weather search inserted into the database...");
            }
            preparedStatement.close();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void coloredDebugLog(String message) {
        System.out.println(Main.BOLD + Main.YELLOW + "\n---------------------DEBUG LOG---------------------");
        System.out.println(message);
        System.out.println("---------------------------------------------------");
    }

}


