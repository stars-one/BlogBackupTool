package wan.bean;

/**
 * @author StarsOne
 * @date Create in  2019/6/28 0028 9:45
 * @description
 */
public class Link {
    private String link;
    private String title;

    public Link(String link, String title) {
        this.link = link;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return "[" + title + "]" + "(" + link + ")"+"\n\n";
    }
}
