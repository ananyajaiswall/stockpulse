package com.example.litellm;

import java.util.Arrays;
import java.util.List;

/**
 * Sample application demonstrating the use of LiteLLMClient.
 * 
 * To run this application, you need to set the following environment variables:
 * LITELLM_API_KEY=your_api_key_here
 * LITELLM_COOKIE_VALUE=your_cookie_value_here
 */
public class SampleApplication {
    
    public static void main(String[] args) {
        try {
            // Load configuration from environment variables (recommended approach)
            Config config = ConfigLoader.loadFromEnvironment();
            
            // Alternative: Load with default empty values (not recommended for production)
            // Config config = ConfigLoader.loadDefault();
            // config.setApiKey("your_api_key_here");
            // config.setCookieValue("your_cookie_value_here");
            
            // Create the API client
            LiteLLMClient client = new LiteLLMClient(config);
            
            // Create a simple message
            ChatCompletionModels.Message userMessage = 
                new ChatCompletionModels.Message("user", "OK?");
            
            List<ChatCompletionModels.Message> messages = Arrays.asList(userMessage);
            
            // Send the request
            System.out.println("Sending request to LiteLLM API...");
            ChatCompletionModels.ChatResponse response = 
                client.createChatCompletion(messages);
            
            // Print the response
            System.out.println("Response received:");
            if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                ChatCompletionModels.Choice choice = response.getChoices().get(0);
                if (choice.getMessage() != null) {
                    System.out.println("Response: " + choice.getMessage().getContent());
                }
            }
            
        } catch (IllegalStateException e) {
            System.err.println("Configuration error: " + e.getMessage());
            System.err.println("Please set the required environment variables:");
            System.err.println("  LITELLM_API_KEY=your_api_key_here");
            System.err.println("  LITELLM_COOKIE_VALUE=your_cookie_value_here");
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}