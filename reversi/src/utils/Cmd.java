package utils;

import field.Coordinates;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Cmd {
    public static String getUserOption(List<String> options) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String userInput = sc.nextLine().trim();

            if (options.contains(userInput)) {
                return userInput;
            }
        }
    }

    public static Coordinates getCoordinates() {
        Scanner sc = new Scanner(System.in);

        while (true){
            try {
                return new Coordinates(sc.nextInt(), sc.nextInt());
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static Integer getInt() {
        Scanner sc = new Scanner(System.in);

        while (true){
            try {
                return sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
