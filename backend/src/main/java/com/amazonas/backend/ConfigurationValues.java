package com.amazonas.backend;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigurationValues {
    public static String getProperty(String property) {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(BackendApplication.getFolderPath()+"config.properties")) {
            // Load the properties file
            properties.load(input);

            // Accessing properties
            return properties.getProperty(property);

        } catch (Exception e) {
            throw new ClassCastException("couldn't find property " + property + " in configuration file - " + e.getMessage());
        }
    }
}
