package wan.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXProgressBar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import wan.Utils.BaseController;
import wan.Utils.DialogBuilder;
import wan.Utils.Intent;
import wan.Utils.MyUtils;
import wan.bean.Link;
import wan.bean.TagItemView;

public class MainSceneController extends BaseController {

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
    private Hyperlink addLabel;
    @FXML
    private JFXButton startBtn;

    @FXML
    private JFXCheckBox outputNavigator;

    @FXML
    private ImageView inPathImg;

    @FXML
    private ImageView outPathImg;
    private File file;//tag.txt，标签的数据


    /**
     * 点击按钮
     *
     * @param event
     */
    @FXML
    void btnStart(ActionEvent event) {
        startTask();
    }

    private void startTask() {
        progressbar.setVisible(true);
        Task<Void> myTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (checkFilePath()) {

                    mdPath = tfOutPath.getText() + File.separator + "MD";
                    htmlPath = tfOutPath.getText() + File.separator + "Html";
                    createDiretory();//创建目录

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    Document document = builder.parse(new File(tfInPath.getText()));//读取xml文件
                    NodeList itemLists = document.getElementsByTagName("item");
                    startClean(itemLists);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                progressbar.setVisible(false);
                new DialogBuilder(tfOutPath).setTitle("提示").setMessage("已完成，输出目录为").setHyperLink(tfOutPath.getText()).setNegativeBtn("确定").create();
            }
        };
        new Thread(myTask).start();

    }

    /**
     * 开始整理分类
     *
     * @param itemLists
     */
    private void startClean(NodeList itemLists) {
        List<Link> links = new ArrayList<>();//连接
        try {
            //获得每一篇博文的单独文件
            for (int i = 0; i < itemLists.getLength(); i++) {
                NodeList list = itemLists.item(i).getChildNodes();
                String title = list.item(0).getTextContent();//第一个结点内容是标题
                String link = list.item(1).getTextContent();//第二个结点内容是链接
                String description = list.item(6).getTextContent();
                links.add(new Link(link, title));
                fileWrite(title, link, description);//抽取内容，写入单独文件
            }
            //标签分类
            boolean selectedLabel = checkBoxLabel.isSelected();//标签分类是否勾选
            if (selectedLabel) {
                fileItemizeForMd(mdPath);//按标签分类（MD文件夹）
                fileItemizeForMd(htmlPath);//按标签分类(Html文件夹）
            }
            //输出目录导航文件
            if (outputNavigator.isSelected()) {
                File tagFile = new File(MyUtils.getCurrentPath(), "tag.txt");
                File outputFile = new File(tfOutPath.getText(), "目录导航.md");

                if (!outputFile.exists()) {
                    //tag.txt不存在，创建tag.txt
                    List<String> list = FileUtils.readLines(tagFile, "UTF-8");
                    for (String s : list) {
                        TagItemView tagItemView = new TagItemView(s);
                        String tagName = tagItemView.getTagNameText();
                        List<String> keys = tagItemView.getMapData().get(tagName);
                        Iterator<Link> iterator = links.iterator();

                        FileUtils.writeStringToFile(outputFile, "## " + tagName + "\n", "UTF-8", true);//写入二级标题
                        while (iterator.hasNext()) {
                            Link next = iterator.next();
                            for (String key : keys) {
                                if (next.getTitle().contains(key)) {
                                    FileUtils.writeStringToFile(outputFile, next.getLink(), "UTF-8", true);
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    String str = FileUtils.readFileToString(outputFile, "UTF-8");
                    StringBuffer allBuffer = new StringBuffer();//总的缓存区
                    List<String> list = FileUtils.readLines(tagFile, "UTF-8");
                    for (String s : list) {
                        TagItemView tagItemView = new TagItemView(s);
                        String tagName = tagItemView.getTagNameText();
                        List<String> keys = tagItemView.getMapData().get(tagName);
                        Iterator<Link> iterator = links.iterator();

                        //字符串缓存区
                        StringBuffer buffer = new StringBuffer();
                        //二级标题
                        String h2Title = "## " + tagName + "\n";
                        //写入二级标题
                        buffer.append(h2Title);

                        while (iterator.hasNext()) {
                            Link next = iterator.next();
                            for (String key : keys) {
                                //博文标题是否包含有关键字
                                if (next.getTitle().contains(key)) {
                                    buffer.append(next.getLink()).append("\n");
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                        //追加内容
                        if (str.contains(h2Title)) {
                            //出现同2级标题的，在2级标题中后面追加内容
                            str = str.replace(h2Title, buffer.toString());
                        } else {
                            //出现新的2级标题，单独追加
                            allBuffer.append(buffer.toString());
                        }
                    }

                    FileUtils.writeStringToFile(outputFile, str+allBuffer.toString(), "UTF-8", false);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 标签分类(Md文件夹）
     *
     * @throws IOException
     */
    private void fileItemizeForMd(String path) throws IOException {
        File file = new File(MyUtils.getCurrentPath(), "tag.txt");
        if (file.exists()) {
            List<String> list = FileUtils.readLines(file, "UTF-8");
            //每个标签以及标签的关键字数据s
            for (String s : list) {
                TagItemView tagItemView = new TagItemView(s);
                String tagName = tagItemView.getTagNameText();
                List<String> keys = tagItemView.getMapData().get(tagName);

                //包含关键字的文件
                File[] files = new File(path).listFiles((dir, name) -> {
                    boolean flag = false;
                    for (String key : keys) {
                        if (name.contains(key)) {
                            flag = true;
                            break;
                        }
                    }
                    return flag;
                });

                //创建标签名的文件夹
                File diretory = new File(path + File.separator + tagName);
                diretory.mkdirs();
                //剪切，把文件放入标签内
                for (File file1 : files) {
                    if (!file1.isDirectory()) {
                        FileUtils.copyFileToDirectory(file1, diretory);
                        file1.delete();
                    }

                }

            }
        }
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
        fileChooser.setTitle("选择xml文件");
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
        if (FilenameUtils.getExtension(file.getName()).contains("xml")) {
            String path = file.getPath();
            tfInPath.setText(path);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        file = new File(MyUtils.getCurrentPath(), "tag.txt");
        if (file.exists()) {
            checkBoxLabel.setSelected(true);
        }

        Image img = MyUtils.getImg("file.png");
        inPathImg.setImage(img);
        outPathImg.setImage(img);
        MyUtils.setLinkAction(addLabel, new MyUtils.LinkActionHander() {
            @Override
            public void setAction() {
                new Intent("scene_tag_input", "输入标签", "icon.png", 600, 700).start();
            }
        });

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


    public void openAboutStage(ActionEvent actionEvent) {
        new Intent("scene_about", "关于", "icon.png", 600, 600).start();
    }

    public void openVersionStage(ActionEvent actionEvent) {
        new Intent("scene_version", "版本更新说明", "icon.png", 600, 600).start();

    }

    private interface onFinishListener {
        void onFinish();
    }

}
