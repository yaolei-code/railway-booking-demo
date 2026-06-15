package org.example.studentcoursesystem.entity;

public class Teacher {
    private int tid;
    private String tname;
    private String title;

    // 构造方法
    public Teacher() {}

    public Teacher(int tid, String tname, String title) {
        this.tid = tid;
        this.tname = tname;
        this.title = title;
    }

    // Getter 和 Setter
    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // toString 方法，便于打印调试
    @Override
    public String toString() {
        return "Teacher{" +
                "tid=" + tid +
                ", tname='" + tname + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}