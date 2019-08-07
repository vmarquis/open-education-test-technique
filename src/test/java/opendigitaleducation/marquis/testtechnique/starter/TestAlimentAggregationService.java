package opendigitaleducation.marquis.testtechnique.starter;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class TestAlimentAggregationService {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new AlimentAggregationService(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void get_aliment_calorie(Vertx vertx, VertxTestContext testContext) throws Throwable {
    EventBus eb = vertx.eventBus();
    eb.request("aliment.calorie.get","Champignon à la grecque",ar -> testContext.verify(() -> {{
      assertThat(ar.succeeded());
      assertThat((int)ar.result().body()==56.07);
      }
    }));
    testContext.completeNow();
  }
}
