package org.example.studentcoursesystem.entity;

public class SC {
    private int sid;          // 学生ID
    private String cid;       // 课程ID
    private String semester;  // 学期

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return "SC{" +
                "sid=" + sid +
                ", cid='" + cid + '\'' +
                ", semester='" + semester + '\'' +
                '}';
    }
}