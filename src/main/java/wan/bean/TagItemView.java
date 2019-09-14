package wan.bean;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * @author StarsOne
 * @date Create in  2019/6/25 0025 12:30
 * @description
 */
public class TagItemView extends HBox {
    private Map<String, List<String>> mapData =new HashMap<>();
    private TextField keyTextField = new TextField();
    private TextField tagNameTextField = new TextField();

    public TagItemView(String text) {
        String[] str = text.split("  ");
        ObservableList<Node> children = this.getChildren();
        keyTextField.setPromptText("输入关键字");
        keyTextField.setText(str[0]);
        tagNameTextField.setPromptText("标签名");
        tagNameTextField.setText(str[1]);
        children.addAll(new Text("标签"), keyTextField, tagNameTextField);
        this.setSpacing(20);
        this.setPrefHeight(50);
        this.setPrefWidth(200);
        this.setAlignment(Pos.CENTER);
    }

    public TagItemView() {
        ObservableList<Node> children = this.getChildren();
        keyTextField.setPromptText("输入关键字");
        tagNameTextField.setPromptText("标签名");
        children.addAll(new Text("标签"), keyTextField, tagNameTextField);
        this.setSpacing(20);
        this.setPrefHeight(50);
        this.setPrefWidth(200);
        this.setAlignment(Pos.CENTER);

    }



    public String getKeyText() {
        if (keyTextField.getText() != null) {
            return keyTextField.getText();
        } else {
            return "";
        }
    }

    public String getTagNameText() {
        if (tagNameTextField.getText() != null) {
            return tagNameTextField.getText();
        } else {
            return "";
        }
    }

    public Map<String, List<String>> getMapData() {
        String[] keys = keyTextField.getText().split("\\|");
        String tagName = tagNameTextField.getText();
        List<String> keyList = Arrays.asList(keys);
        mapData.put(tagName,keyList);
        return mapData;
    }
}
