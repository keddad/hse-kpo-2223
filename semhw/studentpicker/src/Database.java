import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

class Student {
    private String name;
    private int grade;

    public Student(String n) {
        name = n;
        grade = -1;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", grade=" + grade +
                '}';
    }
}

public class Database {
    private List<Student> data = new ArrayList<>();

    public Database(List<String> s) {
        s.forEach(it -> {
            data.add(new Student(it));
        });
    }

    public String randomStudent() throws OutOfStudentsException {
        Optional<Student> s = data.stream().filter(it -> it.getGrade() == -1).sorted((o1, o2) -> ThreadLocalRandom.current().nextBoolean() ? -1 : 1).findFirst();

        if (s.isEmpty()) {
            throw new OutOfStudentsException();
        }

        return s.get().getName();
    }

    public void removeStudent(String name) {
        data.removeIf(it -> it.getName().equals(name));
    }

    public void setStudentGrade(String name, int grade) {
        Student s = data.stream().filter(it -> it.getName().equals(name)).findAny().get();
        s.setGrade(grade);
    }

    @Override
    public String toString() {
        return "Database{" +
                "data=" + data.stream().filter(it -> it.getGrade() != -1).toList() +
                '}';
    }
}
