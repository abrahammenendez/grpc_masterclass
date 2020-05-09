package com.abrahammenendez.grpcmasterclass.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

  private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
  private MongoDatabase database = mongoClient.getDatabase("grpc-java-course");
  private MongoCollection<Document> collection = database.getCollection("blog");

  @Override
  public void createBlog(
      CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {

    System.out.println("Received Create Blog Request");

    Blog blog = request.getBlog();

    Document doc =
        new Document("author_id", blog.getAuthorId())
            .append("title", blog.getTitle())
            .append("content", blog.getContent());

    System.out.println("Inserting blog...");
    collection.insertOne(doc);

    String id = doc.getObjectId("_id").toString();
    System.out.println("Inserted blog " + id);

    CreateBlogResponse response =
        CreateBlogResponse.newBuilder().setBlog(blog.toBuilder().setId(id).build()).build();

    responseObserver.onNext(response);

    responseObserver.onCompleted();
  }

  @Override
  public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {

    System.out.println("Searching for a blog");

    String blogId = request.getBlogId();

    Document result = null;

    try {
      result = collection.find(eq("_id", new ObjectId(blogId))).first();
    } catch (Exception e) {
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with the corresponding " + "id was not found")
              .augmentDescription(e.getLocalizedMessage())
              .asRuntimeException());
    }

    if (result == null) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with the corresponding " + "id was not found")
              .asRuntimeException());
    } else {
      System.out.println("Blog found, sending response");
      Blog blog = documentToBlog(result);

      responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());

      responseObserver.onCompleted();
    }
  }

  @Override
  public void updateBlog(
      UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
    System.out.println("Received Update Blog Request");

    Blog blog = request.getBlog();

    String blogId = blog.getId();

    Document result = null;

    try {
      result = collection.find(eq("_id", new ObjectId(blogId))).first();
    } catch (Exception e) {
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with the corresponding " + "id was not found")
              .augmentDescription(e.getLocalizedMessage())
              .asRuntimeException());
    }

    if (result == null) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with the corresponding " + "id was not found")
              .asRuntimeException());
    } else {
      Document replacement =
          new Document("author_id", blog.getAuthorId())
              .append("title", blog.getTitle())
              .append("content", blog.getContent());

      System.out.println("Replacing blog in database...");

      collection.replaceOne(eq("_id", result.getObjectId("_id")), replacement);

      responseObserver.onNext(
          UpdateBlogResponse.newBuilder().setBlog(documentToBlog(result)).build());

      responseObserver.onCompleted();
    }
  }

  @Override
  public void deleteBlog(
      DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {
    System.out.println("Received Delete Blog Request");

    String blogId = request.getBlogId();
    DeleteResult result = null;
    try {
      result = collection.deleteOne(eq("_id", new ObjectId(blogId)));
    } catch (Exception e) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with the corresponding id was not found")
              .augmentDescription(e.getLocalizedMessage())
              .asRuntimeException());
    }

    if (result.getDeletedCount() == 0) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("The blog with the corresponding id was not found")
              .asRuntimeException());
    } else {
      System.out.println("Blog was deleted");
      responseObserver.onNext(DeleteBlogResponse.newBuilder().setBlogId(blogId).build());

      responseObserver.onCompleted();
    }
  }

  @Override
  public void listBlog(ListBlogRequest request, StreamObserver<ListBlogResponse> responseObserver) {
    System.out.println("Received List Blog Request");

    collection
        .find()
        .iterator()
        .forEachRemaining(
            document ->
                responseObserver.onNext(
                    ListBlogResponse.newBuilder().setBlog(documentToBlog(document)).build()));

    responseObserver.onCompleted();
  }

  private Blog documentToBlog(Document result) {
    return Blog.newBuilder()
        .setAuthorId(result.getString("author_id"))
        .setTitle(result.getString("title"))
        .setContent(result.getString("content"))
        .setId(result.getObjectId("_id").toString())
        .build();
  }
}
