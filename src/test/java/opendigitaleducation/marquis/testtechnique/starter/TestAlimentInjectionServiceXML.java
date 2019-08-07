package opendigitaleducation.marquis.testtechnique.starter;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testing Alimentation injection service Open Data XML")
@ExtendWith(VertxExtension.class)
public class TestAlimentInjectionServiceXML {



  @Test
  @DisplayName("Get Aliment DTO from name")
  void get_aliment_data(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.deployVerticle(new AlimentInjectionServiceXml(), id->
    {
      EventBus eventBus = vertx.eventBus();
      eventBus.request("aliment.data.xmlInjecteur.get", "Champignon à la grecque", busReply -> testContext.verify(() -> {
        {
          assertThat(busReply.succeeded()).as("bus reply").isTrue();
          AlimentDTO champigonData=(AlimentDTO) busReply.result().body();
          assertThat(champigonData.getName()).isEqualTo("Champignon à la grecque");
          assertThat(champigonData.getGlucides()).as("%s's proteine", champigonData.getName()).isEqualTo(3.95);
          assertThat(champigonData.getLipides()).as("%s's lipides", champigonData.getName()).isEqualTo(3.55);
          assertThat(champigonData.getProteines()).as("%s's glucides", champigonData.getName()).isEqualTo(2.07);
          testContext.completeNow();
        }
      }));
    });
  }
}
