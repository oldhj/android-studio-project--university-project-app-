package com.hevttc.bigwork.bean;

public class Seat {
    private String id;
    private int row;
    private int column;
    private boolean reserved;
    private long reservationTime;
    private boolean userOwned; // 是否是当前用户的座位

    public Seat(String id, int row, int column, boolean reserved, long reservationTime) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.reserved = reserved;
        this.reservationTime = reservationTime;
    }

    public long getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(long reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Seat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public boolean isUserOwned() { return userOwned; }

    public void setUserOwned(boolean userOwned) { this.userOwned = userOwned; }
}
