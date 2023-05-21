# reactive-programming-using-reactor
This project has the necessary code for the reactive programming course using Project Reactor

## Swagger-UI

-   Check the following [link](http://localhost:8080/movies/swagger-ui.html) for swagger.


## Basic classes

```
Publisher {
  public void subscribe(Subscriber<? super T> s);
}
```

```
Subscriber {
 public void onSubscribe(Subscription s);
 public void onNext(T t);
 public void onError(Throwable t);
 public void onComplete();
}
```

```
Subscription {
 public void request(long n);
 public void cancel();
}
```