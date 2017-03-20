package QuickEcoLife;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
    private AnchorPane mainLayout;
    private final String title = "Make Life Eco Again";
    
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(title);
		
		initMainLayout();
	}
	
	private void initMainLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
			mainLayout = (AnchorPane) loader.load();
			
			// Show the scene containing the root layout.
			Scene scene = new Scene(mainLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void killChromeDriverExe() {
		if (System.getProperty("os.name").startsWith("Windows")) {
			String cmd_killChromeDriverExe = "taskkill /im chromedriver.exe /f";
			try {
				Runtime.getRuntime().exec(cmd_killChromeDriverExe);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
		killChromeDriverExe();
	}
}
