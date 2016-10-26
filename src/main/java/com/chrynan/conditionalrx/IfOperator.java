package com.chrynan.conditionalrx;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckeenan on 10/25/16.
 */
public class IfOperator<T> implements Observable.Operator<T, T> {
    private final List<Condition<T>> conditions = new ArrayList<>();

    public IfOperator(List<Condition<T>> conditions) {
        this.conditions.addAll(conditions);
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super T> subscriber) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(T t) {
                for (final Condition<T> condition : conditions) {
                    if (condition.getCondition().call(t)) {
                        condition.getAction().call(t);
                    }
                }

                subscriber.onNext(t);
            }
        };
    }

    public IfConditionBuilder<T> elseThis(Func1<T, Boolean> condition) {
        return new IfConditionBuilder<T>(conditions, condition);
    }

    public ElseOperator.ElseConditionBuilder<T> otherwise() {
        return new ElseOperator.ElseConditionBuilder<>(conditions);
    }

    public ElseOperator<T> otherwise(Action1<T> action) {
        final ElseOperator.ElseConditionBuilder<T> conditionBuilder = new ElseOperator.ElseConditionBuilder<>(conditions);

        return conditionBuilder.then(action);
    }

    public static class IfConditionBuilder<T> {

        private final List<Condition<T>> previousConditions;
        private final Func1<T, Boolean> condition;

        public IfConditionBuilder(Func1<T, Boolean> condition) {
            this.previousConditions = new ArrayList<>();
            this.condition = condition;
        }

        public IfConditionBuilder(List<Condition<T>> previousConditions, Func1<T, Boolean> condition) {
            this.previousConditions = previousConditions;
            this.condition = condition;
        }

        public ConjunctionConditionBuilder<T> andThis(Func1<T, Boolean> conjunctionCondition) {
            return new ConjunctionConditionBuilder<>(previousConditions, condition, ConjunctionConditionBuilder.Conjunction.AND, conjunctionCondition);
        }

        public ConjunctionConditionBuilder<T> orThis(Func1<T, Boolean> conjunctionCondition) {
            return new ConjunctionConditionBuilder<>(previousConditions, condition, ConjunctionConditionBuilder.Conjunction.OR, conjunctionCondition);
        }

        public ConjunctionConditionBuilder<T> andNotThis(Func1<T, Boolean> conjunctionCondition) {
            return new ConjunctionConditionBuilder<>(previousConditions, condition, ConjunctionConditionBuilder.Conjunction.NOT_AND, conjunctionCondition);
        }

        public ConjunctionConditionBuilder<T> orNotThis(Func1<T, Boolean> conjunctionCondition) {
            return new ConjunctionConditionBuilder<>(previousConditions, condition, ConjunctionConditionBuilder.Conjunction.NOT_OR, conjunctionCondition);
        }

        public IfOperator<T> then(Action1<T> action) {
            final Condition<T> c = new Condition<>(condition, action);

            previousConditions.add(c);

            return new IfOperator<>(previousConditions);
        }
    }

    public static class ConjunctionConditionBuilder<T> {

        public enum Conjunction {
            AND,
            NOT_AND,
            OR,
            NOT_OR
        }

        private final List<Condition<T>> previousConditions;
        private final Func1<T, Boolean> comparingCondition;
        private final Conjunction conjunction;
        private final Func1<T, Boolean> conjunctionCondition;

        public ConjunctionConditionBuilder(List<Condition<T>> previousConditions, Func1<T, Boolean> condition,
                                           Conjunction conjunction, Func1<T, Boolean> conjunctionCondition) {
            this.previousConditions = previousConditions;
            this.comparingCondition = condition;
            this.conjunction = conjunction;
            this.conjunctionCondition = conjunctionCondition;
        }

        public IfOperator<T> then(Action1<T> action) {
            final Func1<T, Boolean> cc = new Func1<T, Boolean>() {

                @Override
                public Boolean call(T t) {

                    if (!previousConditions.isEmpty()) {
                        switch (conjunction) {
                            case AND:
                                return comparingCondition.call(t) && conjunctionCondition.call(t);
                            case NOT_AND:
                                return comparingCondition.call(t) && !conjunctionCondition.call(t);
                            case OR:
                                return comparingCondition.call(t) || conjunctionCondition.call(t);
                            case NOT_OR:
                                return comparingCondition.call(t) || !conjunctionCondition.call(t);
                        }
                    }

                    return false;
                }
            };

            final Condition<T> c = new Condition<>(cc, action);

            previousConditions.add(c);

            return new IfOperator<>(previousConditions);
        }
    }
}
