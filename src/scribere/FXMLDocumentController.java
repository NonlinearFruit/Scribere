/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scribere;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.END;
import static javafx.scene.input.KeyCode.HOME;
import static javafx.scene.input.KeyCode.PAGE_DOWN;
import static javafx.scene.input.KeyCode.PAGE_UP;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 *
 * @author nonfrt
 */
public class FXMLDocumentController implements Initializable {
    
    private SimpleIntegerProperty redColor = new SimpleIntegerProperty(255);
    private Timeline whiteToRed = new Timeline();
    private Timeline reaper = new Timeline();
    private Integer goal = 5;
    
    @FXML
    private VBox anchor;
    @FXML
    private Text displayText;
    @FXML
    private TextArea writersDOOM;
    @FXML
    private HBox header;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Misc
        writersDOOM.setMouseTransparent(true);
        
        // Listeners
        writersDOOM.textProperty().addListener((observe,old,neo)->{
            int wordsNeo = neo.trim().split("\\s+").length;
            int wordsOld = old.trim().split("\\s+").length;
            displayText.setText(wordsNeo+" words");
            whiteToRed.stop();
            if(wordsNeo >= goal) {
                winnerWinnerChickenDinner();
            } else if(wordsNeo<wordsOld) {
                redColor.set(0);
            } else {
                whiteToRed.play();
                reaper.stop();
            }
        });
        redColor.addListener((observe, old, neo)->{
            writersDOOM.setStyle("-fx-control-inner-background: rgb(255,"+neo+","+neo+");");
        });
        writersDOOM.addEventFilter(KeyEvent.ANY, keyEvent->{
              switch (keyEvent.getCode()) {
                // block cursor control keys.
                case LEFT:
                case RIGHT:
                case UP:
                case DOWN:
                case PAGE_UP:
                case PAGE_DOWN:
                case HOME:
                case END:
                case DELETE:
                case BACK_SPACE:
                  keyEvent.consume();
             }
        });
        
        // Game-ifiers
        reaper.setCycleCount(Timeline.INDEFINITE);
        reaper.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,e->reapWord()),
                new KeyFrame(Duration.seconds(2))
        );
        whiteToRed.setOnFinished(e->reaper.play());
        whiteToRed.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(redColor,255)
                ),
                new KeyFrame(Duration.seconds(20),
                        new KeyValue(redColor,0,Interpolator.EASE_IN)
                )
        );
        whiteToRed.play();
    }    
    
    public void reapWord(){
        String text = writersDOOM.getText().trim();
        if(text.contains(" ") && text.length()>1){
            text = text.replaceAll("\\s+[^\\s]+$", "");
            writersDOOM.setText(text);
            writersDOOM.positionCaret(text.length());
        }
    }
    
    public void winnerWinnerChickenDinner(){
        reaper.stop();
        whiteToRed.stop();
        displayText.setText("Finished!");
        writersDOOM.setMouseTransparent(false);
        
        Button save = new Button("Save'n'Close");
        header.getChildren().clear();
        header.getChildren().add(save);
        save.setOnAction(e->{
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName("My_Scribble.txt");
            File file = chooser.showSaveDialog(save.getScene().getWindow());
            try( PrintWriter out = new PrintWriter(file) ){
                out.print(writersDOOM.getText());
            }catch(FileNotFoundException ex){
                new Alert(Alert.AlertType.ERROR,ex.getMessage(),ButtonType.OK).show();
            }
        });
    }
}
