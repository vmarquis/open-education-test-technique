package opendigitaleducation.marquis.testtechnique;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import opendigitaleducation.marquis.testtechnique.agregateurs.CalorieAggregateurVerticle;
import opendigitaleducation.marquis.testtechnique.agregateurs.CalorieAgregateurType;
import opendigitaleducation.marquis.testtechnique.agregateurs.CalorieRequestDTO;
import opendigitaleducation.marquis.testtechnique.dal.MongoDbVerticle;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HttpServerVerticle extends AbstractVerticle {
  private EventBus eventBus;

  @Override
  public void start(Promise<Void> startPromise) {
    eventBus = vertx.eventBus();
    HttpServer server = vertx.createHttpServer();
    server.requestHandler(request -> {
      try {
        String aliment = URLDecoder.decode(request.path().substring(1), StandardCharsets.UTF_8.toString());
        CalorieRequestDTO calorieRequestDTO=new CalorieRequestDTO();
        calorieRequestDTO.setSearchedAlimentName(aliment).setCalorieAgregateurType(CalorieAgregateurType.Simple);
        eventBus.request("aliment.calorie.get.ByName", JsonObject.mapFrom(calorieRequestDTO), getCalorieReponseMessage -> {
          double calorie = (double) getCalorieReponseMessage.result().body();
          request.response().end(String.format("%f", calorie));
        });
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    });
    DeployNeededVerticle(server);
    startPromise.complete();
  }

  private void DeployNeededVerticle(HttpServer server) {
    vertx.deployVerticle(new CalorieAggregateurVerticle(),
      dep1 -> vertx.deployVerticle(new MongoDbVerticle(),
        dep2 ->
          server.listen(8080, res -> {
            if (res.succeeded()) {
              System.out.println("Server is now listening!");
            } else {
              System.out.println("Failed to bind!");
            }
          }
          )
      )
    );
  }
}
