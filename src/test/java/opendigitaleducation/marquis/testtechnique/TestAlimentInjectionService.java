package opendigitaleducation.marquis.testtechnique;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import opendigitaleducation.marquis.testtechnique.Injecteur.AlimentInjectionService;
import opendigitaleducation.marquis.testtechnique.Injecteur.InjectionOptionDTO;
import opendigitaleducation.marquis.testtechnique.Injecteur.InjectionSourceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing Alimentation injection service Open Data XML")
@ExtendWith(VertxExtension.class)
class TestAlimentInjectionService {

  @Test
  @DisplayName("Cheking if the Aliment Injection service send message for inject ClicAli data XML")
  void CheckEventSend(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new AlimentInjectionService(), id -> {
      EventBus eventBus = vertx.eventBus();
      InjectionOptionDTO injectionOptionDTO = new InjectionOptionDTO();
      injectionOptionDTO.InjectionSource = InjectionSourceType.CiquALXml;
      eventBus.send("aliment.data.inject.start", JsonObject.mapFrom(injectionOptionDTO));
      eventBus.consumer("aliment.data.inject.CiquALXml.start", message -> {
        assertThat(message.body().toString()).as("les parametres d'injection de donnée ont été modifiés").isEqualTo(JsonObject.mapFrom(injectionOptionDTO).toString());
        testContext.completeNow();
      });
    });
  }

}
