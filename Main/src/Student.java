import java.io.Serializable;
import java.util.*;

class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String email;
    private String phone;
    private String course;
    private Map<String, Double> grades;
    private Map<String, Boolean> attendance;

    public Student(String id, String name, String email, String phone, String course) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.course = course;
        this.grades = new HashMap<>();
        this.attendance = new HashMap<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCourse() { return course; }
    public Map<String, Double> getGrades() { return grades; }
    public Map<String, Boolean> getAttendance() { return attendance; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCourse(String course) { this.course = course; }

    public void addGrade(String subject, double grade) {
        grades.put(subject, grade);
    }

    public void markAttendance(String date, boolean present) {
        attendance.put(date, present);
    }

    public double getAverageGrade() {
        if (grades.isEmpty()) return 0.0;
        return grades.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public double getAttendancePercentage() {
        if (attendance.isEmpty()) return 0.0;
        long present = attendance.values().stream().filter(b -> b).count();
        return (present * 100.0) / attendance.size();
    }
}