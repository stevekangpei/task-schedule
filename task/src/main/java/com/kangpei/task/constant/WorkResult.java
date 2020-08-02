package com.kangpei.task.constant;

/**
 * description: WorkResult
 * 任务执行结果类<br>
 * date: 2020/8/2 6:49 下午 <br>
 * author: kangpei <br>
 * version: 1.0 <br>
 */
public class WorkResult<R> {


    private R result;

    private Exception exception;

    private ResultStatus rs;


    public WorkResult(R result, Exception exception, ResultStatus rs) {
        this.result = result;
        this.exception = exception;
        this.rs = rs;
    }

    public static <R> WorkResult<R> defaultResult(R result) {

        return new WorkResult<>(result, null, ResultStatus.DEFAULT);
    }

    public static <R> WorkResult<R> errorResult(Exception e) {

        return new WorkResult<>(null, e, ResultStatus.ERROR);
    }

    public static <R> WorkResult<R> successResult(R result) {

        return new WorkResult<>(result, null, ResultStatus.SUCCESS);
    }


}
