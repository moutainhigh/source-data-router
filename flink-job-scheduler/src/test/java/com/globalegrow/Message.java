package com.globalegrow;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class Message implements Delayed{

    private int id;
    private String body;  //消息内容
    private long excuteTime;//执行时间

    private long inTime = System.currentTimeMillis();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getExcuteTime() {
        return excuteTime;
    }

    public long getInTime() {
        return inTime;
    }

    public void setInTime(long inTime) {
        this.inTime = inTime;
    }

    public void setExcuteTime(long excuteTime) {
        this.excuteTime = excuteTime;
    }

    public Message(int id, String body,long delayTime) {
        this.id = id;
        this.body = body;
        this.excuteTime = TimeUnit.NANOSECONDS.convert(delayTime, TimeUnit.MILLISECONDS) + System.nanoTime();
    }

    @Override
    public int compareTo(Delayed delayed) {
        Message msg = (Message)delayed;
        return Integer.valueOf(this.id)>Integer.valueOf(msg.id)?1:( Integer.valueOf(this.id)<Integer.valueOf(msg.id)?-1:0);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return  unit.convert(this.excuteTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

}
