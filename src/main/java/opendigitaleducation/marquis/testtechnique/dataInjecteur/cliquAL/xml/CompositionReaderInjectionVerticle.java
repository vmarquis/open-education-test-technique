package opendigitaleducation.marquis.testtechnique.dataInjecteur.cliquAL.xml;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.RecordParser;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.dataInjecteur.InjectionOptionDTO;

public class CompositionReaderInjectionVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    EventBus eb = vertx.eventBus();

    eb.consumer("aliment.data.inject.CiquALXml.setComposition", message -> {
      JsonObject messageJson = ((JsonObject) message.body());
      InjectionOptionDTO injectionOptionDTO = messageJson.getJsonObject("injectionOptionDTO").mapTo(InjectionOptionDTO.class);
      AlimentDTO alimentDTO = messageJson.getJsonObject("alimentDTO").mapTo(AlimentDTO.class);

      vertx.fileSystem().open(injectionOptionDTO.getCliqAlDirectory() + "/" + injectionOptionDTO.getCliqAlCompoFile(),
        new OpenOptions(), result -> {
          AsyncFile file = result.result();
          RecordParser parser = RecordParser.newDelimited("</COMPO>", parsedBufer -> {
            fillAlimentDTOCompositionFromXml(alimentDTO, parsedBufer);
            if (alimentDTO.getGlucides() != -1 && alimentDTO.getLipides() != -1 && alimentDTO.getProteines() != -1) {
              eb.send("aliment.data.inject.save", JsonObject.mapFrom(alimentDTO));
              file.end();
            }
          });
          file.endHandler(v -> file.close());
          file.handler(parser);
        });
    });
    startPromise.complete();
  }

  private void fillAlimentDTOCompositionFromXml(AlimentDTO alimentDTO, Buffer parsedBufer) {
    try {
      String XML = parsedBufer.toString();
      int constCode = Integer.parseInt(getXMLValue(XML, "<const_code>", "</const_code>"));
      int alimCode = Integer.parseInt(getXMLValue(XML, "<alim_code>", "</alim_code>"));
      if (alimCode == alimentDTO.getCode()) {
        switch (constCode) {
          case 25000:
            alimentDTO.setProteines(getTeneur(XML));
            break;
          case 31000:
            alimentDTO.setGlucides(getTeneur(XML));
            break;
          case 40000:
            alimentDTO.setLipides(getTeneur(XML));
            break;
        }
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
 }

  private double getTeneur(String XML) {
    return Double.parseDouble(getXMLValue(XML, "<teneur>", "</teneur>").replace(",", "."));
  }

  private static String getXMLValue(String buf, String startBalise, String endBalise) {
    int start = buf.indexOf(startBalise) + startBalise.length();
    int end = buf.indexOf(endBalise);
    return buf.substring(start, end).trim();

  }
}
