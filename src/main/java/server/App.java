package server;

import server.databaseManager.ConnectionManager;
import server.databaseManager.LabWorksDatabaseManager;
import server.manager.Controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * server.App запускающий класс, содержащий метод {@link #main(String[])}
 */

public class App {

    private static String getRandomString() {
        int l = 6;
        String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
        StringBuilder s = new StringBuilder(l);
        int i;
        for (i = 0; i < l; i++) {
            int ch = (int) (AlphaNumericStr.length() * Math.random());
            s.append(AlphaNumericStr.charAt(ch));
        }
        return s.toString();
    }

    /**
     * Главный  метод, который запускает {@link Controller#start()}
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException, NoSuchAlgorithmException {

//        BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
//        MessageDigest md = MessageDigest.getInstance("MD2");
//        System.out.println("Введите имя пользователя");
//        String user = b.readLine().trim();
//        System.out.println("Введите пароль");
//        String password = b.readLine().trim();
//        String salt = getRandomString();
//        String pepper = "#63Rq*9Oxx!";
//
//        byte[] hash = md.digest((pepper+password+salt).getBytes("UTF-8"));

        String file;

        try{
            file = args[0];
        } catch (IndexOutOfBoundsException e) {
            file = "notes.json";
        }

        Controller controller = new Controller(file);
        controller.start();
    }
}
