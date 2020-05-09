package com.abrahammenendez.grpcmasterclass.greeting.client;

import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClientStreamingClient {

  public static void main(String[] args) {
    System.out.println("Hello i'm a gRPC client");

    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

    System.out.println("Creating stub");

    CountDownLatch latch = new CountDownLatch(1);

    GreetServiceGrpc.GreetServiceStub greetClient = GreetServiceGrpc.newStub(channel);

    Greeting greeting =
        Greeting.newBuilder().setFirstName("Abraham").setLastName("Menéndez").build();

    GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

    StreamObserver requestObserver =
        greetClient.longGreet(
            new StreamObserver<LongGreetResponse>() {
              @Override
              public void onNext(LongGreetResponse value) {
                System.out.println("Received a response from the server.");
                System.out.println(value.getResult());
              }

              @Override
              public void onError(Throwable t) {
                // TODO
              }

              @Override
              public void onCompleted() {
                System.out.println("Server has completed sending us something");
                latch.countDown();
              }
            });

    List<String> names = List.of("Abraham", "Andrés", "Juan");
    names.forEach(
        name -> {
          requestObserver.onNext(
              LongGreetRequest.newBuilder()
                  .setGreeting(Greeting.newBuilder().setFirstName(name).build())
                  .build());
        });

    requestObserver.onCompleted();

    try {
      latch.await(3L, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Shutting down channel");
    channel.shutdown();
  }
}
