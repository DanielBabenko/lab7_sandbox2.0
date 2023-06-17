package admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Admin {
    private static final String password = "Burevestnik";
    private static DatagramSocket admin;

    static {
        try {
            admin = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {

        if (checkAccess())
            while (true) {
                System.out.println("Enter command:");
                String message = reader.readLine();
                if (message.equals("save"))
                    sendCommandToServer(message);
                if (message.equals("exit")) {
                    sendCommandToServer(message);
                    break;
                }
            }

        admin.close();
    }

    private static void sendCommandToServer(String message) throws IOException {
        byte[] receivingDataBuffer = new byte[1024];
        byte[] sendingDataBuffer;

        sendingDataBuffer = message.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, InetAddress.getByName("localhost"), 50026);
        admin.send(sendPacket);

        DatagramPacket receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        admin.receive(receivingPacket);

        String dataFromServer = new String(receivingPacket.getData());
        System.out.println("Sent from server: " + dataFromServer.trim());
    }

    private static boolean checkAccess() throws IOException {
        int i = 1;

        while (true) {
            System.out.println("ADMIN PANEL\nplease enter the password:");
            String pass = reader.readLine();
            if (pass.equals(password))
                return true;
            if (i == 3)
                break;
            i += 1;
        }

        return false;
    }
}
