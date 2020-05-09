package com.abrahammenendez.grpcmasterclass.greeting.client;

import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.GreetWithDeadlineRequest;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class GreetingDeadlineClient {

  public static void main(String[] args) {
    System.out.println("Hello i'm a gRPC client");

    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

    System.out.println("Creating stub");

    GreetServiceGrpc.GreetServiceBlockingStub greetClient =
        GreetServiceGrpc.newBlockingStub(channel);

    try {
      greetClient
          .withDeadlineAfter(100l, TimeUnit.MILLISECONDS)
          .greetWithDeadline(
              GreetWithDeadlineRequest.newBuilder()
                  .setGreeting(
                      Greeting.newBuilder().setFirstName("Stephane").getDefaultInstanceForType())
                  .build());
    } catch (StatusRuntimeException e) {
      if (e.getStatus() == Status.DEADLINE_EXCEEDED) {
        System.out.println("Deadline has been exceded");
      } else {
        e.printStackTrace();
      }
    }

    System.out.println("Shutting down channel");
    channel.shutdown();
  }
}
