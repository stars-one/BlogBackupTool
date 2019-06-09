package wan;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wan.Utils.PathUtil;

public class Main extends Application {

    private InputStream in;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();    // 创建对象
        loader.setBuilderFactory(new JavaFXBuilderFactory());    // 设置BuilderFactory
        loader.setLocation(PathUtil.getFxmlPath(this, "scene_main"));
        InputStream inputStream = PathUtil.getFxmlFile(this, "scene_main");
        Object o = loader.load(inputStream);

        //        Parent root = FXMLLoader.load(PathUtil.getFxmlPath(this,"scene_main"));
        Parent root = (Parent) o;
        primaryStage.getIcons().add(PathUtil.getImg(this, "icon.png"));
        primaryStage.setTitle("博客园博文备份工具");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

    }


    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
