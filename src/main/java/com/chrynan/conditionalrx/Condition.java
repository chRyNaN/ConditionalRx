package com.chrynan.conditionalrx;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by ckeenan on 10/25/16.
 * The class holding both the {@link Func1} representing the condition to be met and the {@link Action1} representing the action
 * to be performed if that condition is met.
 */
public class Condition<T> {

    private final Func1<T, Boolean> condition;
    private final Action1<T> action;

    public Condition(Func1<T, Boolean> condition, Action1<T> action) {
        this.condition = condition;
        this.action = action;
    }

    /**
     * Retrieves the {@link Func1} representing the condition to be met.
     *
     * @return The {@link Func1} representing the condition to be met.
     */
    public Func1<T, Boolean> getCondition() {
        return condition;
    }

    /**
     * Retrieves the {@link Action1} representing the action to be performed if the {@link Func1} condition is met.
     *
     * @return The {@link Action1} representing the action to be performed if the {@link Func1} condition is met.
     */
    public Action1<T> getAction() {
        return action;
    }
}
