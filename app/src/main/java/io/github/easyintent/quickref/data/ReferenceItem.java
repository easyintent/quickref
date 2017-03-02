package io.github.easyintent.quickref.data;

public class ReferenceItem {

    private String id;
    private String title;
    private String summary;

    // the command, if available
    //
    private String command;

    /** Reference category
     *
     */
    private String category;

    /** Reference children category, if available.
     *
     */
    private String children;

    public ReferenceItem() {
    }

    public boolean hasChildren() {
        return children != null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean hasEmbeddedCommand() {
        return command != null;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChildren() {
        return children;
    }

    public void setChildren(String children) {
        this.children = children;
    }
}
