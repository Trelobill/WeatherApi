package com.unipi.gkagkakis.GUI;

import com.unipi.gkagkakis.database.Database;
import com.unipi.gkagkakis.main.Main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

public class GUI extends JFrame {
    private JTextField cityName;
    private JLabel countryArea;
    private JLabel temperatureArea;
    private JLabel humidityArea;
    private JLabel windSpeedArea;
    private JLabel UVIndexArea;
    private JLabel weatherDescriptionArea;
    private JButton searchButton;
    private JTabbedPane Search;
    private JTable statisticsTable;
    private JButton searchButtonStatistics;
    private JTextField cityNameStatistics;

    public GUI() {
        //τιτλος application
        setTitle("WeatherApp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(Search);
        pack();
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setupStatisticsTable();

        //listener για οταν παταω enter να πατιεται το Search Button
        cityName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!cityName.getText().isEmpty()) {
                        searchButton.doClick();
                        return;
                    }
                    setLabelsToDefault();
                }
            }
        });

        //Λειτουργια button αναλογα το input που δωσαμε
        searchButton.addActionListener(e -> {
            String text = cityName.getText();
            if (!text.isEmpty()) {
                String formattedtext = text.replaceAll("\\s+", "+");
                Map<String, String> weatherData = Main.makePOSTRequest(formattedtext, false);
                handleData(weatherData);
                return;
            }
            setLabelsToDefault();
        });

        //listener για οταν παταω enter να πατιεται το Search Button για τα στατιστικα
        cityNameStatistics.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!cityNameStatistics.getText().isEmpty()) {
                        searchButtonStatistics.doClick();
                        return;
                    }
                    fillTableWithDefaultValues();
                }
            }
        });

        //Λειτουργια button statistics αναλογα το input που δωσαμε
        searchButtonStatistics.addActionListener(e -> {
            String cityName = cityNameStatistics.getText();
            if (cityName.isEmpty()) {
                fillTableWithDefaultValues();
                return;
            }

            List<Map<String, String>> statisticsData = Database.getStatisticsOfTown(cityName);
            DefaultTableModel model = (DefaultTableModel) statisticsTable.getModel();
            model.setRowCount(0);

            if (statisticsData.isEmpty()) {
                fillTableWithDefaultValues();
            } else {
                statisticsData.forEach(data -> model.addRow(new Object[]{
                        data.get("Town"),
                        data.get("Date"),
                        data.get("Temperature"),
                        data.get("Humidity"),
                        data.get("Wind Speed"),
                        data.get("UV Index"),
                        data.get("Weather Description")
                }));
            }
        });
    }

    private void setupStatisticsTable() {
        String[] columnNames = {"Search query", "Date", "Temperature", "Humidity", "Wind Speed", "UV Index", "Weather Description"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        statisticsTable.setModel(model);
    }

    //εμφανιζει τα data στο app
    private void handleData(Map<String, String> weatherData) {
        if (weatherData.containsKey("Error")) {
            System.out.println(weatherData.get("Error"));
        } else {
            temperatureArea.setText(weatherData.get("Temperature"));
            countryArea.setText(weatherData.get("Country"));
            humidityArea.setText(weatherData.get("Humidity"));
            windSpeedArea.setText(weatherData.get("Wind Speed"));
            UVIndexArea.setText(weatherData.get("UV Index"));
            weatherDescriptionArea.setText(weatherData.get("Weather Description"));
        }
    }

    //αρχικοποιηση πεδιων αν δωσω κενο input
    private void setLabelsToDefault() {
        countryArea.setText("----");
        temperatureArea.setText("----");
        humidityArea.setText("----");
        windSpeedArea.setText("----");
        UVIndexArea.setText("----");
        weatherDescriptionArea.setText("----");
    }

    //γεμιζω πινακα με default values αν δεν εχω δωσει input
    private void fillTableWithDefaultValues() {
        DefaultTableModel model = (DefaultTableModel) statisticsTable.getModel();
        model.setRowCount(0);
        for (int i = 0; i < 5; i++) {
            model.addRow(new Object[]{"----", "----", "----", "----", "----", "----", "----"});
        }
    }

}