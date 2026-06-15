package org.example.studentcoursesystem.entity;

public class Student {
    private int sid;
    private String sname;
    private String gender;
    private String major;

    // 构造方法
    public Student() {}

    public Student(int sid, String sname, String gender, String major) {
        this.sid = sid;
        this.sname = sname;
        this.gender = gender;
        this.major = major;
    }

    // Getter 和 Setter
    public int getSid() { return sid; }
    public void setSid(int sid) { this.sid = sid; }

    public String getSname() { return sname; }
    public void setSname(String sname) { this.sname = sname; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    @Override
    public String toString() {
        return "Student{" +
                "sid=" + sid +
                ", sname='" + sname + '\'' +
                ", gender='" + gender + '\'' +
                ", major='" + major + '\'' +
                '}';
    }
}