package opendigitaleducation.marquis.testtechnique.injecteur.cliquAl.xml;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.injecteur.cliquAL.xml.AlimentReaderVerticle;
import opendigitaleducation.marquis.testtechnique.injecteur.InjectionOptionDTO;
import opendigitaleducation.marquis.testtechnique.injecteur.InjectionSourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing CliqAl Alimentation injection service Open Data XML")
@ExtendWith(VertxExtension.class)
class TestAlimentReaderVerticle {
  private EventBus eventBus;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new AlimentReaderVerticle(), testContext.completing());
    eventBus = vertx.eventBus();
  }

  @Test
  @DisplayName("Checking StartInject CliquAL (reading XML aliments)")
  void CheckStartInjectCliquAlXML(VertxTestContext testContext) {
    InjectionOptionDTO injectionOptionDTO = new InjectionOptionDTO();
    injectionOptionDTO.setInjectionSource(InjectionSourceType.CiquALXml);
    eventBus.send("aliment.data.inject.CiquALXml.start", JsonObject.mapFrom(injectionOptionDTO));
    eventBus.consumer("aliment.data.inject.CiquALXml.setComposition", message -> {
      AlimentDTO alimentToSave = ((JsonObject) message.body()).mapTo(AlimentDTO.class);
      if (alimentToSave.getCode() == 18066) //Eau du robinet
      {
//        assertThat(alimentToSave.getName()).isEqualTo("Eau du robnet"); //Fail test
        assertThat(alimentToSave.getName()).isEqualTo("Eau du robinet");
        testContext.completeNow();
      }
    });
  }
}

