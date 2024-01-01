package Game;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static Game.FileReader.readProvinces;
import static Game.Province.readProvinceFromFile;

public class Game {

    private LocalDate myDate;
    private Map<Integer,Province> provinces;

    // Constructor
    public Game() throws IOException {
        this.myDate = LocalDate.of(1870, 1, 1);
        provinces = readProvinces("provinces");
    }

    public LocalDate getMyDate() {
        return myDate;
    }


    public Province getProvince(Integer id){
        return provinces.get(id);
    }

    public void monthTick(){
        myDate = myDate.plusMonths(1);
        provinces.forEach((key, province) -> {
            province.monthTick();
        });
    }
}
