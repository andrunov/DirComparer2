package comparer.controller;


import comparer.MainApp;
import comparer.model.FileComparer;
import comparer.util.AppPreferences;
import comparer.util.Formatter;
import comparer.util.Message;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/*controller for MainView.fxml window*/
public class MainController implements Initializable {

    /*label of first directory*/
    @FXML
    private Label firstDirLbl;

    /*label of second directory*/
    @FXML
    private Label secondDirLbl;

    /*info label*/
    @FXML
    private Label infoLbl;

    /*result label*/
    @FXML
    private Label resultLbl;

    /*button for firs directory selection*/
    @FXML
    private Button firstDirSelectBtn;

    /*button for second directory selection*/
    @FXML
    private Button secondDirSelectBtn;

    /*button for change language pocket*/
    @FXML
    private Button changeLocalButton;

    /*button for start comparing procedure*/
    @FXML
    private Button executeButton;

    /*button for exit application*/
    @FXML
    private Button openResultBtn;

    /*button for clear resources to default*/
    @FXML
    private Button clearBtn;

    /*button for open settings window*/
    @FXML
    private Button settingsBtn;

    /*button for open application info window*/
    @FXML
    private Button aboutBtn;

    /*button for exit application*/
    @FXML
    private Button exitBtn;

    /*language pocket*/
    private ResourceBundle resourceBundle;

    /*first choose directory for comparing*/
    private File firstDirectory;

    /*second choose directory for comparing*/
    private File secondDirectory;

    /*reference to compare engine class*/
    private FileComparer comparer;

    /* Reference to the main application*/
    private MainApp mainApp;

    /*desktop uses for open files just from JavaFX application*/
    private Desktop desktop;



    /*constructor*/

    public MainController() {
    }

    public void init(MainApp mainApp) {
        this.mainApp = mainApp;
        this.comparer = new FileComparer();
        if (Desktop.isDesktopSupported()) {
            this.desktop = Desktop.getDesktop();
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /*choose first directory*/
    @FXML
    private void choseFirstDirectory(){
        File initialDirectory = this.firstDirectory == null ? null : this.firstDirectory.getParentFile();
        File directory = chooseDirectory(initialDirectory, "firstDirectory");
        if (directory != null) {
            this.firstDirectory = directory;
            AppPreferences.setDirectory(directory.getParentFile(), "firstDirectory");
            setTextDirLabel(this.firstDirLbl, "FirstDirectory", getDirInfo(directory));
            updateTextInfoLbl();
        }
    }

    /*choose second directory*/
    @FXML
    private void choseSecondDirectory(){
        File initialDirectory = this.secondDirectory == null ? null : this.secondDirectory.getParentFile();
        File directory = chooseDirectory(initialDirectory, "secondDirectory");
        if (directory != null) {
            this.secondDirectory = directory;
            AppPreferences.setDirectory(directory.getParentFile(), "secondDirectory");
            setTextDirLabel(this.secondDirLbl, "SecondDirectory", "" + getDirInfo(directory));
            updateTextInfoLbl();
        }
    }

    /*start comparing procedure*/
    @FXML
    private void executeComparing(){
        if (this.firstDirectory != null) {
            this.comparer.setStartDirectoryName(this.firstDirectory.getAbsolutePath());
            if (this.secondDirectory != null) {
                this.comparer.setEndDirectoryName(this.secondDirectory.getAbsolutePath());
            }
        }else {
             //*if selected single directory save it as firstDirectory*//*
            if (this.secondDirectory != null) {
                this.comparer.setStartDirectoryName(this.secondDirectory.getAbsolutePath());
            }
        }

        this.comparer.setResourceBundle(this.resourceBundle);
        try{
            if(this.comparer.compare()) {
                setTextDirLabel(this.resultLbl, "Result", getFileInfo(this.comparer.getReportName()));
                setVisibility(true);
            }
        }
        catch (Exception e){
            Message.errorAlert(this.resourceBundle,"Error: ", e);
          //  e.printStackTrace();
        }
    }

    /*open dialog to choose directory*/
    private File chooseDirectory(File initialDirectory, String directoryKey) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (initialDirectory == null) {
            initialDirectory = AppPreferences.getDirectory(directoryKey);
        }
        if ((initialDirectory != null)&&(initialDirectory.exists())) {
            directoryChooser.setInitialDirectory(initialDirectory);
        }
        return directoryChooser.showDialog(null);
    }

    /*initialize language pocket and set visibility to window elements*/
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        setVisibility(false);
    }

    /*change pocket language*/
    @FXML
    private void changeLocale(){
        if (this.resourceBundle.getLocale().getLanguage().equalsIgnoreCase("ru")){
            this.resourceBundle = ResourceBundle.getBundle("comparer/bundles/Locale",new Locale("en"));
        }else {
            this.resourceBundle = ResourceBundle.getBundle("comparer/bundles/Locale",new Locale("ru"));
        }
        updateLocalText();
    }

    /*update text of window elements*/
    private void updateLocalText(){
        updateTextInfoLbl();
        this.firstDirSelectBtn.setText(this.resourceBundle.getString("Select"));
        this.secondDirSelectBtn.setText(this.resourceBundle.getString("Select"));
        this.changeLocalButton.setText(this.resourceBundle.getString("ChangeLocal"));
        this.executeButton.setText(this.resourceBundle.getString("Compare"));
        this.clearBtn.setText(this.resourceBundle.getString("Clear"));
        this.openResultBtn.setText(this.resourceBundle.getString("Open"));
        this.settingsBtn.setText(this.resourceBundle.getString("Settings"));
        this.aboutBtn.setText(this.resourceBundle.getString("AppInfo"));
        this.exitBtn.setText(this.resourceBundle.getString("Exit"));
    }

    /*updates text for infoLbl Label depending of
    * firstDirectory and secondDirectory directories*/
    private void updateTextInfoLbl(){
        setTextDirLabel(firstDirLbl,"FirstDirectory",getDirInfo(firstDirectory));
        setTextDirLabel(secondDirLbl,"SecondDirectory",getDirInfo(secondDirectory));
        String reportName = this.comparer.getReportName();
        if(reportName != null) {
            setTextDirLabel(resultLbl, "Result", getFileInfo(this.comparer.getReportName()));
        }
        if ((firstDirectory ==null)&&(secondDirectory ==null)){
            infoLbl.setText(resourceBundle.getString("InfoDefault"));
        }
        else if ((firstDirectory ==null)||(secondDirectory ==null)){
            infoLbl.setText(resourceBundle.getString("CompareSingleDirectory"));
        }else if(firstDirectory.equals(secondDirectory)){
            infoLbl.setText(resourceBundle.getString("CompareSingleDirectory"));
        }
        else {
            infoLbl.setText(resourceBundle.getString("CompareTwoDirectories"));
        }
    }

    /*updates text for several Labels*/
    private void setTextDirLabel(Label label, String bundleKey, String infoPath){
         label.setText(resourceBundle.getString(bundleKey) + infoPath);
    }

    /**/
    private String getDirInfo(File directory){
        String result = "";
        if (directory != null) {
            result = ": " + directory.getPath();
        }
        return result;
    }

    /*return string-represent directory name with closest parent directory*/
    private String getFileInfo(String filePath){
        String result = "";
        File file = new File(filePath);
        if (file.exists()){
            result = ": " + file.getParentFile().getPath() + "\\" + file.getName();
        }
        return result;
    }

    /*open saved txt-result file*/
    @FXML
    private void openResult(){
        try {
            assert this.desktop != null;
            this.desktop.open(new File(this.comparer.getReportName()));
        } catch (Exception e) {
            Message.errorAlert(this.resourceBundle, "Error in MainController.openResult() ", e);
        }
    }

    /*set visibility to open result button and label*/
    private void setVisibility(boolean visibility){
        this.resultLbl.setVisible(visibility);
        this.openResultBtn.setVisible(visibility);
    }

    /*clear fields to default*/
    @FXML
    private void clear(){
        this.comparer.clean();
        this.comparer.setReportName(null);
        this.firstDirectory = null;
        this.secondDirectory = null;
        updateTextInfoLbl();
        setVisibility(false);
    }

    /*open settings window*/
    @FXML
    private void openSettings(){
        mainApp.showSettingsEditDialog(this.resourceBundle, this.comparer);
    }

    /*show application info*/
    @FXML
    private void showAppInfo(){
        Message.info(this.resourceBundle,"AboutApp");
    }

    /*exit application*/
    @FXML
    private void doExitApp(){
        this.mainApp.getPrimaryStage().close();
    }

    /*listener for observe change height of main window */
    public ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) ->
    {
        double newHeight = this.mainApp.getPrimaryStage().getHeight();
        double newWidth = Formatter.getTextSize(newHeight);
        String newSize = "-fx-font-size:" +  String.valueOf(newWidth) + ";";
        this.firstDirLbl.setStyle(newSize);
        this.secondDirLbl.setStyle(newSize);
        this.infoLbl.setStyle(newSize);
        this.resultLbl.setStyle(newSize);
        this.firstDirSelectBtn.setStyle(newSize);
        this.secondDirSelectBtn.setStyle(newSize);
        this.changeLocalButton.setStyle(newSize);
        this.executeButton.setStyle(newSize);
        this.openResultBtn.setStyle(newSize);
        this.clearBtn.setStyle(newSize);
        this.settingsBtn.setStyle(newSize);
        this.aboutBtn.setStyle(newSize);
        this.exitBtn.setStyle(newSize);
    };

}
