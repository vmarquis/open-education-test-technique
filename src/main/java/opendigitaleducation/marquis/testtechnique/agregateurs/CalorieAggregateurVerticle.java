package opendigitaleducation.marquis.testtechnique.agregateurs;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.parsetools.JsonParser;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;

public class CalorieAggregateurVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    EventBus eb = vertx.eventBus();

    eb.consumer("aliment.calorie.get", message -> {
      Buffer buff = Buffer.buffer(1000);
      JsonParser parser = JsonParser.newParser();

      parser.handler(event -> {
        // Start the object
        parser.objectValueMode();
        switch (event.type()) {
          case START_OBJECT:
            // Set object value mode to handle each entry, from now on the parser won't emit start object events

            break;
          case VALUE:
            // Handle each object
            // Get the field in which this object was parsed
            if (event.fieldName().equals("alim_nom_fr") && event.value() == message.body())
            {
              AlimentDTO alimentDTO= event.objectValue().mapTo(AlimentDTO.class);
              message.reply(CalculateCalorie(alimentDTO));
            }
            break;
          case END_OBJECT:
            // Set the object event mode so the parser emits start/end object events again
            parser.objectEventMode();
            break;
        }
      });

      parser.handle(buff);


      OpenOptions options = new OpenOptions();
      options.setRead(true);

      vertx.fileSystem().open("ressources/alim.json", options, res -> {
        if (res.succeeded()) {
          AsyncFile file = res.result();


          for (int i = 0; i < 10; i++) {
            file.read(buff, i * 100, i * 100, 100, ar -> {
              if (ar.succeeded()) {

              } else {
                parser.end();
              }
            });
          }

          file.endHandler((r) -> System.out.println("Copy done"));
        } else {
          System.out.println("Open failed");
            System.out.println(res.cause().getMessage());
        }
      });


    });
    startPromise.complete();

  }

  private double CalculateCalorie(AlimentDTO alimentDTO) {
    return 4*alimentDTO.getGlucides()+4*alimentDTO.getProteines()+4*alimentDTO.getLipides();
  }
}
