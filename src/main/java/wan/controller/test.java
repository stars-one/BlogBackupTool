package wan.controller;

import com.jfoenix.controls.JFXSnackbar;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import wan.Utils.BaseController;

/**
 * @author StarsOne
 * @date Create in  2019/6/27 0027 13:14
 * @description
 */
public class test extends BaseController {
    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button btn;

    @FXML
    void btnOclick(ActionEvent event) {
        JFXSnackbar bar = new JFXSnackbar();
        bar.registerSnackbarContainer(rootPane);
        bar.enqueue(new JFXSnackbar.SnackbarEvent(new Text("保存成功")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
