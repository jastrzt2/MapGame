package GUI;

import Game.Game;
import Game.Province;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static GUI.PopulationChartMaker.createChartPanel;

public class GuiController {
    @FXML
    private Text dateField;
    @FXML
    private Pane map_pane;
    @FXML
    private Button turnButton;
    @FXML
    private StackPane populationPyramid;
    private ImageView map; // Initial scale factor
    private ImageView userMap; // Initial scale factor
    private double maxX;
    private double maxY;
    private double minX;
    private double minY;

    private double scale = 1.0; // Initial scale factor
    private double dragStartX;
    private double dragStartY;
    private boolean isDragging = false;
    private Game game;
    private Map<java.awt.Color, Integer> provinceMap;
    private Province clickedProvince;

    @FXML
    private void turnButtonClicked() {
        game.monthTick();
        updateDate();
        updateGui();
    }

    private void updateDate() {
        dateField.setText(game.getMyDate().toString());
    }

    @FXML
    private void initialize() {
        try {
            // Load an image
            String userMapPath = "userMap.bmp";
            String mapPath = "provinces.bmp";
            provinceMap = loadProvinces("provinces.txt");

            InputStream bmpInputStreamUserMap = getClass().getResourceAsStream(userMapPath);
            InputStream bmpInputStreamMap = getClass().getResourceAsStream(mapPath);
            Image mapImage = new Image(bmpInputStreamMap);
            Image userMapImage = new Image(bmpInputStreamUserMap);

            // Create an ImageView to display the image
            this.map = new ImageView(mapImage);
            this.userMap = new ImageView(userMapImage);

            // Set the ImageView as a child of the map_pane
            map_pane.getChildren().add(map);
            map_pane.getChildren().add(userMap);


            // Initialize mouse scroll event for zooming
            map_pane.setOnScroll(this::handleScroll);

            // Initialize keyboard events for panning
            map_pane.setOnKeyPressed(this::handleKeyPressed);
            map_pane.setFocusTraversable(true); // Make sure the pane can receive keyboard events
            map.setOnMouseClicked(this::handleMouseClicked);
            userMap.setOnMouseClicked(this::handleMouseClicked);
            map_pane.setOnMousePressed(this::handleMousePressed);
            map_pane.setOnMouseDragged(this::handleMouseDragged);
            map_pane.setOnMouseReleased(this::handleMouseReleased);
            this.game = new Game();
            updateDate();
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception in an appropriate way for your application
        }
    }

    private void handleScroll(ScrollEvent event) {
        double delta = 1.2;

        if (event.getDeltaY() < 0) {
            // Zoom out
            scale /= delta;
        } else {
            // Zoom in
            scale *= delta;
        }

        // Limit the scale to reasonable values
        scale = Math.min(10.0, Math.max(1, scale));

        // Update maximum translation values after scaling
        updateBounds();

        // Ensure that the zoomed and panned image stays within the bounds of the pane
        map.setTranslateX(clamp(map.getTranslateX(), minX, maxX));
        map.setTranslateY(clamp(map.getTranslateY(), minY, maxY));
        userMap.setTranslateX(clamp(map.getTranslateX(), minX, maxX));
        userMap.setTranslateY(clamp(map.getTranslateY(), minY, maxY));

        map.setScaleX(scale);
        map.setScaleY(scale);
        userMap.setScaleX(scale);
        userMap.setScaleY(scale);

        updateClip();

        // Update the clip when the size of map_pane changes
        map_pane.widthProperty().addListener((obs, oldWidth, newWidth) -> updateClip());
        map_pane.heightProperty().addListener((obs, oldHeight, newHeight) -> updateClip());

        event.consume();
    }

    private void handleKeyPressed(KeyEvent event) {
        double delta = 20/scale; // Pan distance

        double oldTranslateX = map.getTranslateX();
        double oldTranslateY = map.getTranslateY();

        double newTranslateX = oldTranslateX;
        double newTranslateY = oldTranslateY;

        switch (event.getCode()) {
            case UP:
            case W:
                newTranslateY = clamp(oldTranslateY + delta, minY, maxY);
                break;
            case DOWN:
            case S:
                newTranslateY = clamp(oldTranslateY - delta, minY, maxY);
                break;
            case LEFT:
            case A:
                newTranslateX = clamp(oldTranslateX + delta, minX, maxX);
                break;
            case RIGHT:
            case D:
                newTranslateX = clamp(oldTranslateX - delta, minX, maxX);
                break;
            default:
                return;
        }

        // Check if translation changed
        if (newTranslateX != oldTranslateX) {
            map.setTranslateX(newTranslateX);
            userMap.setTranslateX(newTranslateX);
        }
        if (newTranslateY != oldTranslateY) {
            map.setTranslateY(newTranslateY);
            userMap.setTranslateY(newTranslateY);
        }

        event.consume();
    }

    private void updateBounds() {
        Bounds viewportBounds = map_pane.getBoundsInLocal();
        double currentImageWidth = map.getBoundsInLocal().getWidth() * scale;
        double currentImageHeight = map.getBoundsInLocal().getHeight() * scale;

        maxX = Math.max(0, (currentImageWidth - viewportBounds.getWidth())/2);
        maxY = Math.max(0, (currentImageHeight - viewportBounds.getHeight())/2);
        minX = -maxX;
        minY = -maxY;
    }

    private void updateClip() {
        Rectangle clip = new Rectangle(map_pane.getWidth(), map_pane.getHeight());
        map_pane.setClip(clip);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void handleMouseClicked(MouseEvent event) {
        // Get the pixel position relative to the map_pane
        double x = event.getX();
        double y = event.getY();

        // Store the starting point for potential drag operation
        dragStartX = x;
        dragStartY = y;

        // Read the color of the pixel at the specified position
        Color color = getColorAtPosition(x, y);

        // Convert color components to integers
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);

        // Perform any action with the color
        System.out.println("Clicked at pixel position: (" + x + ", " + y + ")");
        System.out.println("Color at position: RGB(" + red + ", " + green + ", " + blue + ")");
        Integer id = provinceMap.get(new java.awt.Color(red,green,blue));
        System.out.println("Province ID is " + id);
        clickedProvince = game.getProvince(id);

        showProvinceInfo();
    }

    private void handleMousePressed(MouseEvent event) {
        // Start dragging operation
        dragStartX = event.getX();
        dragStartY = event.getY();
        isDragging = true;
        event.consume();
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isDragging) {
            double offsetX = event.getX() - dragStartX;
            double offsetY = event.getY() - dragStartY;

            // Update the translation values to move the image
            map.setTranslateX(clamp(map.getTranslateX() + offsetX, minX, maxX));
            map.setTranslateY(clamp(map.getTranslateY() + offsetY, minY, maxY));
            userMap.setTranslateX(clamp(userMap.getTranslateX() + offsetX, minX, maxX));
            userMap.setTranslateY(clamp(userMap.getTranslateY() + offsetY, minY, maxY));

            // Update the starting point for the next drag operation
            dragStartX = event.getX();
            dragStartY = event.getY();

            event.consume();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        // End dragging operation
        isDragging = false;
        event.consume();
    }

    private Color getColorAtPosition(double x, double y) {
        // Get the PixelReader from the image
        double imageX = x;
        double imageY = y;

        // Ensure the coordinates are within the image bounds
        imageX = clamp(imageX, 0, map.getImage().getWidth() - 1);
        imageY = clamp(imageY, 0, map.getImage().getHeight() - 1); //potrzebne?????

        return map.getImage().getPixelReader().getColor((int) imageX, (int) imageY);
    }

    public static Map<java.awt.Color, Integer> loadProvinces(String filePath) throws IOException {
        Map<java.awt.Color, Integer> provinceMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 4) {
                    int red = Integer.parseInt(parts[1]);
                    int green = Integer.parseInt(parts[2]);
                    int blue = Integer.parseInt(parts[3]);

                    java.awt.Color color = new java.awt.Color(red, green, blue);
                    Integer id = Integer.parseInt(parts[0]);

                    provinceMap.put(color, id);
                }
            }
        }

        return provinceMap;
    }

    private void updateGui(){
        if(clickedProvince!= null)
            showProvinceInfo();
    }


    private void showProvinceInfo() {
        System.out.println(clickedProvince.toString());

        Platform.runLater(() -> {

            SwingNode swingNode = new SwingNode();
            swingNode.setContent(createChartPanel(clickedProvince));
            if(!populationPyramid.getChildren().isEmpty())
                populationPyramid.getChildren().clear();

            // Add the SwingNode to the StackPane
            populationPyramid.getChildren().add(swingNode);
        });
    }

}
