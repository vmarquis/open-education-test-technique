package opendigitaleducation.marquis.testtechnique.injecteur.cliquAL.xml;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.RecordParser;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.ProjectConfig;

public class AlimentReaderVerticle extends AbstractVerticle {
  private ProjectConfig projectConfig;
  private EventBus eventBus;

  @Override
  public void start(Promise<Void> startPromise) {
    eventBus = vertx.eventBus();
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(ar -> {
      projectConfig = new ProjectConfig().MapFrom(ar.result());
      StartEventBusConsummer();
    });
    startPromise.complete();
  }

  private void StartEventBusConsummer() {
    eventBus.consumer("aliment.data.inject.CiquALXml.start", message ->
      vertx.fileSystem().open(projectConfig.CliqAlDirectory +"/"+ projectConfig.CliqAlAlimFile,
      new OpenOptions(), result -> {
        AsyncFile file = result.result();
        file.exceptionHandler(ex -> {
          if (ex.getMessage() != null)
            System.out.println("file exceptionHandler AlimentReader" + ex.getMessage());
        });          RecordParser parser= RecordParser.newDelimited("</ALIM>", parsedBufer->{
          AlimentDTO alimentDTO= new AlimentDTO();
          String XML =parsedBufer.toString();
          alimentDTO.setCode(Integer.parseInt(getXMLValue(XML,"<alim_code>","</alim_code>")));
          alimentDTO.setName(getXMLValue(XML,"<alim_nom_fr>","</alim_nom_fr>"));
          eventBus.send("aliment.data.inject.CiquALXml.setComposition",JsonObject.mapFrom(alimentDTO));
        } );
        file.handler(parser)
          .endHandler(v -> file.close());
      }));
  }

  private static String getXMLValue(String buf, String startBalise, String endBalise) {
    int start=buf.indexOf(startBalise)+startBalise.length();
    int end=buf.indexOf(endBalise);
    return buf.substring(start,end).trim();
  }
}
