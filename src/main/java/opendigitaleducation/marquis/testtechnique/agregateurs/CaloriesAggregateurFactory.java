package opendigitaleducation.marquis.testtechnique.agregateurs;

class CaloriesAggregateurFactory {
  static CalorieAgregateur GetAggegateur(CalorieAgregateurType calorieAgregateurType)
  {
    //Pour ajouter des aggregateurs
    //noinspection SwitchStatementWithTooFewBranches
    switch (calorieAgregateurType)
      {
        case Simple:
          return new SimpleCalorieAgregateur();
      }
      return null;
  }
}
