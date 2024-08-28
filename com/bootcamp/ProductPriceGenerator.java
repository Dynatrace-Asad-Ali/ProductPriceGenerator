package com.bootcamp;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;

public class ProductPriceGenerator {

    private static String DT_URL = "DT_URL";
    private static String DT_LOG_INGEST_TOKEN = "DT_LOG_INGEST_TOKEN";

    public static void main(String[] args) {
        String dtUrl = System.getenv(DT_URL);
        String logToken = System.getenv(DT_LOG_INGEST_TOKEN);
        HttpClient client = HttpClient.newHttpClient();

        String dtUrlEndPoint = dtUrl + "/api/v2/logs/ingest";
        String authorization = "Api-Token " + logToken;

        String jsonPayload = """
            [
                {
                "content": "The price of the product 273EIR is $56",
                "log.source": "/var/open/syslog",
                "log.tag": ["tag1", "pipe"]
                },
                {
                "content": "The price of the product 938RNG is $49",
                "log.source": "/var/open/syslog",
                "log.tag": ["tag1", "pipe"]
                },
                {
                "content": "The price of the product 285VJS is $27",
                "log.source": "/var/open/syslog",
                "log.tag": ["tag1", "pipe"]
                },
                {
                "content": "The price of the product 386SLB is $37",
                "log.source": "/var/open/syslog",
                "log.tag": ["tag1", "pipe"]
                }
            ]
            """;
        // Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(dtUrlEndPoint))
                .header("accept", "application/json; charset=utf-8")
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(jsonPayload))
                .build();

        // Define logs
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            generateData(client, request);
        };

        // Schedule the task to run every minute
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);

    }

    private static void generateData(HttpClient client, HttpRequest request) {
       /*  String jsonPayload = """
            [
              {
                "content": "The price of the product 273EIR is $56",
                "log.source": "/var/open/syslog",
                "log.tag": ["tag1", "pipe"]
              },
              {
                "content": "The price of the product 938RNG is $49",
                "log.source": "/var/open/syslog",
                "log.tag": ["tag1", "pipe"]
              },
              {
                "content": "The price of the product 285VJS is $27",
                "log.source": "/var/open/syslog",
                "log.tag": ["tag1", "pipe"]
              },
              {
                "content": "The price of the product 386SLB is $37",
                "log.source": "/var/open/syslog",
                "log.tag": ["tag1", "pipe"]
              }
            ]
            """;
        */
        try {
            // Convert logs to JSON
           /* ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonObject = objectMapper.createObjectNode();
            jsonObject.put("content", "The price of the product 273EIR is $56");
            jsonObject.put("log.source", "/var/open/syslog");
            jsonObject.put("log.tag",  "pipe");

            String logsJson = jsonObject.toString();
            //String logsJson = objectMapper.writeValueAsString(logs);
            */
            // Create HTTP client
           /*  HttpClient client = HttpClient.newHttpClient();

            String dtUrlEndPoint = dtUrl + "/api/v2/logs/ingest";
            String authorization = "Api-Token " + logToken;
            // Build the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(dtUrlEndPoint))
                    .header("accept", "application/json; charset=utf-8")
                    .header("Authorization", authorization)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonPayload))
                    .build();
            */
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            // Handle the response
            if (response.statusCode() == 200 || response.statusCode() == 204)  {
                System.out.println("Response: " + response.body());
            } else {
                System.err.println("Failed to send logs: " + response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
