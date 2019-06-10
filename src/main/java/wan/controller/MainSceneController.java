package wan.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXProgressBar;

import org.codehaus.plexus.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import wan.Utils.DialogBuilder;
import wan.Utils.MyUtils;

public class MainSceneController implements Initializable {

    public JFXProgressBar progressbar;
    private String mdPath;
    private String htmlPath;
    @FXML
    private TextField tfInPath;

    @FXML
    private TextField tfOutPath;

    @FXML
    private JFXCheckBox checkBoxType;

    @FXML
    private JFXCheckBox checkBoxLabel;

    @FXML
    private JFXButton startBtn;

    @FXML
    private ImageView inPathImg;

    @FXML
    private ImageView outPathImg;


    /**
     * 点击按钮
     *
     * @param event
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    @FXML
    void btnStart(ActionEvent event) throws ParserConfigurationException, IOException, SAXException {
        startTask();
    }

    private void startTask() throws ParserConfigurationException, SAXException, IOException {
        progressbar.setVisible(true);
        boolean selectedLabel = checkBoxLabel.isSelected();//标签分类是否勾选
        if (checkFilePath()) {

            mdPath = tfOutPath.getText() + File.separator + "MD";
            htmlPath = tfOutPath.getText() + File.separator + "Html";
            createDiretory();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new File(tfInPath.getText()));//读取xml文件
            NodeList itemLists = document.getElementsByTagName("item");
            startClean(itemLists, () -> {
                progressbar.setVisible(false);
                new DialogBuilder(tfOutPath).setTitle("提示").setMessage("已完成，输出目录为").setHyperLink(tfOutPath.getText()).setNegativeBtn("确定").create();
            });


        }
    }

    /**
     * 开始整理分类
     *
     * @param itemLists
     * @param listener
     */
    private void startClean(NodeList itemLists, onFinishListener listener) {
        for (int i = 0; i < itemLists.getLength(); i++) {
            NodeList list = itemLists.item(i).getChildNodes();
            String title = list.item(0).getTextContent();//第一个结点内容是标题
            String link = list.item(1).getTextContent();//第二个结点内容是链接
            String description = list.item(6).getTextContent();
            try {
                fileWrite(title, link, description);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        listener.onFinish();
    }

    /**
     * 创建目录
     */
    private void createDiretory() {
        File file = new File(mdPath);
        File file1 = new File(htmlPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file1.exists()) {
            file1.mkdirs();
        }
    }

    /**
     * 检查输入和输出目录是否正确
     *
     * @return
     */
    private boolean checkFilePath() {
        if (tfInPath == null || tfInPath.getText().equals("")) {
            new DialogBuilder(checkBoxLabel).setTitle("错误提示").setMessage("xml文件路径为空").setNegativeBtn("确定").create();
            return false;
        } else if (!new File(tfInPath.getText()).exists()) {
            new DialogBuilder(checkBoxLabel).setTitle("错误提示").setMessage("xml不存在").setNegativeBtn("确定").create();
            return false;
        }
        if (tfOutPath == null || tfOutPath.getText().equals("")) {
            new DialogBuilder(checkBoxLabel).setTitle("错误提示").setMessage("输出路径为空").setNegativeBtn("确定").create();
            return false;
        } else if (tfOutPath.getText().contains(";") || tfOutPath.getText().contains("：")) {
            new DialogBuilder(checkBoxLabel).setTitle("错误提示").setMessage("输出路径有误").setNegativeBtn("确定").create();
        } else if (!new File(tfOutPath.getText()).exists()) {
            new File(tfInPath.getText()).mkdirs();//目录不存在，自动创建
        }
        return true;

    }


    /**
     * 选择输出文件夹
     *
     * @param event
     */
    @FXML
    void choseDiretory(MouseEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择输出文件夹");
        File file = directoryChooser.showDialog(inPathImg.getScene().getWindow());
        tfOutPath.setText(file.getPath());

    }

    /**
     * 选择xml文件
     *
     * @param event
     */
    @FXML
    void choseFile(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择输出目录");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml文件", "*.xml"));
        File choseFile = fileChooser.showOpenDialog(inPathImg.getScene().getWindow());
        if (choseFile != null) {
            tfInPath.setText(choseFile.getPath());
        }
    }

    /**
     * 拖动获得文件路径
     *
     * @param event
     */
    @FXML
    void getFile(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        File file = files.get(0);
        if (FileUtils.getExtension(file.getName()).contains("xml")) {
            String path = file.getPath();
            tfInPath.setText(path);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //设置图片
        inPathImg.setImage(MyUtils.getImg(this, "file.png"));
        outPathImg.setImage(MyUtils.getImg(this, "file.png"));

    }

    //判断是否是html还是md
    private boolean isMD(String msg) {
        //h p div 都是html的相关标签
        if (msg.startsWith("<h")) {
            return false;
        } else if (msg.startsWith("<p")) {
            return false;
        } else if (msg.startsWith("<d")) {
            return false;
        } else {
            return true;
        }
    }

    private void fileWrite(String title, String link, String msg) throws IOException {
        //处理title，有"/"会当做路径名，所以得把/去除，如PL/SQL这种字样，还有":"也是会报错
        title = dealTitle(title);

        File file;
        OutputStreamWriter outputStreamWriter;

        if (isMD(msg)) {
            //md文件

            file = new File(mdPath, title + ".md");
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        } else {
            //html文件
            file = new File(htmlPath, title + ".html");
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "GBK");
        }

        BufferedWriter writer = new BufferedWriter(outputStreamWriter);
        writer.write("原文链接：" + link + "\n");
        writer.write(msg);
        writer.close();//必须要调用close才能成功地将内容写入文件
    }

    private String dealTitle(String title) {
        //把:和/处理
        return title.replaceAll("/", "").replaceAll(":", " ");

    }

    public void pressEnter(KeyEvent keyEvent) throws IOException, SAXException, ParserConfigurationException {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            startTask();
        }
    }


    public void openAboutStage(ActionEvent actionEvent) throws IOException {
        MyUtils.createAndShowStage(this, null, "关于", "scene_about", "icon.png", 600, 600);
    }

    public void openVersionStage(ActionEvent actionEvent) throws IOException {
        MyUtils.createAndShowStage(this, null, "版本更新说明", "scene_version", "icon.png", 600, 600);

    }

    private interface onFinishListener {
        void onFinish();
    }

}
