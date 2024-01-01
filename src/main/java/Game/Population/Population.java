package Game.Population;

public class Population {
    private Integer[] popsFemale;

    private Integer[] popsMale;
    private Double fertilityRate;
    private Double[] mortality;

    public Population() {
        popsFemale = new Integer[17];
        popsMale = new Integer[17];
        mortality = new Double[17];
        mortality[0] = 0.2/12;
        mortality[1] = 0.02/12;
        for(int i =2;i<15;i++)
            mortality[i] = 0.001/12;
        mortality[15] = 0.04/12;
        mortality[16] = 0.1/12;
        fertilityRate = 0.05;
    }
    private double countNumberOfBirths(){
        double x = 0;
        for(int i = 4;i<9;i++){
            x += popsFemale[i];
        }
        System.out.println(x);
        x = x * fertilityRate;
        System.out.println(x/2);
        return x / 2;
    }
    public Integer[] getPopsFemale() {
        return popsFemale;
    }

    public Integer[] getPopsMale() {
        return popsMale;
    }

    public void monthTick(){
        Double[] newMale = new Double[17];
        Double[] newFemale = new Double[17];
        for(int i = 1;i<popsMale.length;i++){ //loop for moving to older cohort
            newMale[i] = (double) (popsMale[i] + (double)popsMale[i-1]/60);
            newFemale[i] = (double) (popsFemale[i] + (double)popsFemale[i-1]/60);
        }
        newMale[0] = Double.valueOf(popsMale[0]);
        newFemale[0] = Double.valueOf(popsFemale[0]);
        for(int i = 0;i<popsMale.length-1;i++){ //loop for removing those people from cohort they no longer belong
            newMale[i] = (double) (newMale[i] - (double)popsMale[i]/60);
            newFemale[i] = (double) (newFemale[i] - (double)popsFemale[i]/60);
        }
        for(int i =0;i<popsMale.length;i++){
            newMale[i] = newMale[i] * (1-mortality[i]);
            newFemale[i] = newFemale[i] * (1-mortality[i]);
        }
        newMale[0] += countNumberOfBirths();
        newFemale[0] += countNumberOfBirths();
        System.out.println(totalPop());
        popsMale = roundArray(newMale);
        popsFemale = roundArray(newFemale);
    }

    public static Integer[] roundArray(Double[] array){
        Integer[] intArray = new Integer[17];
        for(int i =0; i<array.length;i++)
            intArray[i] = Math.toIntExact(Math.round(array[i]));
        return intArray;

    }

    public int totalPop() {
        int totalPop = 0;
        for (int i = 0; i < popsMale.length; i++){
            totalPop += popsFemale[i];
            totalPop += popsMale[i];
        }
        return totalPop;
    }
}
