package com.example.litellm;

/**
 * Utility class for loading configuration from environment variables.
 * This approach is more secure than hardcoding credentials.
 */
public class ConfigLoader {
    
    // Environment variable names
    public static final String API_KEY_ENV = "LITELLM_API_KEY";
    public static final String COOKIE_VALUE_ENV = "LITELLM_COOKIE_VALUE";
    
    /**
     * Load configuration from environment variables.
     * 
     * @return Config object populated with environment values
     * @throws IllegalStateException if required environment variables are not set
     */
    public static Config loadFromEnvironment() throws IllegalStateException {
        String apiKey = System.getenv(API_KEY_ENV);
        String cookieValue = System.getenv(COOKIE_VALUE_ENV);
        
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException(
                "Environment variable " + API_KEY_ENV + " is not set. " +
                "Please set it to your LiteLLM API key.");
        }
        
        if (cookieValue == null || cookieValue.isEmpty()) {
            throw new IllegalStateException(
                "Environment variable " + COOKIE_VALUE_ENV + " is not set. " +
                "Please set it to your LiteLLM cookie value.");
        }
        
        return new Config(apiKey, cookieValue);
    }
    
    /**
     * Load configuration with default values.
     * Note: This is less secure and should only be used for development/testing.
     * 
     * @return Config object with default (empty) values
     */
    public static Config loadDefault() {
        return new Config();
    }
}