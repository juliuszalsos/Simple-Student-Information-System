package info;
import java.io.*;
import java.util.*;


public class csvbridge {
    private static final String FILE_PATH = "students.csv";
    public static String toCSV(Student s) {

        return s.getStudentID() + "," + s.getStudentfirstName() + "," + s.getStudentlastName()+ "," + s.getStudentProgramCode() + "," + s.getStudentYearLevel() + "," + s.getStudentGender()+ "," + s.getStudentYearLevel()+ "," + s.getStudentGender();
    }
    public static void createStudents(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Student s : students) {
                writer.write(toCSV(s));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public static List<Student> ReadandListStudents() {
    List<Student> students = new ArrayList<>();
    File file = new File(FILE_PATH);

    if (!file.exists()) {
        System.out.println("No database file found. Starting fresh.");
        return students;
    }

    System.out.println("\n--- Loading & Displaying Student Records ---");

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            if (data.length >= 2) {
                Student s = new Student(data[0], data[1], data[2], data[3], data[4], data[5]); 
                students.add(s);

                System.out.println("ID: " + s.getStudentID() + " | Name: " + s.getStudentfirstName() + " " + s.getStudentlastName());
            }
        }
    } catch (IOException e) {
        System.err.println("Error processing data: " + e.getMessage());
    }

    System.out.println("Total records loaded: " + students.size() + "\n");
    return students;
}

    public static void UpdateandDeleteStudent(List<Student> students, String targetID, boolean isDelete) {
    Student foundStudent = null;
    for (Student s : students) {
        if (s.getStudentID().equals(targetID)) {
            foundStudent = s;
            break;
        }
    }

    if (foundStudent == null) {
        System.out.println("Student with ID " + targetID + " not found.");
        return;
    }

    if (isDelete) {
        students.remove(foundStudent);
        System.out.println("Student removed successfully.");
    } else {
        System.out.println("Updating details for: " + foundStudent.getStudentfirstName() + foundStudent.getStudentlastName() + foundStudent.getStudentProgramCode() + foundStudent.getStudentYearLevel() + foundStudent.getStudentGender());
        foundStudent.setStudentfirstName("Updated First Name");
        foundStudent.setStudentlastName("Updated Last Name");
        foundStudent.setStudentProgramCode("Updated Program Code");
        foundStudent.setStudentYearLevel("Updated Year Level");
        foundStudent.setStudentGender("Updated Gender");
        System.out.println("Student updated successfully.");
    }
    createStudents(students); 
}


}