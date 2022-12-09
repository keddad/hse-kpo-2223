package org.templater;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("What path would it be?");

        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();

        Templater templater = new Templater(path);
        try {
            templater.process();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}