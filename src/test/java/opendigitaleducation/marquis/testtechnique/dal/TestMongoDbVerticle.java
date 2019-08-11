package opendigitaleducation.marquis.testtechnique.dal;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.ProjectConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
class TestMongoDbVerticle {
  private EventBus eventBus;
  @Test
  @DisplayName("Checking if the Aliment are saved in mongoDB")
  void CheckAlimentSaved(Vertx vertx, VertxTestContext testContext) {
    eventBus=vertx.eventBus();
    vertx.deployVerticle(new MongoDbVerticle(), id -> {
      ConfigRetriever retriever = ConfigRetriever.create(vertx);
      retriever.getConfig(ar -> {

        ProjectConfig projectConfig = new ProjectConfig().MapFrom(ar.result());
        MongoClient mongoClient = MongoClient.createShared(vertx, new JsonObject()
          .put("connection_string", projectConfig.MongoDbConnectionString)
          .put("db_name", projectConfig.MongoDbAlimentDb));
        eventBus.consumer("aliment.mongo.inject.saved",
          savedMessage -> mongoClient.find(projectConfig.MongoDbCollection,
          new JsonObject().put("code",1500),findTestAlimentResult ->{
            assertThat(findTestAlimentResult.succeeded()).isEqualTo(true);
            List<JsonObject> findAlimentList = findTestAlimentResult.result();
            assertThat(findAlimentList.size()).isEqualTo(1);
            findAlimentList.get(0).remove("_id");
            AlimentDTO findAliment =findAlimentList.get(0).mapTo(AlimentDTO.class);
            assertThat(findAliment.getCode()).isEqualTo(1500);
            //assertThat(findAliment.getCode()).isEqualTo(1501);  //Fail test for refactoring test
            assertThat(findAliment.getGlucides()).isEqualTo(10);
            assertThat(findAliment.getLipides()).isEqualTo(5);
            assertThat(findAliment.getName()).isEqualTo("TestName");
            assertThat(findAliment.getProteines()).isEqualTo(3);
            testContext.completeNow();
          }));
        mongoClient.removeDocuments(projectConfig.MongoDbCollection, new JsonObject().put("code",1500), cleanMongoCollectionResult ->
        {
          AlimentDTO testAliment =new AlimentDTO().setName("TestName").setGlucides(10).setLipides(5).setProteines(3).setCode(1500);
          eventBus.send("aliment.mongo.inject.bulk.save",JsonObject.mapFrom(testAliment));

        });
      });
    });
  }
  @Test
  void CheckGetAlimentByName(Vertx vertx, VertxTestContext testContext) {
    eventBus = vertx.eventBus();
    vertx.deployVerticle(new MongoDbVerticle(), id ->
    {
      EventBus eventBus = vertx.eventBus();
      eventBus.request("aliment.mongo.get.ByName", "Eau du robinet", mongoResult -> {
        if (mongoResult.succeeded()) {
          JsonObject alimentJson = ((JsonObject) mongoResult.result().body());
          alimentJson.remove("_id");
          AlimentDTO alimentDTO = alimentJson.mapTo(AlimentDTO.class);
          //assertThat(alimentDTO.getCode()).isEqualTo(18067); //Fail test
          assertThat(alimentDTO.getCode()).isEqualTo(18066);
          assertThat(alimentDTO.getProteines()).isEqualTo(0);
          assertThat(alimentDTO.getLipides()).isEqualTo(0);
          assertThat(alimentDTO.getGlucides()).isEqualTo(0);
          assertThat(alimentDTO.getName()).isEqualTo("Eau du robinet");
          testContext.completeNow();
        }
      });
    });
  }
}
