package opendigitaleducation.marquis.testtechnique.injecteur;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing Alimentation injecteur service")
@ExtendWith(VertxExtension.class)
class TestAlimentInjecteursStarterVertice {
  private EventBus eventBus;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new AlimentInjecteursStarterVertice(), testContext.completing());
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
}

