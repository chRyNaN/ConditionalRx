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
public class ConditionalIfTest {

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

        assertTrue("singleConditionShouldWork()", 1);
    }

    @Test
    public void objectShouldPropogate() {
        resetVariables();

        // Single condition to check if same object is available in both the condition and the action
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(25);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count = 25;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("objectShouldPropogate()", 25);
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

        assertTrue("elseIfConditionShouldWork()", 0);
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

        assertTrue("elseConditionShouldWork()", -4);
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

        assertTrue("multipleConditionsShouldWork()", 8);
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

        assertTrue("elseConditionShouldNotBeReached()", 6);
    }

    @Test
    public void singleConditionAndShouldWork() {
        resetVariables();

        // Verify a true and statement works
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(3);
            }
        }).andThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer < 4;
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("singleConditionAndShouldWork()", 1);

        resetVariables();

        // Verify a false and statement does not work
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(3);
            }
        }).andThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer > 4;
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("singleConditionAndShouldWork()", 0);
    }

    @Test
    public void singleConditionNotAndShouldWork() {
        resetVariables();

        // Verify a true not and statement works
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(25);
            }
        }).andNotThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(12);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("singleConditionNotAndShouldWork()", 1);

        resetVariables();

        // Verify a false not and statement works
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(3);
            }
        }).andNotThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(3);
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("singleConditionNotAndShouldWork()", 0);
    }

    @Test
    public void singleConditionOrShouldWork() {
        resetVariables();

        // Verify a true or statement works
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(3);
            }
        }).orThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer < 5;
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("singleConditionOrShouldWork()", 2);

        resetVariables();

        // Verify a true statement but a false or statement works
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(3);
            }
        }).orThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer < 3;
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("singleConditionOrShouldWork()", 1);
    }

    @Test
    public void singleConditionNotOrShouldWork() {
        resetVariables();

        // Verify a true not or statement works
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(3);
            }
        }).orNotThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer > 3;
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("singleConditionNotOrShouldWork()", 1);

        resetVariables();

        // Verify a true statement but a false or statement works
        observable.lift(Conditional.ifThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer.equals(12);
            }
        }).orNotThis(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer < 100;
            }
        }).then(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                count++;
            }
        })).subscribe(testSubscriber);

        assertCodeExecutedSuccessfully();

        assertTrue("singleConditionNotOrShouldWork()", 1);
    }

    private void resetVariables() {
        // Reset count variable
        count = 0;

        // Helps test Observables and their results
        testSubscriber = new TestSubscriber<>();

        // Observerable used to test
        observable = Observable.from(testValues);
    }

    private void assertCodeExecutedSuccessfully() {
        // Make assertions
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(testValues.size());
        testSubscriber.assertReceivedOnNext(testValues);
    }

    private void assertTrue(String methodName, int valueCountShouldBe) {
        Assert.assertTrue("Count field value in " + methodName + " method in ConditionalIfTest class is not correct " +
                        "after performing test. Count value should be = " + valueCountShouldBe + " but it is = " + count,
                count == valueCountShouldBe);
    }
}
