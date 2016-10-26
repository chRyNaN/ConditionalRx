package com.chrynan.conditionalrx;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckeenan on 10/26/16.
 */
public class ConditionalTest {

    private Observable<Integer> observable;
    private TestSubscriber<Integer> testSubscriber;
    private List<Integer> testValues;
    private int count;

    @Before
    public void init() {
        // Setup new test values
        testValues = new ArrayList<>();

        testValues.add(5);
        testValues.add(12);
        testValues.add(25);
        testValues.add(3);
        testValues.add(68);
        testValues.add(4);
    }

    @Test
    public void singleConditionShouldWork() {
        resetVariables();

        // Single condition
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(25);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        Assert.assertTrue("Count does not equal 1 for a single value condition.", count == 1);
    }

    @Test
    public void elseIfConditionShouldWork() {
        resetVariables();

        // If-else-if condition
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(5);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        }).elseThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(4);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count--;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        Assert.assertTrue("Count does not equal 0 for an else-if condition.", count == 0);
    }

    @Test
    public void elseConditionShouldWork() {
        resetVariables();

        // If-else condition
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(68);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        }).otherwise(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count--;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        Assert.assertTrue("Count does not equal -4 for an else condition.", count == -4);
    }

    @Test
    public void multipleConditionsShouldWork() {
        resetVariables();

        // Multiple conditions
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(12);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        }).elseThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(68);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count--;
            }
        }).otherwise(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count = count + 2;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        Assert.assertTrue("Count does not equal 8 for multiple conditions.", count == 8);
    }

    @Test
    public void elseConditionShouldNotBeReached() {
        resetVariables();

        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return testValues.contains(integer);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        }).otherwise(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count--;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        Assert.assertTrue("Count does not equal 6 for where else condition shouldn't be reached.", count == 6);
    }

    @Test
    public void singleConditionAndShouldWork() {
        resetVariables();

    }

    private void assertCodeExecutedSuccessfully() {
        // Make assertions
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(testValues.size());
        testSubscriber.assertReceivedOnNext(testValues);
    }

    private void resetVariables() {
        // Reset count variable
        count = 0;

        // Helps test Observables and their results
        testSubscriber = new TestSubscriber<>();

        // Observerable used to test
        observable = Observable.from(testValues);
    }
}
