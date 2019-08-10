package opendigitaleducation.marquis.testtechnique.agregateurs;

@SuppressWarnings("unused") //JSon serialisation
public class CalorieRequestDTO {
  private String searchAlimentName;
  private CalorieAgregateurType calorieAgregateurType;

  public CalorieAgregateurType getCalorieAgregateurType() {
    return calorieAgregateurType;
  }

  public void setCalorieAgregateurType(CalorieAgregateurType calorieAgregateurType) {
    this.calorieAgregateurType = calorieAgregateurType;
  }

  public String getSearchAlimentName() {
    return searchAlimentName;
  }

  public void setSearchAlimentName(String searchAlimentName) {
    this.searchAlimentName = searchAlimentName;
  }
}
