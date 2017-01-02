package org.ifou.colorbox;

import org.ifou.colorbox.mousemove.MouseMoveChecker;
import org.ifou.colorbox.mousemove.MouseMoveException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainFrame extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 610, 230);
		root.setCenter(new ColorBox(primaryStage));
		try {
			MouseMoveChecker.getInstance().startChecker();
		} catch (MouseMoveException e) {
			e.printStackTrace();
		}
		primaryStage.setTitle("ColorBox");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/icon.png")));
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent e) {
				MouseMoveChecker.getInstance().stopChecker();
			}
		});
	}

}
