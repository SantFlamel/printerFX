package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class Controller {

    @FXML
    private Button button;

//    @FXML
//    public void initialize(){
//        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                button.setText("Thanks!");
//            }
//        });
//    }

    @FXML
    public void onClickMethod(){
        button.setText("Thanks!");
    }

}