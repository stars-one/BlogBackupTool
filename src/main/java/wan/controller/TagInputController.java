package wan.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSnackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import wan.Utils.BaseController;
import wan.Utils.MyUtils;
import wan.bean.TagItemView;

/**
 * @author StarsOne
 * @date Create in  2019/6/24 0024 13:03
 * @description
 */
public class TagInputController extends BaseController {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Node> children = vboxTag.getChildren();
        File file = new File(MyUtils.getCurrentPath(), "tag.txt");
        if (!file.exists()) {
            for (int i = 0; i < 5; i++) {
                children.add(new TagItemView());
            }

        } else {

            try {
                List<String> stringList = FileUtils.readLines(file, "UTF-8");
                //得到数据，读取并添加tag
                for (String s : stringList) {
                    children.add(new TagItemView(s));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    @FXML
    private ScrollPane rootPane;

    @FXML
    private VBox vboxTag;

    @FXML
    private JFXButton btnAddTag;

    @FXML
    private JFXButton btnSave;

    @FXML
    void addTag(ActionEvent event) {
        ObservableList<Node> children = vboxTag.getChildren();
        children.add(new TagItemView());

    }

    @FXML
    void save(ActionEvent event) {
        ObservableList<Node> children = vboxTag.getChildren();
        File file = new File(MyUtils.getCurrentPath(), "tag.txt");
        StringBuilder stringBuilder = new StringBuilder();
        for (Node child : children) {
            TagItemView child1 = (TagItemView) child;

            String keyText = child1.getKeyText();
            String tagNameText = child1.getTagNameText();


            if (!keyText.equals("") && !tagNameText.equals("")) {
                String text = keyText + " " + tagNameText + "\n";
                stringBuilder.append(text);
            }
        }
        try {
            if (!stringBuilder.toString().equals("")) {
                FileUtils.writeStringToFile(file, stringBuilder.toString(), "UTF-8");
                //Toast提示
                JFXSnackbar bar = new JFXSnackbar(((Pane) vboxTag.getParent().getParent()));
                bar.enqueue(new JFXSnackbar.SnackbarEvent(new Text("保存成功")));
            } else {
                JFXSnackbar bar = new JFXSnackbar(((Pane) vboxTag.getParent().getParent()));
                bar.enqueue(new JFXSnackbar.SnackbarEvent(new Text("未输入内容")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
