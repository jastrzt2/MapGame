package Game;

import Game.Population.Population;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Province {
    private Population pops;
    private Terrain terrain;
    private String name;

    public Province(){
        pops = new Population();
    }
    public static Province readProvinceFromFile(String filePath) throws IOException {
        Province province = new Province();
        StringBuilder currentLine = new StringBuilder("");
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        int currentChar;

        while ((currentChar = reader.read()) != -1) {
            char currentCharAsChar = (char) currentChar;

            if (currentCharAsChar == '#') {
                // Skip comments
                while ((currentChar = reader.read()) != -1 && (char) currentChar != '\n') {
                    // Skip until the end of the line
                }
            } else if (Character.isWhitespace(currentCharAsChar) && currentLine.length() == 0){}
            else { currentLine.append(currentCharAsChar); }
            if (currentLine.toString().equals("population")) {
                StringBuilder populationBlock = new StringBuilder("");
                while ((currentChar = reader.read()) != -1) {
                    currentCharAsChar = (char) currentChar;
                    if (currentCharAsChar == '#') {
                        while ((currentChar = reader.read()) != -1 && (char) currentChar != '\n') {
                        }
                    }
                    populationBlock.append(currentCharAsChar);
                    if(currentCharAsChar == '}')
                        break;;
                }
                processPopulationBlock(populationBlock.toString().replaceAll("#",""),province);
            }
            if( currentCharAsChar == '\n'){
                processLine(currentLine.toString().replaceAll("#",""), province);
                currentLine = new StringBuilder();
            }
        }
        processLine(currentLine.toString().replaceAll("#",""), province);

        reader.close();
        return province;
    }

    private static void processLine(String line, Province province) {
        // Split the line by "=" to get the key and value
        String[] parts = line.split("=", 2); // Specify limit to split only on the first "="
        if (parts.length == 2) {
            String key = parts[0].trim();
            String value = parts[1].trim();

            switch (key) {
                case "name":
                    province.setName(value);
                    break;
                case "terrain":
                    province.setTerrain(Terrain.valueOf(value.toUpperCase()));
                    break;
                default:
                    // Handle other keys as needed
                    break;
            }
        }
    }

    private static void processPopulationBlock(String value, Province province) {
        String cleanedString = value.replace("=", "").replace("{", "").replace("}", "").replaceAll("\\s+", " ").trim();
        String[] tokens = cleanedString.split("\\s+");

        for (int i = 0; i < tokens.length; i += 2) {
            province.getPopsMale()[i / 2] = Integer.parseInt(tokens[i]);
            province.getPopsFemale()[i / 2] = Integer.parseInt(tokens[i + 1]);
        }
    }

    private void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    private void setName(String value) {
        this.name = value;
    }


    public Integer[] getPopsMale() {
        return pops.getPopsMale();
    }
    public Integer[] getPopsFemale() {
        return pops.getPopsFemale();
    }

    @Override
    public String toString() {
        return "Province{" +
                "name='" + name + '\'' +
                ",\n popsMale=" + Arrays.toString(this.getPopsMale()) +
                ",\n popsFemale=" + Arrays.toString(this.getPopsFemale()) +
                ",\n terrain=" + terrain +
                '}';
    }

    public static void main(String[] args) {
        try {
            Province province = readProvinceFromFile("provinces/2 - tnt.txt");
            System.out.println(province);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void monthTick() {
        pops.monthTick();
    }
}
