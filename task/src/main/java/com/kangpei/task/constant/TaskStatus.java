package com.kangpei.task.constant;

/**
 * description: TaskStatus <br>
 * date: 2020/8/2 6:46 下午 <br>
 * author: kangpei <br>
 * version: 1.0 <br>
 */
public enum  TaskStatus {

    INIT("INIT", 0),
    WORKING("WORKING", 1),
    FINISH("FINISH", 2),
    ERROR("ERROR", 3);


    private String status;

    private int code;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    TaskStatus(String status, int code) {
        this.status = status;
        this.code = code;
    }
}
