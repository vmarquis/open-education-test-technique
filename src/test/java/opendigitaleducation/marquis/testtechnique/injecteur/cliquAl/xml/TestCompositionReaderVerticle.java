package opendigitaleducation.marquis.testtechnique.injecteur.cliquAl.xml;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.injecteur.cliquAL.xml.CompositionReaderVerticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing CliqAl composition reader service Open Data XML")
@ExtendWith(VertxExtension.class)
class TestCompositionReaderVerticle {
  private EventBus eventBus;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new CompositionReaderVerticle(), testContext.completing());
    eventBus = vertx.eventBus();
  }

  @Test
  @DisplayName("Checking  Set Composition (reading CliquAl Xml) composition")
  @Timeout(value = 1, timeUnit = TimeUnit.MINUTES)
  void CheckSetComposition(VertxTestContext testContext) {
    AlimentDTO alimentDTO = new AlimentDTO();
    alimentDTO.setCode(18066);
    eventBus.send("aliment.data.inject.CiquALXml.setComposition", JsonObject.mapFrom(alimentDTO));
    eventBus.consumer("aliment.mongo.inject.bulk.save", messageToSave -> {
      AlimentDTO alimentToSave = ((JsonObject) messageToSave.body()).mapTo(AlimentDTO.class);
      assertThat(alimentToSave.getCode()).isEqualTo(18066);
      //assertThat(alimentToSave.getGlucides()).isEqualTo(1); //Fail test
      assertThat(alimentToSave.getGlucides()).isEqualTo(0);
      assertThat(alimentToSave.getLipides()).isEqualTo(0);
      assertThat(alimentToSave.getProteines()).isEqualTo(0);
      testContext.completeNow();
    });
  }
}

