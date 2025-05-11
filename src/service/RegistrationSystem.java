
asdlhasdgasdjgkjagsdjg
import core.Linkedlist;
import core.Node;
import model.Student;
import util.Action;
import model.Course;
import java.util.Stack;

public class RegistrationSystem {
    private Linkedlist students;
    private Linkedlist courses;
    private Stack<Action> undoStack;
    private Stack<Action> redoStack;

    private Student lastStudent;
    private Course lastCourse;

    public RegistrationSystem() {
        this.students = new Linkedlist();
        this.courses = new Linkedlist();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public void addStudent(int studentID, String studentName) {
        Student student = new Student(studentID, studentName);
        if (students.findNode(studentID) == null) {
            students.appendList(student); // Append the Student object
            lastStudent = student;
            undoStack.push(new Action(studentID, studentName, 0 , null, "addStudent"));
            redoStack.clear();
            System.out.println("Student added successfully!");
            return;
        }
        System.out.println("Student already exists.");
    }
    
    public void addCourse(int courseID,String courseName) {
        Course course = new Course(courseID,courseName);
        if (courses.findNode(courseID) == null) {
            courses.appendList(course); // Append the Course object
            lastCourse = course;
            undoStack.push(new Action(0, null, courseID, courseName, "addCourse"));
            redoStack.clear();
            System.out.println("Course added successfully!");
            return;
        }
        System.out.println("Course already exists.");

    }

    public void removeStudent(int studentID, String studentName) {
        students.removeList(studentID); // Pass studentID as String
        lastStudent = null;
        if (students.findNode(studentID) == null) {
            removeEnrollment(studentID, -1);
            undoStack.push(new Action(studentID, studentName, 0 , null, "removeStudent"));
            redoStack.clear();
            return;
        }
    }

    public void removeCourse(int courseID, String courseName) {
        courses.removeList(courseID); // Pass courseID as String
        lastCourse = null;
        if (courses.findNode(courseID) == null) {
            removeEnrollment(-1, courseID);
            undoStack.push(new Action(0, null, courseID, courseName, "removeCourse"));
            redoStack.clear();
            return;
        }
    }

    public int getLastStudentAdded() {
        return lastStudent != null ? lastStudent.id : 0;
    }

    public Integer getLastCourseAdded() {
        return lastCourse != null ? lastCourse.id : null;
    }

    public void enrollStudent(int studentID, int courseID,String studentName,String courseName) {
        Student student = (Student)students.findNode(studentID);
        Course course = (Course)courses.findNode(courseID);
        if (student != null && course != null) {
            student.enrolledCourse.appendList(new Node(courseID,courseName)); // Pass courseID as String
            course.enrolledStudents.appendList(new Node(studentID,studentName)); // Pass studentID as String
            undoStack.push(new Action(student.id, student.Name,course.id,course.Name, "enroll"));
            redoStack.clear(); // Clear redo stack on new action
        }
    }

    public void removeEnrollment(int studentID, int courseID) {
        Student student = (Student)students.findNode(studentID);
        Course course = (Course)courses.findNode(courseID);
        if (courseID == -1) {
            student.enrolledCourse.removeList(-1);
        }
        if (studentID == -1) {
            course.enrolledStudents.removeList(-1);
        }
        if (student != null && course != null) {
            student.enrolledCourse.removeList(courseID); // Pass courseID as String
            course.enrolledStudents.removeList(studentID); // Pass studentID as String
            undoStack.push(new Action(student.id, student.Name,course.id,course.Name, "Remove"));
            redoStack.clear(); // Clear redo stack on new action
        }
    }

    public void listCoursesByStudent(int studentID) {
        Student student = (Student)students.findNode(studentID);
        if (student != null) {
            System.out.print("Courses enrolled by student : ");
            student.enrolledCourse.printList();
        } else {
            System.out.println("Student not found.");
        }
    }

    public void listStudentsByCourse(int courseID) {
        Course course = (Course)courses.findNode(courseID);
        if (course != null) {
            System.out.print("Students enrolled in course : ");
            course.enrolledStudents.printList();
        } else {
            System.out.println("Course not found.");
        }
    }

    public void sortStudents() {
        students.sortList();
        students.printList();
    }

    public void sortCourses() {
        courses.sortList();
        courses.printList();
    }

    public boolean isFullCourse(int courseID) {
        Course course = (Course)courses.findNode(courseID);
        if (course != null) {
            return course.enrolledStudents.list_length >= 30; // Assuming max capacity is 30
        }
        return false;
    }

    public boolean isNormalStudent(int studentID) {
        Student student = (Student)students.findNode(studentID);
        if (student != null) {
            int n = student.enrolledCourse.list_length;
            return n >= 2 && n <= 7; // Assuming max 5 courses for normal students
        }
        return false;
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Action action = undoStack.pop();
            switch (action.ActionType) {
                case "enroll":
                    removeEnrollment(action.StudentID, action.CourseID);
                    redoStack.push(new Action(action.StudentID,action.StudentName, action.CourseID,action.CourseName, "remove"));
                    break;
                case "remove":
                    enrollStudent(action.StudentID, action.CourseID, action.StudentName, action.CourseName);
                    redoStack.push(new Action(action.StudentID,action.StudentName, action.CourseID,action.CourseName,  "enroll"));
                    break;
                case "addStudent":
                    removeStudent(action.StudentID, action.StudentName);
                    redoStack.push(new Action(action.StudentID, action.StudentName, 0, null, "removeStudent"));
                    break;
                case "addCourse":
                    removeCourse(action.CourseID, action.StudentName);
                    redoStack.push(new Action(0, null, action.CourseID, action.CourseName, "removeCourse"));
                    break;
                case "removeStudent":
                    addStudent(action.StudentID, action.StudentName);
                    removeEnrollment(action.StudentID, -1);
                    redoStack.push(new Action(action.StudentID, action.StudentName, 0, null, "addStudent"));
                    break;
                case "removeCourse":
                    addCourse(action.CourseID, action.CourseName);
                    removeEnrollment(-1, action.CourseID);
                    redoStack.push(new Action(0, null, action.CourseID, action.CourseName, "addCourse"));
                    break;
                default:
                    System.out.println("Unknown action type for undo.");
            }
        } else {
            System.out.println("No actions to undo.");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Action action = redoStack.pop();
            switch (action.ActionType) {
                case "enroll":
                    enrollStudent(action.StudentID, action.CourseID, action.StudentName, action.CourseName);
                    undoStack.push(new Action(action.StudentID,action.StudentName, action.CourseID,action.CourseName,  "enroll"));
                    break;
                case "remove":
                    removeEnrollment(action.StudentID, action.CourseID);
                    undoStack.push(new Action(action.StudentID,action.StudentName, action.CourseID,action.CourseName,  "remove"));
                    break;
                case "removeStudent":
                    removeStudent(action.StudentID, action.StudentName);
                    removeEnrollment(action.StudentID, -1);
                    redoStack.push(new Action(action.StudentID, action.StudentName, 0, null, "removeStudent"));
                    break;
                case "removeCourse":
                    removeCourse(action.CourseID, action.CourseName);
                    removeEnrollment(-1, action.CourseID);
                    redoStack.push(new Action(0, null, action.CourseID, action.CourseName, "removeCourse"));
                    break;
                case "addStudent":
                    addStudent(action.StudentID, action.StudentName);
                    redoStack.push(new Action(action.StudentID, action.StudentName, 0, null, "addStudent"));
                    break;
                case "addCourse":
                    addCourse(action.CourseID, action.CourseName);
                    redoStack.push(new Action(0, null, action.CourseID, action.CourseName, "addCourse"));
                    break;
                default:
                    System.out.println("Unknown action type for redo.");
            }
        } else {
            System.out.println("No actions to redo.");
        }
    }

}
