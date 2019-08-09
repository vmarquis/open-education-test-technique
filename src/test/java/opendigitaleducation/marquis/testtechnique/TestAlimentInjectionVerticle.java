package opendigitaleducation.marquis.testtechnique;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import opendigitaleducation.marquis.testtechnique.dataInjecteur.AlimentInjectionVerticle;
import opendigitaleducation.marquis.testtechnique.dataInjecteur.InjectionOptionDTO;
import opendigitaleducation.marquis.testtechnique.dataInjecteur.InjectionSourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing Alimentation injection service")
@ExtendWith(VertxExtension.class)
class TestAlimentInjectionVerticle {
  private EventBus eventBus;
  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new AlimentInjectionVerticle(), testContext.completing());
    eventBus = vertx.eventBus();
  }

  @Test
  @DisplayName("Checking if the Aliment Injection service send message for inject ClicAli data XML")
  void CheckEventSend(VertxTestContext testContext) {
    InjectionOptionDTO injectionOptionDTO = new InjectionOptionDTO();
    injectionOptionDTO.setInjectionSource(InjectionSourceType.CiquALXml);
    eventBus.send("aliment.data.inject.start", JsonObject.mapFrom(injectionOptionDTO));
    eventBus.consumer("aliment.data.inject.CiquALXml.start", message -> {
      //injectionOptionDTO.InjectionSource=null;  //To fail test if refactoring it
      assertThat(message.body().toString()).as("les parametres d'injection de données ont été modifiés").isEqualTo(JsonObject.mapFrom(injectionOptionDTO).toString());
      testContext.completeNow();
    });
  }

  @Test
  @DisplayName("Cheking if the Aliment are saved in mongoDB")
  void CheckAlimentSaved(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new AlimentInjectionVerticle(), id -> {
      ConfigRetriever retriever = ConfigRetriever.create(vertx);
      retriever.getConfig(ar -> {

        ProjectConfig projectConfig = new ProjectConfig().MapFrom(ar.result());
        MongoClient mongoClient = MongoClient.createShared(vertx, new JsonObject()
          .put("connection_string", projectConfig.MongoDbConnectionString)
          .put("db_name", projectConfig.MongoDbAlimentDb));
        eventBus.consumer("aliment.data.inject.saved", savedMessage -> {
          assertThat((int)savedMessage.body()).as("Le code aliment retourné par l'event bus est incorrect").isEqualTo(1500);
          mongoClient.find(projectConfig.MongoDbCollection,
            new JsonObject().put("code",1500),findTestAlimentResult ->{
            assertThat(findTestAlimentResult.succeeded()).isEqualTo(true);
              List<JsonObject> findAlimentList = findTestAlimentResult.result();
              assertThat(findAlimentList.size()).isEqualTo(1);
              AlimentDTO findAliment =findAlimentList.get(0).mapTo(AlimentDTO.class);
              assertThat(findAliment.getCode()).isEqualTo(1500);
              //assertThat(findAliment.getCode()).isEqualTo(1501);  //Fail test for refactoring test
              assertThat(findAliment.getGlucides()).isEqualTo(10);
              assertThat(findAliment.getLipides()).isEqualTo(5);
              assertThat(findAliment.getName()).isEqualTo("TestName");
              assertThat(findAliment.getProteines()).isEqualTo(3);
              testContext.completeNow();
            });
        } );
        mongoClient.removeDocuments(projectConfig.MongoDbCollection, new JsonObject(), cleanMongoCollectionResult ->
        {
          AlimentDTO testAliment =new AlimentDTO().setName("TestName").setGlucides(10).setLipides(5).setProteines(3).setCode(1500);
          eventBus.send("aliment.data.inject.save",JsonObject.mapFrom(testAliment));

        });
      });
    });
    }
  }

