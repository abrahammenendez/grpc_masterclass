package com.abrahammenendez.grpcmasterclass.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.SquareRootRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class CalculatorSquareRootClient {

  public static void main(String[] args) {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();

    CalculatorServiceGrpc.CalculatorServiceBlockingStub stub =
        CalculatorServiceGrpc.newBlockingStub(channel);

    int number = -1;

    try {
      stub.squareRoot(SquareRootRequest.newBuilder().setNumber(number).build());
    } catch (StatusRuntimeException e) {
      System.out.println("Got an exception for square root!");
      e.printStackTrace();
    }

    channel.shutdown();
  }
}
