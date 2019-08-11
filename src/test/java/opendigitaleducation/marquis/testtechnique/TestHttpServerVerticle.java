package opendigitaleducation.marquis.testtechnique;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
class TestHttpServerVerticle {
  private MongoClient mongoClient;
  private ProjectConfig projectConfig;
  private Vertx vertx;

  @Test
  @Timeout(value = 1, timeUnit = TimeUnit.MINUTES)
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) {
      this.vertx=vertx;
       vertx.deployVerticle(new HttpServerVerticle(), dep1 ->
       {
         ConfigRetriever retriever = ConfigRetriever.create(vertx);
         retriever.getConfig(ar -> {
           projectConfig = new ProjectConfig().MapFrom(ar.result());
           mongoClient = MongoClient.createShared(vertx, new JsonObject()
             .put("connection_string", projectConfig.MongoDbConnectionString)
             .put("db_name", projectConfig.MongoDbAlimentDb));
           AlimentDTO alimentDTO = new AlimentDTO();
           alimentDTO.setCode(10).setGlucides(1).setLipides(1).setProteines(1).setName("Emmental ou emmenthal");
           //noinspection CodeBlock2Expr For better code understand
           mongoClient.replaceDocumentsWithOptions(projectConfig.MongoDbCollection,
             new JsonObject().put("code", alimentDTO.getCode()),
             JsonObject.mapFrom(alimentDTO),
             new UpdateOptions().setUpsert(true),
             cleanMongoCollectionResult ->
             {
               TestHttpServer(testContext);
             }
           );
         });
       });
  }

  private void TestHttpServer(VertxTestContext testContext) {
    HttpClientOptions options = new HttpClientOptions().setDefaultHost("localhost").setDefaultPort(8080);
    HttpClient client = vertx.createHttpClient(options);
    //noinspection deprecation Pas grave pour un test technique
    client.getNow("/Emmental%20ou%20emmenthal", response -> response.bodyHandler(
      httpResponseBuffer -> {
        double calories=Double.parseDouble(httpResponseBuffer.toString().replace(",","."));
//          assertThat(calories).isEqualTo(384.5); //Fail test
        assertThat(calories).isEqualTo(384.6);
        testContext.completeNow();
      }
    ));
  }
  //
  }

