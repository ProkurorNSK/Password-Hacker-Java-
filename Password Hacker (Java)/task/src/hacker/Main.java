package hacker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        try (Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

        String receivedMsg;
        char[] pass = new char[1];
        pass[0] = getFirstToken();

        while (true) {
            output.writeUTF(new String(pass));
            receivedMsg = input.readUTF();
            if (Objects.equals(receivedMsg, "Connection success!")) {
                System.out.println(pass);
                break;
            }

            for (int i = pass.length - 1; i >= 0; i--) {
                char temp = pass[i];
                pass[i] = getNextToken(pass[i]);
                if (!isLastToken(temp)) {
                    break;
                } else if (i == 0) {
                    pass = Arrays.copyOf(pass, pass.length + 1);
                    pass[pass.length - 1] = getFirstToken();
                }
            }
        }


        } catch (IOException ignored) {
        }
    }

    static boolean isLastToken(char token) {
        return token == '9';
    }

    static char getFirstToken() {
        return 'a';
    }

    static char getNextToken(char token) {
        switch (token) {
            case 'z' -> token = '0';
            case '9' -> token = 'a';
            default -> token++;
        }
        return token;
    }
}
