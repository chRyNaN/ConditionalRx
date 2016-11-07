package com.chrynan.conditionalrx;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by ckeenan on 10/25/16.
 * The base class for ConditonalRx. This class provides static methods for creating condition {@link rx.Observable.Operator}s.
 */
public class Conditional {

    /**
     * Starts the creation process for an {@link IfOperator} by passing the provided {@link Func1} representing the if-condition
     * to the {@link com.chrynan.conditionalrx.IfOperator.IfConditionBuilder} constructor. This method is equivalent to an if statement
     * where the provided {@link Func1} represents the condition to be met in order to execute the code. The code to be executed is
     * represented by an {@link rx.functions.Action1} provided to the {@link com.chrynan.conditionalrx.IfOperator.IfConditionBuilder#then(Action1)}
     * method. Note that this method returns an {@link com.chrynan.conditionalrx.IfOperator.IfConditionBuilder} where the
     * {@link com.chrynan.conditionalrx.IfOperator.IfConditionBuilder#then(Action1)} method needs to be called in order to return a
     * proper {@link rx.Observable.Operator} that can be used in {@link rx.Observable#lift(Observable.Operator)} method.
     *
     * @param condition The {@link Func1} representing the condition to be met.
     * @param <T>       The generic parameterized type.
     * @return The {@link com.chrynan.conditionalrx.IfOperator.IfConditionBuilder} used to create the {@link IfOperator}.
     */
    public static <T> IfOperator.IfConditionBuilder<T> ifThis(Func1<T, Boolean> condition) {
        return new IfOperator.IfConditionBuilder<>(condition);
    }

    public static <T, R> SwitchOperator<T, R> switchOn(Func1<T, R> switchStatementCondition) {
        return new SwitchOperator<>(switchStatementCondition);
    }

    private Conditional() {
        // Private default constructor
    }

}
