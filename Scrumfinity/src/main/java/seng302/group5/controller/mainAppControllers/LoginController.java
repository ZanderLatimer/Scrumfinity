package seng302.group5.controller.mainAppControllers;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import seng302.group5.Main;
import seng302.group5.model.util.Loading;
import seng302.group5.model.util.Settings;

/**
 * Created by Michael on 5/8/2015.
 *
 * Controller which handles the login screen. Exists on a seperate Scene, and shows the
 * mainApp scene when user enters the program
 */


public class LoginController {

  @FXML private TextField organizationName;
  private Main mainApp;

  @FXML
  protected void btnNewLogin(ActionEvent event) {
    String setName = organizationName.getText().trim();
    if (setName != null) {
      Settings.organizationName = setName;
      if (!setName.isEmpty()) {
        mainApp.setMainTitle("Scrumfinity - " + setName);
      } else {
        mainApp.setMainTitle("Scrumfinity");
      }
      mainApp.toggleName();
      mainApp.setMainScene();
      mainApp.getLMPC().getScrumBoardController().hardReset();
    }
  }

  @FXML
  protected void btnLoginLoad(ActionEvent event) {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Project");
    FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
    fileChooser.getExtensionFilters().add(filter);
    if (Settings.defaultFilepath != null) {
      fileChooser.setInitialDirectory(Settings.defaultFilepath);
    }
    try {
      File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
      if (file != null) {
        boolean successfulLoad;
        Settings.currentFile = file;
        mainApp.resetAll();
        Loading load = new Loading(mainApp);

        successfulLoad = load.loadFile(file);

        if (successfulLoad) {
          mainApp.getLMPC().refreshList(null);
          if (!Settings.organizationName.isEmpty()) {
            mainApp.setMainTitle("Scrumfinity - " + Settings.organizationName);
            mainApp.toggleName();
          }
          mainApp.setLastSaved(); //for revert
          mainApp.setMainScene();
          mainApp.getLMPC().initBurnDown();
        } else {
          mainApp.resetAll();
          mainApp.showLoginScreen(mainApp.getPrimaryStage());
        }
        mainApp.getLMPC().getScrumBoardController().hardReset();
        mainApp.getLMPC().getBurndownController().hardReset();
      }
    } catch (Exception e) {
//      e.printStackTrace();
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Loading error");
      alert.setHeaderText(null);
      alert.setContentText("There was a problem with loading. The file is out of date or corrupt.");
      alert.showAndWait();
    }
  }

  public void setMainApp(Main mainApp) {
    this.mainApp = mainApp;
  }
}
