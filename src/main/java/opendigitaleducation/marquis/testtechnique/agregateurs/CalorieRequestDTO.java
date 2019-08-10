package opendigitaleducation.marquis.testtechnique.agregateurs;

@SuppressWarnings({"unused", "WeakerAccess"}) //JSon serialisation
public class CalorieRequestDTO {
  private String searchedAlimentName;
  private CalorieAgregateurType calorieAgregateurType;

  public CalorieAgregateurType getCalorieAgregateurType() {
    return calorieAgregateurType;
  }

  public void setCalorieAgregateurType(CalorieAgregateurType calorieAgregateurType) {
    this.calorieAgregateurType = calorieAgregateurType;
  }

  public String getSearchedAlimentName() {
    return searchedAlimentName;
  }

  public void setSearchedAlimentName(String searchedAlimentName) {
    this.searchedAlimentName = searchedAlimentName;
  }
}
