import java.io.*;
import java.util.*;

class DataManager {
    private static final String DATA_FILE = "students.dat";
    private Map<String, Student> students;

    public DataManager() {
        students = new HashMap<>();
        loadData();
    }

    public void addStudent(Student student) {
        students.put(student.getId(), student);
        saveData();
    }

    public void updateStudent(Student student) {
        students.put(student.getId(), student);
        saveData();
    }

    public void deleteStudent(String id) {
        students.remove(id);
        saveData();
    }

    public Student getStudent(String id) {
        return students.get(id);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

    public boolean studentExists(String id) {
        return students.containsKey(id);
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(students);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                students = (Map<String, Student>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading data: " + e.getMessage());
                students = new HashMap<>();
            }
        }
    }
}