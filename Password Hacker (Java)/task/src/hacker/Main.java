package hacker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        try (Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {

            output.writeUTF(args[2]);
            String receivedMsg = input.readUTF();
            System.out.println(receivedMsg);

        } catch (IOException ignored) {
        }
    }
}
