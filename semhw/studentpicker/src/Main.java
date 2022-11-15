import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private Database db;

    static void commandHelp() {
        System.out.println("""
                1. /r - choose random student
                2. /l - list of student with grades\n
                """);
    }

    static void printHello() {
        System.out.print("\n> ");
    }

    static void printDb(Database db) {
        System.out.println(db.toString());
    }

    static void randomStudent(Database db) {
        mainloop:
        while (true) {
            try {
                String student = db.randomStudent();

                StringBuilder s = new StringBuilder();
                Scanner sc = new Scanner(System.in);
                s.append("Выбран студент: ");
                s.append(student);
                s.append("\nОн на паре? y/n");

                System.out.println(s);

                printHello();

                while (true) {
                    char c;
                    c = sc.next().charAt(0);

                    if (c == 'y') {
                        break;
                    } else if (c == 'n') {
                        db.removeStudent(student);
                        continue mainloop;
                    } else {
                        printHello();
                        break;
                    }
                }

                System.out.println("Введите оценку студента:");
                printHello();

                int grade;

                while (true) {
                    grade = sc.nextInt();

                    if (grade < 0 || grade > 10) {
                        System.out.println("Invalid grade!");
                        printHello();
                    } else {
                        break;
                    }
                }

                db.setStudentGrade(student, grade);
                break;
            } catch (OutOfStudentsException e) {
                System.out.println("No more students to grade!");
                return;
            }
        }

    }

    public static void main(String[] args) {
        Database db = new Database(List.of("Петр Иванович", "Лев Николаевич", "Иван Забрамович"));

        String command;
        Scanner sc = new Scanner(System.in);

        while (true) {
            printHello();
            command = sc.next();

            if (Objects.equals(command, "/r")) {
                randomStudent(db);
            } else if (Objects.equals(command, "/h")) {
                commandHelp();
            } else if (Objects.equals(command, "/l")) {
                printDb(db);
            } else {
                System.out.println("???");
            }
        }

    }
}