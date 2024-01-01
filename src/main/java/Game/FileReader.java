package Game;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static Game.Province.readProvinceFromFile;

public class FileReader {

    public static void main(String[] args) {

    }

    public static Map<Integer,Province> readProvinces(String path){
        try {
            Path folderPath = Paths.get(path);
            String [] pathsStrings;
            Map<Integer,Province> provinces = new HashMap<>();
            // Use try-with-resources to ensure the stream is closed
            try (Stream<Path> paths = Files.walk(folderPath)) {
                pathsStrings = paths.filter(Files::isRegularFile).map(Path::toString).toArray(String[]::new);
            }
            for(int i = 0 ;i < pathsStrings.length;i++){
                provinces.put(extractFirstInteger(pathsStrings[i]),readProvinceFromFile(pathsStrings[i]));
            }

            return provinces;
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception in an appropriate way for your application
        }
        return null;
    }

    private static int extractFirstInteger(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();

        // Use a regular expression to match the first integer in the file name
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(fileName);

        // Find the first match
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        } else {
            // Return a default value or handle the case where no integer is found
            return -1; // For example, return -1 if no integer is found
        }
    }
}