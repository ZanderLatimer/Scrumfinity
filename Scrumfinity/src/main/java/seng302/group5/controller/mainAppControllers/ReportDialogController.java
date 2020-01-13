package seng302.group5.controller.mainAppControllers;

import java.io.File;
import java.util.Collection;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.group5.Main;
import seng302.group5.model.AgileItem;
import seng302.group5.model.util.ReportWriter;

/**
 * Created by Craig barnard on 26/05/2015.
 * Class that controls the report dialog which is used to create parameterised reports.
 */
public class ReportDialogController {

  @FXML private ComboBox<String> reportLevelCombo;
  @FXML private ListView<AgileItem> availableItemsList;
  @FXML private ListView<AgileItem> selectedItemsList;
  @FXML private Button addBtn;
  @FXML private Button removeBtn;
  @FXML private Button saveBtn;
  @FXML private Button cancelBtn;
  @FXML private HBox btnContainer;

  private Main mainApp;
  private Stage thisStage;
  private ObservableList<AgileItem> selectedItems = FXCollections.observableArrayList();
  private ObservableList<AgileItem> availableItems = FXCollections.observableArrayList();
  private ObservableList<String> reportLevels = FXCollections.observableArrayList();
  private ObservableList<AgileItem> chosenAvailableItems = FXCollections.observableArrayList();
  private ObservableList<AgileItem> chosenSelectedItems = FXCollections.observableArrayList();
  private ObservableList<AgileItem> tempItems = FXCollections.observableArrayList();

  private boolean comboListenerFlag;

  /**
   * Setup the report DialogController
   * Sets up the Controller, populating the drop down list with all the available categories.
   * Also sets up the selection model of the available items and selected items lists to multiple
   * selection to allow multiple items to be added to the report at once.
   * @param mainApp the god object of the application
   * @param thisStage    The stage of the dialog
   */
  public void setupController(Main mainApp, Stage thisStage) {
    this.mainApp = mainApp;
    this.thisStage = thisStage;

    thisStage.setTitle("Create Report");
    reportLevelCombo.setItems(reportLevels);

    String os = System.getProperty("os.name");
    selectedItemsList.setDisable(true);
    availableItemsList.setDisable(true);

    if (!os.startsWith("Windows")) {
      btnContainer.getChildren().remove(saveBtn);
      btnContainer.getChildren().add(saveBtn);
    }
    initialiseLists();

    comboListenerFlag = false;

    reportLevelCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
      try {
        // Check if the listener should be changing report level or not
        if (comboListenerFlag) {
          // Get out instantly after resetting flag to false
          comboListenerFlag = false;
          return;
        }
        if (!selectedItems.isEmpty()) {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setResizable(true);
          alert.getDialogPane().setPrefSize(400, 150);
          alert.setTitle("You have items selected");
          alert.setHeaderText(null);
          alert.setContentText("You have items in the Selected Items list. "
                               + "Changing report levels will clear this selection. "
                               + "Are you sure you wish to proceed?");
          alert.getButtonTypes().add(ButtonType.CANCEL);
          alert.showAndWait();
          if (alert.getResult().equals(ButtonType.OK)) {
            selectedItems.clear();
            setLevel();
          } else {
            comboListenerFlag = true;
            Platform.runLater(() -> {
              // to avoid firing the listener from within itself
              reportLevelCombo.setValue(oldValue);
            });
          }
        } else {
          setLevel();
        }
      } catch (Exception e) {
        //e.printStackTrace();
      }
    });

    thisStage.getIcons().add(new Image("Thumbnail.png"));
  }

  private void initialiseLists() {
    this.reportLevels.addAll("All", "Projects", "Teams", "People", "Skills", "Releases",
                             "Stories", "Backlogs", "Estimates", "Sprints"); // Add backlogs when they are done.
    availableItemsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    selectedItemsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    availableItemsList.setItems(availableItems.sorted());
    this.reportLevelCombo.setValue("All");
    selectedItemsList.setItems(selectedItems);

  }

  /**
   * Set the level of the report that you wish to write. Checks what Category is selected and
   * gets the list of those items from Main.
   */
  private void setLevel() {
    String level = reportLevelCombo.getSelectionModel().getSelectedItem();
    switch(level) {
      case ("All"):
        availableItemsList.setItems(null);
        updateLists();
        break;
      case ("Projects"):
        this.availableItems.clear();
        this.availableItems.setAll(mainApp.getProjects());
        this.availableItemsList.setItems(availableItems);

        updateLists();
        break;
      case ("Teams"):
        this.availableItems.clear();
        this.availableItems.setAll(mainApp.getTeams());
        this.availableItemsList.setItems(availableItems);
        updateLists();
        break;
      case ("People"):
        this.availableItems.clear();
        this.availableItems.setAll(mainApp.getPeople());
        this.availableItemsList.setItems(availableItems);
        updateLists();
        break;
      case ("Skills"):
        this.availableItems.clear();
        this.availableItems.setAll(mainApp.getSkills());
        this.availableItemsList.setItems(availableItems);
        updateLists();
        break;
      case ("Releases"):
        this.availableItems.clear();
        this.availableItems.setAll(mainApp.getReleases());
        this.availableItemsList.setItems(availableItems);
        updateLists();
        break;
      case ("Stories"):
        this.availableItems.clear();
        this.availableItems.setAll(mainApp.getStories());
        this.availableItemsList.setItems(availableItems);
        updateLists();
        break;
      case ("Backlogs"):
        this.availableItems.clear();
        this.availableItems.setAll(mainApp.getBacklogs());
        this.availableItemsList.setItems(availableItems);
        updateLists();
        break;
      case ("Estimates"):
        this.availableItems.clear();
        this.availableItems.setAll(mainApp.getEstimates());
        this.availableItemsList.setItems(availableItems);
        updateLists();
        break;
      case ("Sprints"):
        this.availableItems.clear();
        this.availableItems.setAll(mainApp.getSprints());
        this.availableItemsList.setItems(availableItems);
        updateLists();
        break;
    }
  }

  private void updateLists(){
    if (reportLevelCombo.getSelectionModel().getSelectedItem().equals("All")) {
      selectedItemsList.setDisable(true);
      availableItemsList.setDisable(true);
    } else {
      selectedItemsList.setDisable(false);
      availableItemsList.setDisable(false);
    }

    for (AgileItem item: selectedItems){
      if (availableItems.contains(item)) {
        availableItems.remove(item);
      }
    }
  }

  /**
   * A button that adds the selected available items to the selected report items for the
   * specified level.
   *
   * @param actionEvent Mouse click event
   * @throws Exception prints a stack trace if an error occurs.
   */
  public void addBtnClick(ActionEvent actionEvent) throws  Exception {
    try {
    chosenAvailableItems.setAll(availableItemsList.getSelectionModel().getSelectedItems());
      if (chosenAvailableItems != null) {
        selectedItems.addAll(availableItemsList.getSelectionModel().getSelectedItems());
        updateLists();
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    }

  /**
   * Removes the selected items from the selected report items list and re adds them to the
   * available items list.
   *
   * @param actionEvent actionEvent
   * @throws Exception prints a stack trace if an error occurs.
   */
  public void removeBtnClick(ActionEvent actionEvent) throws Exception {
    try {
      chosenSelectedItems.setAll(selectedItemsList.getSelectionModel().getSelectedItems());
      if (chosenSelectedItems != null) {
        selectedItems.removeAll(selectedItemsList.getSelectionModel().getSelectedItems());
        availableItems.addAll(chosenSelectedItems);
        updateLists();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Cancels the dialog, exiting without writing a report or saving the selections.\
   * @param actionEvent Mouse click event
   */
  public void cancelBtnClick(ActionEvent actionEvent) {
    thisStage.close();
  }

  /**
   * Save a report which details the selected items underneath the selected level.
   * @param actionEvent Mouse click event
   */
  public void saveBtnClick(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Report");
    FileChooser.ExtensionFilter
        filter =
        new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
    fileChooser.getExtensionFilters().add(filter);
    File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

    if (file != null) {
      ReportWriter report = new ReportWriter();
      String level = reportLevelCombo.getSelectionModel().getSelectedItem();
      if (level.equals("All")) {
        report.writeReport(mainApp, file);
        thisStage.close();
      }
      else {
        report.writeCustomReport(mainApp, file, selectedItems, level);
        thisStage.close();
      }
    }
  }
}


