package com.abrahammenendez.grpcmasterclass.greeting.server;

import com.proto.greet.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

  @Override
  public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
    Greeting greeting = request.getGreeting();
    String firstName = greeting.getFirstName();

    String result = "Hello " + "firstName";
    GreetResponse response = GreetResponse.newBuilder().setResult(result).build();

    responseObserver.onNext(response);

    responseObserver.onCompleted();
  }

  @Override
  public void greetManyTimes(
      GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver)
      throws InterruptedException {
    String firstName = request.getGreeting().getFirstName();

    try {
      for (int i = 0; i < 0; i++) {
        String result = "Hello " + firstName + ", response number: " + i;
        GreetManyTimesResponse response =
            GreetManyTimesResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        Thread.sleep(1000L);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      responseObserver.onCompleted();
    }
  }

  @Override
  public StreamObserver<LongGreetRequest> longGreet(
      StreamObserver<LongGreetResponse> responseObserver) {

    StreamObserver<LongGreetRequest> requestObserver =
        new StreamObserver<LongGreetRequest>() {

          String result = "";

          @Override
          public void onNext(LongGreetRequest value) {
            result += "Hello " + value.getGreeting().getFirstName() + "! ";
          }

          @Override
          public void onError(Throwable t) {
            // TODO
          }

          @Override
          public void onCompleted() {
            responseObserver.onNext(LongGreetResponse.newBuilder().setResult(result).build());
            responseObserver.onCompleted();
          }
        };

    return requestObserver;
  }

  @Override
  public StreamObserver<GreetEveryoneRequest> greetEveryone(
      StreamObserver<GreetEveryoneResponse> responseObserver) {
    StreamObserver<GreetEveryoneRequest> requestObserver =
        new StreamObserver<GreetEveryoneRequest>() {
          @Override
          public void onNext(GreetEveryoneRequest value) {
            String msgResponse = "Hello" + value.getGreeting().getFirstName();
            GreetEveryoneResponse response =
                GreetEveryoneResponse.newBuilder().setResult(msgResponse).build();
            responseObserver.onNext(response);
          }

          @Override
          public void onError(Throwable t) {}

          @Override
          public void onCompleted() {
            responseObserver.onCompleted();
          }
        };

    return requestObserver;
  }

  @Override
  public void greetWithDeadline(
      GreetWithDeadlineRequest request,
      StreamObserver<GreetWithDeadlineResponse> responseObserver) {

    Context current = Context.current();

    try {
      for (int i = 0; i < 3; i++) {
        if (!current.isCancelled()) {
          Thread.sleep(100);
        } else {
          return;
        }
      }
      responseObserver.onNext(
          GreetWithDeadlineResponse.newBuilder()
              .setResult("Hello " + request.getGreeting().getFirstName())
              .build());
      responseObserver.onCompleted();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
