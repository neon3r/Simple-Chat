package SimpleChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage (String message) {
        System.out.println(message);
    }

    public static String readString() {
        String str = null;
        try {
            str = reader.readLine();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            str = readString();
        }
        return str;
    }

    public static int readInt() {
        int num = 0;
        try {
            num = Integer.parseInt(readString());
        } catch (NumberFormatException ex) {
            System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            num = readInt();
        }
        return num;
    }
}