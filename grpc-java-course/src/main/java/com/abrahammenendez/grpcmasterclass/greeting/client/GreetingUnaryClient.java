package com.abrahammenendez.grpcmasterclass.greeting.client;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingUnaryClient {

  public static void main(String[] args) {
    System.out.println("Hello i'm a gRPC client");

    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

    // With server authentication SSL/TLS; custom CA root certificates; not on Android
    //        ManagedChannel secureChannel = NettyChannelBuilder.forAddress("localhost", 50051)
    //                .sslContext(GrpcSslContexts.forClient().trustManager(new
    // File("ssl/ca.crt")).build())
    //                .build();

    System.out.println("Creating stub");

    GreetServiceGrpc.GreetServiceBlockingStub greetClient =
        GreetServiceGrpc.newBlockingStub(channel);

    Greeting greeting =
        Greeting.newBuilder().setFirstName("Abraham").setLastName("Menéndez").build();

    GreetRequest greetRequest = GreetRequest.newBuilder().setGreeting(greeting).build();

    GreetResponse greetResponse = greetClient.greet(greetRequest);

    System.out.println(greetResponse.getResult());

    System.out.println("Shutting down channel");
    channel.shutdown();
  }
}
