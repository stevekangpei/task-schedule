package com.kangpei.task.worker;

import com.kangpei.task.constant.ResultStatus;
import com.kangpei.task.constant.TaskStatus;
import com.kangpei.task.constant.WorkResult;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kangpei.task.constant.TaskStatus.*;

/**
 * description: WorkWrapper
 * <p>
 * 编排任务的核心类，任务的执行参数，
 * 任务id，任务执行状态，执行结果，任务依赖项，任务链都由这个
 * 类来进行编排。<br>
 * date: 2020/8/2 6:34 下午 <br>
 * author: kangpei <br>
 * version: 1.0 <br>
 */
public class WorkWrapper<T, R> {


    /**
     * 当前任务的唯一id。
     * 可以由类自己生成，也可以由用户自己指定。
     */
    private String id;

    private String name;


    /**
     * 当前任务的执行所需要的参数。
     */
    private T param;

    /**
     * 当前任务的具体计算逻辑。里面的execute执行具体的计算
     * 接收一个T型参数，返回一个R型参数。
     */
    private ITask<T, R> worker;

    /**
     * 回调接口
     */
    private ICallback callback;


    /**
     * 当前任务的next 任务包装类。
     * 设置好之后，不允许再次更改
     */
    private final List<WorkWrapper<T, R>> nextWrapper;

    private final List<DependWorkerWrapper<T, R>> depends;

    /**
     * 当前任务的执行状态，0 表示初始化。未开始
     */
    private AtomicInteger state = new AtomicInteger(0);

    private WorkResult<R> workResult = WorkResult.defaultResult(null);

    private boolean shouldCheckNextWrapper = true;

    public boolean isShouldCheckNextWrapper() {
        return shouldCheckNextWrapper;
    }

    public void setShouldCheckNextWrapper(boolean shouldCheckNextWrapper) {
        this.shouldCheckNextWrapper = shouldCheckNextWrapper;
    }

    public int getState() {

        return this.state.get();
    }

    public boolean compareAndSet(int expect, int update) {

        return this.state.compareAndSet(expect, update);
    }

    public WorkWrapper(String id, T param, ITask<T, R> worker,
                       ICallback callback, List<WorkWrapper<T, R>> nextWrapper,
                       List<DependWorkerWrapper<T, R>> depends,
                       String name) {
        this.id = id;
        this.param = param;
        this.worker = worker;

        if (callback == null) {
            callback = new DefaultCallBack();
        }
        this.callback = callback;
        this.nextWrapper = nextWrapper;
        this.depends = depends;
        this.name = name;
    }

    public WorkWrapper(T param, ITask<T, R> worker, ICallback callback,
                       List<WorkWrapper<T, R>> nextWrapper,
                       List<DependWorkerWrapper<T, R>> depends, String name) {

        this.id = UUID.randomUUID().toString();
        this.param = param;
        this.worker = worker;
        this.callback = callback;
        this.nextWrapper = nextWrapper;
        this.depends = depends;
        this.name = name;
    }

    /**
     * 执行任务的主入口
     * <p>
     * 1， 先判断当前任务的执行时间是否足够，如果不够直接fail掉。
     * 2， 判断当前任务是否在执行中，有可能这个任务会被其他的依赖项启动了。
     *     如果这个任务不是init状态，则避免重复执行。
     * 3， 接下来判断这些任务的后序状态是否被执行了，如果当前任务没有被执行，但是后序任务执行了。
     * 则fail掉。
     * 4， 接下来看看当前任务有没有依赖项。如果没有依赖项，直接启动。
     * 5，如果有依赖项，则分开处理。
     */
    public void work(long now, long timeOut) {

        long remainTime = timeOut - now;

//        if (remainTime <= 0) {
//
//        }

        if (getState() == TaskStatus.FINISH.getCode() || getState() == TaskStatus.ERROR.getCode()) {

            beginNext();
        } else if (getState() == TaskStatus.WORKING.getCode()) {
            return;
        }

        if (shouldCheckNextWrapper) {

            if (checkNextWrapperFail()) {

                fastFail(INIT, null);
                beginNext();
            }
        }

        if (this.depends == null || this.depends.size() == 0) {
            startWork();
        }

    }

    /**
     * 开始工作
     * 1， 首先设置任务为工作状态。
     * 2， 调用回调方法。
     * 3， 开始执行任务。
     * 4， 设置任务为完成状态。
     * 5， 调用回调方法。
     * 6， 设置任务结果
     */
    private void startWork() {

        try {
            if (!compareAndSet(INIT.getCode(), TaskStatus.WORKING.getCode())) {

                fastFail(INIT, null);
                beginNext();
            }

            callback.onStart();
            R result = this.worker.execute(param);
            if (!compareAndSet(WORKING.getCode(), TaskStatus.FINISH.getCode())) {

                fastFail(ERROR, null);
                beginNext();
            }

            callback.onFinish();

            this.workResult = WorkResult.successResult(result);
        } catch (Exception e) {

            callback.onError();
            fastFail(ERROR, e);
            beginNext();
        }
    }

    private void fastFail(TaskStatus taskStatus, Exception e) {

        if (!compareAndSet(taskStatus.getCode(), TaskStatus.ERROR.getCode())) {
            return;
        }
        this.workResult = new WorkResult<R>(null, e, ResultStatus.DEFAULT);
        beginNext();

    }

    private boolean checkNextWrapperFail() {

        if (this.nextWrapper == null || this.nextWrapper.size() == 0) {
            return false;
        }

        boolean curState = this.getState() == INIT.getCode();

        Iterator<WorkWrapper<T, R>> iterator = this.nextWrapper.iterator();
        boolean next = true;

        while (iterator.hasNext()) {

            WorkWrapper<T, R> wrapper = iterator.next();
            next = next & wrapper.getState() == INIT.getCode();
        }
        return curState && next;
    }

    private void beginNext() {

        if (this.nextWrapper == null || this.nextWrapper.size() == 0) {
            return;
        }


    }
}
