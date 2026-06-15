package org.example.studentcoursesystem.entity;

public class Course {
    private String cid;
    private String cname;
    private int credit;

    // Getter 和 Setter 方法
    public String getCid() { return cid; }
    public void setCid(String cid) { this.cid = cid; }

    public String getCname() { return cname; }
    public void setCname(String cname) { this.cname = cname; }

    public int getCredit() { return credit; }
    public void setCredit(int credit) { this.credit = credit; }
}