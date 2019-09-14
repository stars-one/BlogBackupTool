package wan;

import javafx.application.Application;
import javafx.stage.Stage;
import wan.Utils.MyUtils;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        MyUtils.showMainStage(this,primaryStage,"博客园博文备份工具","scene_main","icon.png",600,400);
        //MyUtils.showMainStage(this,primaryStage,"博客园博文备份工具","test","icon.png",600,400);
        //MyUtils.showMainStage(this,primaryStage,"博客园博文备份工具","scene_tag_input","icon.png",600,700);
        //FXMLLoader loader = new FXMLLoader();    // 创建对象
        //loader.setBuilderFactory(new JavaFXBuilderFactory());    // 设置BuilderFactory
        //loader.setLocation(MyUtils.getFxmlPath(this, "scene_main"));
        //InputStream inputStream = MyUtils.getFxmlFile(this, "scene_main");
        //Object o = loader.load(inputStream);
        //
        ////        Parent root = FXMLLoader.load(MyUtils.getFxmlPath(this,"scene_main"));
        //Parent root = (Parent) o;
        //primaryStage.getIcons().add(MyUtils.getImg(this, "icon.png"));
        //primaryStage.setTitle("博客园博文备份工具");
        //primaryStage.setScene(new Scene(root, 600, 400));
        //primaryStage.show();

    }


    public static void main(String[] args)  {
        launch(args);
    }
}
