module Game {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

        requires org.controlsfx.controls;
            requires com.dlsc.formsfx;
                            requires com.almasb.fxgl.all;
    requires java.desktop;
    requires javafx.swing;
    requires org.jfree.jfreechart;

    exports GUI;
    opens GUI to javafx.fxml;
    exports Game;
    opens Game to javafx.fxml;
}