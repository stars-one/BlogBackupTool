package wan.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import wan.Utils.MyUtils;

/**
 * @author StarsOne
 * @date Create in  2019/6/9 0009 22:41
 * @description
 */
public class SceneAboutController implements Initializable {

    public ImageView icon;

    @FXML
    private ImageView weixin;

    @FXML
    private ImageView zhifubao;

    @FXML
    private Hyperlink projectlink;

    @FXML
    private Hyperlink bloglink;

    @FXML
    private Hyperlink qqlink;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        icon.setImage(MyUtils.getImg(this,"icon.png"));
        weixin.setImage(MyUtils.getImg(this,"weixin.jpg"));
        zhifubao.setImage(MyUtils.getImg(this,"zhifubao.jpg"));
        MyUtils.setLinkAction(projectlink, () -> MyUtils.setLinkAutoAction(projectlink));
        MyUtils.setLinkAction(qqlink, () -> MyUtils.setLinkAutoAction(qqlink));
        MyUtils.setLinkAction(bloglink, () -> MyUtils.setLinkAutoAction(bloglink));
    }
}
