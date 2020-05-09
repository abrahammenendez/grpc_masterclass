package com.abrahammenendez.grpcmasterclass.greeting.client;

import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingBidiStreamingClient {

  public static void main(String[] args) {
    System.out.println("Hello i'm a gRPC client");

    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

    System.out.println("Creating stub");

    GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<GreetEveryoneRequest> requestObserver =
        asyncClient.greetEveryone(
            new StreamObserver<GreetEveryoneResponse>() {
              @Override
              public void onNext(GreetEveryoneResponse value) {
                System.out.println("Response from server: " + value.getResult());
              }

              @Override
              public void onError(Throwable t) {
                latch.countDown();
              }

              @Override
              public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
              }
            });

    Arrays.asList("Stephane", "John", "Marc", "Patricia")
        .forEach(
            name -> {
              System.out.println("Sending: " + name);
              requestObserver.onNext(
                  GreetEveryoneRequest.newBuilder()
                      .setGreeting(Greeting.newBuilder().setFirstName(name))
                      .build());
              try {
                Thread.sleep(100);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });

    requestObserver.onCompleted();

    try {
      latch.await(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
