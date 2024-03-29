package hacker;

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

            URL url = new URL("https://stepik.org/media/attachments/lesson/255258/passwords.txt");
            Scanner sc = new Scanner(url.openStream(), StandardCharsets.UTF_8);

            while (sc.hasNextLine()) {
                String pass = sc.nextLine();
                List<String> variants = getVariants(pass);
                for (String variant : variants) {
                    output.writeUTF(variant);
                    String receivedMsg = input.readUTF();
                    if (Objects.equals(receivedMsg, "Connection success!")) {
                        System.out.println(variant);
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
