package com.hevttc.bigwork.bean;

public class Student {
    private String studentId;
    private String name;
    private int gender = 0;
    private String phone;
    private String email;
    private String password;
    private int userType;
    private String faculty;
    private String major;
    private String lastLogin;
    private String college;


    public Student(String studentId, String name, int gender, String phone, String email, String password, int userType, String faculty, String major) {
        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.faculty = faculty;
        this.major = major;
    }

    public Student(String studentId, String name, int gender, String phone, String email, String password, String lastLogin, String college) {
        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.lastLogin = lastLogin;
        this.college = college;
    }

    public Student() {
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
}
