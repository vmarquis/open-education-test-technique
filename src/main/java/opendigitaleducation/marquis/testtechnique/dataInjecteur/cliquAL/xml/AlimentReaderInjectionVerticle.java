package opendigitaleducation.marquis.testtechnique.dataInjecteur.cliquAL.xml;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.RecordParser;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.dataInjecteur.InjectionOptionDTO;

public class AlimentReaderInjectionVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    EventBus eb = vertx.eventBus();
    eb.consumer("aliment.data.inject.CiquALXml.start", message -> {
      InjectionOptionDTO injectionOptionDTO = ((JsonObject) message.body()).mapTo(InjectionOptionDTO.class);


      vertx.fileSystem().open(injectionOptionDTO.getCliqAlDirectory() +"/"+ injectionOptionDTO.getCliqAlAlimFile(),
        new OpenOptions(), result -> {
          AsyncFile file = result.result();
          RecordParser parser= RecordParser.newDelimited("</ALIM>", parsedBufer->{
            AlimentDTO alimentDTO= new AlimentDTO();
            String XML =parsedBufer.toString();
            alimentDTO.setCode(Integer.parseInt(getXMLValue(XML,"<alim_code>","</alim_code>")));
            alimentDTO.setName(getXMLValue(XML,"<alim_nom_fr>","</alim_nom_fr>"));
            eb.send("aliment.data.inject.CiquALXml.setComposition",JsonObject.mapFrom(alimentDTO));
          } );

          file.handler(parser)
            .endHandler(v -> file.close());
        });
    });
    startPromise.complete();
  }

  private static String getXMLValue(String buf, String startBalise, String endBalise) {
    int start=buf.indexOf(startBalise)+startBalise.length();
    int end=buf.indexOf(endBalise);
    return buf.substring(start,end).trim();

  }
}
