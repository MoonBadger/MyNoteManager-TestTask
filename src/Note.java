import java.util.Date;

public class Note {
    private String content;
    private String title;
    private Date datetime;

    public Note(String title, String content,  Date datetime) {
        this.content = content;
        this.title = title;
        this.datetime = datetime;
    }

    public Note(String title, String content) {
        this(title, content, new Date());
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Note{" +
                "content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", datetime=" + datetime +
                '}';
    }
}
