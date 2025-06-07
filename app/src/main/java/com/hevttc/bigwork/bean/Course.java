package com.hevttc.bigwork.bean;

public class Course {
    private int id;          // 数据库自增ID
    private String name;    // 课程名称
    private String time;    // 时间（如 "周一 08:00-09:40"）
    private String room;    // 教室
    private String teacher; // 教师（可选）
    private int weekDay;
    private String startTime;// 开始时间（格式：HH:mm）
    private String endTime;  // 结束时间（格式：HH:mm）

    public Course(String name,String time,String room){
        this.name = name;
        this.time = time;
        this.room = room;
    }

    public Course(int id, String name, String time, String room, String teacher, int weekDay, String startTime, String endTime) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.room = room;
        this.teacher = teacher;
        this.weekDay = weekDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public Course(String name, int weekDay, String startTime, String endTime, String room) {
        this.name = name;
        this.weekDay = weekDay;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    public Course() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    // 辅助方法：获取格式化后的时间段（如 "08:00-09:40"）
    public String getTimeRange() {
        return startTime + "-" + endTime;
    }

    // 辅助方法：获取中文星期表示（如 "周一"）
    public String getWeekDayChinese() {
        switch (weekDay) {
            case 1: return "周一";
            case 2: return "周二";
            case 3: return "周三";
            case 4: return "周四";
            case 5: return "周五";
            case 6: return "周六";
            case 7: return "周日";
            default: return "";
        }
    }

}
