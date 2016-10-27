# ConditionalRx

If, else-if, else, and switch condition operators for RxJava. 

These operators are like a combination of both the `filter` and `doOnNext` operators. They filter on a condition then perform a function for those objects that passed the condition. All objects are emmitted whether they passed the filter condition or not.

ConditionalRx provides another approach to handling conditions in a stream like manner by providing a builder styled `Operator` API. 

## Example:

Consider the scenario of performing an action on all objects of a stream that meet a certain condition and performing another action on all the other objects that do not meet that condition. One approach might look something like this:

```java
myObservable.doOnNext(new Action1<MyObject>() {
    @Override
    public void call(MyObject myObject) {
        if (myObject.isTrue()) {
            performFunction(myObject);
        } else {
            performOtherFunction(myObject);
        }
    }
}
```

It's usually desireable to use Lamda Expressions with RxJava to reduce the amount of code needed and to increase readability. However, when there are multiple conditions it's hard, if not impossible, to use Lambdas, so, the code begins to look convoluted. With ConditionalRx, this problem is addressed by providing stream like builder styled Operators that can be used with RxJava's `lift` method. The above code can be written as the following using ConditionalRx:

```java
myObservable.lift(Conditional.ifThis(myObject -> myObject.isTrue())
                      .then(myObject::performFunction)
                      .otherwise(myObject::performOtherFunction));
```

## Note:

This is a new experimental project and is currently not tested.

## TODO:

* Add switch statement methods
* Clean-up code
* Add more tests
* Add JavaDoc
