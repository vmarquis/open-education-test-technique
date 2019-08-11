package opendigitaleducation.marquis.testtechnique;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing CliqAl Alimentation injection service Open Data XML")
@ExtendWith(VertxExtension.class)
class TestMainInjecteurLauncher {
  private EventBus eventBus;
  private MongoClient mongoClient;
  private ProjectConfig projectConfig;

  @Test
  @Timeout(value = 2, timeUnit = TimeUnit.MINUTES)
  @DisplayName("Checking StartInject CliquAL (reading XML aliments)")
  void TestCalcXmlDataInject(Vertx vertx, VertxTestContext testContext) {
    eventBus = vertx.eventBus();

    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(ar -> {
      projectConfig = new ProjectConfig().MapFrom(ar.result());
      mongoClient = MongoClient.createShared(vertx, new JsonObject()
        .put("connection_string", projectConfig.MongoDbConnectionString)
        .put("db_name", projectConfig.MongoDbAlimentDb));
      mongoClient.removeDocuments(projectConfig.MongoDbCollection, new JsonObject(), cleanMongoCollectionResult ->
        mongoClient.find(projectConfig.MongoDbCollection,new JsonObject(),FoundDocumentResult->{
        assertThat(FoundDocumentResult.result().size()).isEqualTo(0);
        //assertThat(FoundDocumentResult.result()).size().isEqualTo(1);// Test Fail
          testCalcXmlDataInject(testContext);
          vertx.deployVerticle(new MainInjecteurLaucher());
      }));
    });
  }

  private void testCalcXmlDataInject(VertxTestContext testContext) {
    eventBus.consumer("aliment.mongo.inject.saved", savedMessage -> {
      System.out.println("Saved " + (int) savedMessage.body());
      int testedCode = 18066;
      mongoClient.find(projectConfig.MongoDbCollection,
        new JsonObject().put("code", testedCode), findTestAlimentResult -> {
          assertThat(findTestAlimentResult.succeeded()).isEqualTo(true);
          List<JsonObject> findAlimentList = findTestAlimentResult.result();
          if (findAlimentList.size() == 1) {
            JsonObject jsonAliment = findAlimentList.get(0);
            jsonAliment.remove("_id");
            AlimentDTO findAliment = jsonAliment.mapTo(AlimentDTO.class);
            assertThat(findAliment.getCode()).isEqualTo(testedCode);
            assertThat(findAliment.getGlucides()).isEqualTo(0);
            //assertThat(findAliment.getLipides()).isEqualTo(1); //Fail test
            assertThat(findAliment.getLipides()).isEqualTo(0); //Fail test
            assertThat(findAliment.getName()).isEqualTo("Eau du robinet");
            assertThat(findAliment.getProteines()).isEqualTo(0);
            testContext.completeNow();
          }
        });
    });
  }
}

