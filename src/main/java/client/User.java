package client;

import server.object.LabWork;

import java.io.*;
import java.net.*;
import java.util.*;

public class User {

    private SendObject sendObject;
    private String host;
    private int port;

    private static DatagramSocket socket;

    private DatagramPacket sendingPacket;

    private DatagramPacket receivingPacket;

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private byte[] receivingDataBuffer;

    private static List<String> files = new ArrayList<>();
    private static List<String> adders = new ArrayList<>();
    public User(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void sendMessage(String message) throws ClassNotFoundException, IOException, InterruptedException {
        socket.setSoTimeout(5000);
        InetAddress address = InetAddress.getByName(this.host);
        socket.connect(address, port);
        receivingDataBuffer = new byte[5000];
        byte[] data = message.trim().getBytes();
        // Создайте UDP-пакет
        sendingPacket = new DatagramPacket(data, data.length, address, port);
        // Отправьте UDP-пакет серверу
        try {
            socket.send(sendingPacket);
        } catch (SocketException e) {
            System.out.println("Server do not respond");
        }
        // Создайте UDP-пакет
        receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        //Получите ответ от сервера
        try {
            socket.receive(receivingPacket);
        } catch (SocketException e) {
            System.out.println("Server do not respond(");
//            socket.close();
        }

        if (message.equals("show")) {
            if (SerializationManager.deserialize(receivingPacket.getData()).toString().trim().equals("[]"))
                    System.out.println("Коллекция пустая!");
            else
                System.out.println(SerializationManager.deserialize(receivingPacket.getData()).toString().trim());
        } else {
            String receivedData = new String(receivingPacket.getData());
            System.out.println(receivedData.trim());
        }

    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        User sender = new User("localhost", 50026);
        sender.setSocket(new DatagramSocket());
        BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
        adders.add("add"); adders.add("add_if_max"); adders.add("remove_lower");
        adders.add("remove_greater");adders.add("update");
        boolean flag = false;

        while (!flag) {
            System.out.println("Enter command: ");
            String message = b.readLine().trim();
            String[] arr = message.split(" ",2);

            if (!message.isEmpty()) {
                if (message.equals("exit")) {
                    System.exit(0);
                } else if (adders.contains(arr[0])) {
                    sender.sendObject = new SendObject(sender.getLabs());
                    sender.sendMessage(message);
                    sender.sendLabWorkObject(reader);
                } else if (arr[0].equals("execute_script")) {
                        sender.executeScript(arr[1]);
                        files.clear();
                } else if (message.equals("save")) {
                    System.out.println("Command save is not available!");
                } else {
                    try {
                        sender.sendMessage(message);
                    } catch (ClassNotFoundException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        b.close();
        // Закрыть соединение
        sender.getSocket().close();
    }

    private void sendLabWorkObject(BufferedReader b) throws IOException {
        sendObject.start(b);

        InetAddress address = InetAddress.getByName(this.host);
        socket.connect(address, port);
        receivingDataBuffer = new byte[5000];
        byte[] data = SerializationManager.serialize(sendObject.getLabWork());
        // Создайте UDP-пакет
        sendingPacket = new DatagramPacket(data, data.length, address, port);
        // Отправьте UDP-пакет серверу
        socket.send(sendingPacket);

        receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        //Получите ответ от сервера
        try {
            socket.receive(receivingPacket);
        } catch (SocketException e) {
            System.out.println("Server do not respond(");
        }
        String receivedData = new String(receivingPacket.getData());
        System.out.println(receivedData.trim());
    }

    private LinkedList<LabWork> getLabs() throws IOException {

        InetAddress address = InetAddress.getByName(this.host);
        socket.connect(address, port);
        receivingDataBuffer = new byte[5000];
        byte[] data = "show".getBytes();
        // Создайте UDP-пакет
        sendingPacket = new DatagramPacket(data, data.length, address, port);
        // Отправьте UDP-пакет серверу
        socket.send(sendingPacket);
        // Создайте UDP-пакет
        receivingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        try {
            socket.receive(receivingPacket);
        } catch (PortUnreachableException e) {
            System.out.println("Server do not respond!");
        }
        try {
            if (SerializationManager.deserialize(receivingPacket.getData()) == null)
                return null;
            else {
                LinkedList<LabWork> labs = SerializationManager.deserialize(receivingPacket.getData());
                return labs;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeScript(String pathToFile) throws IOException, ClassNotFoundException, InterruptedException {
        boolean flag = true;
        int count = 1;
        if (new File(pathToFile).exists()) {
            while (flag) {
                // настраиваем поток ввода
                BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(pathToFile)));
                if (count == 0) {
                    flag = false;
                }

                if (flag == false){
                    break;
                }
                count--;

                String command = buff.readLine();
                while (command != null) {
                    String[] ex = command.split(" ", 2);
                    if (ex[0].equals("execute_script")) {
                        executeScriptOutSide(ex[1]);
                        files.clear();
                    }
                    else if (adders.contains(ex[0])){
                        sendObject = new SendObject(getLabs());
                        sendMessage(command);
                        sendLabWorkObject(buff);
                    } else {
                        sendMessage(command);
                    }

                    command = buff.readLine();
                }
            }
        } else {
            sendMessage("No");
        }
    }

    public void executeScriptOutSide(String pathToFile) throws IOException, ClassNotFoundException, InterruptedException {
        boolean flag = true;
        if (new File(pathToFile).exists()) {
            while (flag) {
                // настраиваем поток ввода
                BufferedReader buff = new BufferedReader(new InputStreamReader(new FileInputStream(pathToFile)));
                if (files.contains(pathToFile)) {
                    flag = false;
                }

                if (flag == false){
                    break;
                }

                files.add(pathToFile);

                String command = buff.readLine();
                while (command != null) {
                    String[] ex = command.split(" ", 2);
                    if (ex[0].equals("execute_script")) {
                        executeScriptOutSide(ex[1]);
                    }
                    else if (adders.contains(ex[0])){
                        sendObject = new SendObject(getLabs());
                        sendMessage(command);
                        sendLabWorkObject(buff);
                    } else {
                        sendMessage(command);
                    }

                    command = buff.readLine();
                }
            }
        } else {
            sendMessage("No");
        }
    }


    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

}


