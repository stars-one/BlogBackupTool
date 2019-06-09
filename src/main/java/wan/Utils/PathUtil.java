package wan.Utils;

import java.io.InputStream;
import java.net.URL;

import javafx.scene.image.Image;

/**
 * @author StarsOne
 * @date Create in  2019/6/5 0005 14:01
 * @description
 */
public class PathUtil {
    /**
     * 获得图片文件，
     * @param o 当前的class，传入this即可
     * @param fileName 图片名+扩展名
     * @return 图片image
     */
    public static Image getImg(Object o, String fileName) {
        URL res = o.getClass().getResource("img");
        if (fileName.contains(".")) {
            String temp = res.toString() + "/" + fileName;
            return new Image(temp);
        }
        return null;
    }


    /**
     * 获得fxml文件路径
     * @param o class文件，传入this
     * @param fileName 文件名
     * @return
     */
    public static URL getFxmlPath(Object o,String fileName) {

        return o.getClass().getResource("fxml/"+fileName+".fxml");
    }

    public static InputStream getFxmlFile(Object o,String fileName) {
        return o.getClass().getResourceAsStream("fxml/"+fileName+".fxml");
    }

}
