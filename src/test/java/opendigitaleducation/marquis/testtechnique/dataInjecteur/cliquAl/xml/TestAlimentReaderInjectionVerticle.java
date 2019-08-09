package opendigitaleducation.marquis.testtechnique.dataInjecteur.cliquAl.xml;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.dataInjecteur.cliquAL.xml.AlimentReaderInjectionVerticle;
import opendigitaleducation.marquis.testtechnique.dataInjecteur.InjectionOptionDTO;
import opendigitaleducation.marquis.testtechnique.dataInjecteur.InjectionSourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing CliqAl Alimentation injection service Open Data XML")
@ExtendWith(VertxExtension.class)
class TestAlimentReaderInjectionVerticle {
  private EventBus eventBus;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new AlimentReaderInjectionVerticle(), testContext.completing());
    eventBus = vertx.eventBus();
  }

  @Test
  @DisplayName("Checking CliquAlInjectionVerticle send aliment JSON")
  void CheckEventSend(VertxTestContext testContext) {
    InjectionOptionDTO injectionOptionDTO = new InjectionOptionDTO();
    injectionOptionDTO.setInjectionSource(InjectionSourceType.CiquALXml);
    injectionOptionDTO.setCliqAlAlimFile("alim_2017 11 21.xml");
    injectionOptionDTO.setCliqAlDirectory("ressources");

    eventBus.send("aliment.data.inject.CiquALXml.start", JsonObject.mapFrom(injectionOptionDTO));
    eventBus.consumer("aliment.data.inject.CiquALXml.SetComposition", message -> {
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

