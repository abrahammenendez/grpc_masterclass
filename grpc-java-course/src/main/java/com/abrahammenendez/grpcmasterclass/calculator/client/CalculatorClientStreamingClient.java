package com.abrahammenendez.grpcmasterclass.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.ComputeAverageRequest;
import com.proto.calculator.ComputeAverageResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClientStreamingClient {

  public static void main(String[] args) {
    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();

    CountDownLatch latch = new CountDownLatch(1);

    CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

    StreamObserver streamObserver =
        stub.computeAverage(
            new StreamObserver<ComputeAverageResponse>() {
              @Override
              public void onNext(ComputeAverageResponse value) {
                System.out.println("Received a response from the server");
                System.out.println(value.getAverage());
              }

              @Override
              public void onError(Throwable t) {
                // TODO
              }

              @Override
              public void onCompleted() {
                System.out.println("Server has completed sending us data");
                latch.countDown();
              }
            });

    List<Integer> numbers = List.of(1, 2, 3, 4);
    numbers.forEach(
        number -> {
          streamObserver.onNext(ComputeAverageRequest.newBuilder().setNumber(number).build());
        });

    streamObserver.onCompleted();
    try {
      latch.await(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Shutting down channel");
    channel.shutdown();
  }
}
