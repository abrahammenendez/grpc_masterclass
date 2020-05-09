package com.abrahammenendez.grpcmasterclass.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimerNumberDecompositionRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorServerStreamingClient {

  public static void main(String[] args) {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();

    CalculatorServiceGrpc.CalculatorServiceBlockingStub stub =
        CalculatorServiceGrpc.newBlockingStub(channel);

    Integer number = 533123448;

    stub.primeNumberDecomposition(
            PrimerNumberDecompositionRequest.newBuilder().setNumber(number).build())
        .forEachRemaining(
            response -> {
              System.out.println(response.getPrimeFactor());
            });

    channel.shutdown();
  }
}
