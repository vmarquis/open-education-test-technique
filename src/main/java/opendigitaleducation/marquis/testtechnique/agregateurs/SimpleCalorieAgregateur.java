package opendigitaleducation.marquis.testtechnique.agregateurs;

import opendigitaleducation.marquis.testtechnique.AlimentDTO;

public class SimpleCalorieAgregateur implements CalorieAgregateur {
  @Override
  public double AgregeCalorie(AlimentDTO alimentDTO) {
    return alimentDTO.getGlucides()*4+alimentDTO.getProteines()*4+alimentDTO.getLipides()*9;
  }
}
