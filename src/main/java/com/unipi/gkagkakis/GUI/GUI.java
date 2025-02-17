package com.unipi.gkagkakis.GUI;

import com.unipi.gkagkakis.main.Main;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.Map;

public class GUI extends JFrame {
    private JPanel MainPanel;
    private JTextField textField;
    private JLabel countryArea;
    private JLabel temperatureArea;
    private JLabel humidityArea;
    private JLabel windSpeedArea;
    private JLabel UVIndexArea;
    private JLabel weatherDescriptionArea;
    private JLabel Hint;
    private JButton searchButton;

    public GUI() {
        //τιτλος application
        setTitle("WeatherApp");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(MainPanel);
        pack();
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        //listener για οταν παταω enter α πατιεται το Search Button
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!textField.getText().isEmpty()) {
                        searchButton.doClick();
                        return;
                    }
                    setLabelsToDefault();
                }
            }
        });
        //Λειτουργια button αναλογα το input που δωσαμε
        searchButton.addActionListener(e -> {
            String text = textField.getText();
            if (!text.isEmpty()) {
                String formattedtext = text.replaceAll("\\s+", "+");
                Map<String, String> weatherData = Main.makePOSTRequest(formattedtext, false);
                handleData(weatherData);
                return;
            }
            setLabelsToDefault();
        });
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
}