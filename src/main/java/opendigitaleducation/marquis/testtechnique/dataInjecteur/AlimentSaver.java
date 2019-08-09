package opendigitaleducation.marquis.testtechnique.dataInjecteur;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.UpdateOptions;
import opendigitaleducation.marquis.testtechnique.ProjectConfig;

public class AlimentSaver implements EventBusStarter {
  @Override
  public void StartEventBusConsumer(Vertx vertx, Promise<Object> eventBusStartpromise) {
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(ar -> {
      ProjectConfig projectConfig = new ProjectConfig().MapFrom(ar.result());
      consumeSaveAlimentMessage(vertx, projectConfig);
      eventBusStartpromise.complete();
    });
  }

  private void consumeSaveAlimentMessage(Vertx vertx, ProjectConfig projectConfig) {
    EventBus eventBus = vertx.eventBus();
    eventBus.consumer("aliment.data.inject.save", saveMessage -> {
      MongoClient mongoClient = MongoClient.createShared(vertx, new JsonObject()
        .put("connection_string", projectConfig.MongoDbConnectionString)
        .put("db_name", projectConfig.MongoDbAlimentDb));
      JsonObject alimentJson = ((JsonObject) saveMessage.body());
      mongoClient.replaceDocumentsWithOptions(projectConfig.MongoDbCollection, new JsonObject()
          .put("code", alimentJson.getInteger("code")),
        alimentJson,
        new UpdateOptions().setMulti(false).setUpsert(true).setReturningNewDocument(false),
        res -> eventBus.send("aliment.data.inject.saved", alimentJson.getInteger("code")));
    });
  }
}
