package opendigitaleducation.marquis.testtechnique.injecteur;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.ProjectConfig;
import opendigitaleducation.marquis.testtechnique.injecteur.cliquAL.xml.AlimentReaderVerticle;
import opendigitaleducation.marquis.testtechnique.injecteur.cliquAL.xml.CompositionReaderVerticle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing CliqAl Alimentation injection service Open Data XML")
@ExtendWith(VertxExtension.class)
class TestIntegrationInjection {
  private EventBus eventBus;
  private MongoClient mongoClient;
  private ProjectConfig projectConfig;

  @Test
  @Timeout(value = 2, timeUnit = TimeUnit.MINUTES)
  @DisplayName("Checking StartInject CliquAL (reading XML aliments)")
  void TestCalcXmlDataInject(Vertx vertx, VertxTestContext testContext) {
    eventBus = vertx.eventBus();
    vertx.deployVerticle(new AlimentInjecteurVerticle(),
      dep1 -> vertx.deployVerticle(new AlimentReaderVerticle(),
        dep2 -> vertx.deployVerticle(CompositionReaderVerticle.class, new DeploymentOptions().setInstances(4), dep3 -> {
      ConfigRetriever retriever = ConfigRetriever.create(vertx);
      retriever.getConfig(ar -> {
        projectConfig = new ProjectConfig().MapFrom(ar.result());
        mongoClient = MongoClient.createShared(vertx, new JsonObject()
          .put("connection_string", projectConfig.MongoDbConnectionString)
          .put("db_name", projectConfig.MongoDbAlimentDb));
        mongoClient.removeDocuments(projectConfig.MongoDbCollection, new JsonObject(), cleanMongoCollectionResult ->
          testCalcXmlDataInject(testContext));
      });
    })));
  }

  private void testCalcXmlDataInject(VertxTestContext testContext) {
    InjectionOptionDTO injectionOptionDTO = new InjectionOptionDTO();
    injectionOptionDTO.setInjectionSource(InjectionSourceType.CiquALXml);
    mongoClient.find(projectConfig.MongoDbCollection, new JsonObject(), findAll ->
    {
      assertThat(findAll.succeeded()).isEqualTo(true);
      //assertThat(findAll.result().size()).isEqualTo(1);  //Fail test
      assertThat(findAll.result().size()).isEqualTo(0);

      eventBus.send("aliment.data.inject.start", JsonObject.mapFrom(injectionOptionDTO));
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
    });
  }
}

