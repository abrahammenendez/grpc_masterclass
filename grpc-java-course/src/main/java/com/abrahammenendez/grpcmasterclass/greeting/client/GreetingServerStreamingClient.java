package com.abrahammenendez.grpcmasterclass.greeting.client;

import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingServerStreamingClient {

  public static void main(String[] args) {
    System.out.println("Hello i'm a gRPC client");

    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

    System.out.println("Creating stub");

    GreetServiceGrpc.GreetServiceBlockingStub greetClient =
        GreetServiceGrpc.newBlockingStub(channel);

    Greeting greeting =
        Greeting.newBuilder().setFirstName("Abraham").setLastName("Menéndez").build();

    GreetManyTimesRequest greetRequest =
        GreetManyTimesRequest.newBuilder().setGreeting(greeting).build();

    greetClient
        .greetManyTimes(greetRequest)
        .forEachRemaining(
            response -> {
              System.out.println(response.getResult());
            });

    System.out.println("Shutting down channel");
    channel.shutdown();
  }
}
