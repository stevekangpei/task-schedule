package com.kangpei.task.worker;

/**
 * description: ITask <br>
 * date: 2020/8/2 6:00 下午 <br>
 * author: kangpei <br>
 * version: 1.0 <br>
 */
@FunctionalInterface
public interface ITask<T, R> {
    
    
    R work(T t);
    
}
