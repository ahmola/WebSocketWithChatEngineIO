package com.example.chat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class ChatController {

    @Value("${CHATIO.PROJECT_ID}")
    private static String CHAT_ENGINE_PROJECT_ID;
    @Value("${CHATIO.PRIVATE_KEY}")
    private static String CHAT_ENGINE_PRIVATE_KEY;

    @CrossOrigin
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity getLogin(@RequestBody HashMap<String, String> request){
        HttpURLConnection connection = null;

        try {
            // Create Get Request
            URL url = new URL("https://api.chatengine.io/users/me");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Set Header
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Project-ID", CHAT_ENGINE_PROJECT_ID);
            connection.setRequestProperty("User-Name", request.get("username"));
            connection.setRequestProperty("User-Secret", request.get("secret"));

            // Generate response String
            StringBuilder responseStr = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))){
                String responseLine = null;
                while ((responseLine = br.readLine()) != null){
                    responseStr.append(responseLine.trim());
                }
            }

            Map<String, Object> response = new Gson().fromJson(
                    responseStr.toString(), new TypeToken<HashMap<String, Object>>(){
                    }.getType());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }

    @CrossOrigin
    @RequestMapping(path = "/signup", method = RequestMethod.POST)
    public ResponseEntity newSignUp(@RequestBody HashMap<String, String> request){
        HttpURLConnection connection = null;
        try {
            URL url = new URL("https://api.chatengine.io/users");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Private-Key", CHAT_ENGINE_PRIVATE_KEY);

            connection.setDoOutput(true);
            Map<String, String> body = new HashMap<String, String>();
            body.put("username", request.get("username"));
            body.put("secret", request.get("secret"));
            body.put("email", request.get("email"));
            body.put("first_name", request.get("first_name"));
            body.put("last_name", request.get("last_name"));

            Gson gson = new Gson();
            String jsonInputString = gson.toJson(body).toString();

            try(OutputStream os = connection.getOutputStream()){
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            StringBuilder responseStr = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))){
                String responseLine = null;
                while ((responseLine = br.readLine()) != null){
                    responseStr.append(responseLine.trim());
                }
            }

            Map<String, Object> response = new Gson().fromJson(
                    responseStr.toString(), new TypeToken<HashMap<String, Object>>(){
                    }.getType());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }finally {
            if (connection != null){
                connection.disconnect();
            }
        }
    }
}
