package io.github.easyintent.quickref.data;

public class ReferenceItem {

    /** The ID of this reference.
     *
     */
    private String id;

    /** Parent id of this reference.
     *
     */
    private String parentId;

    private String title;
    private String summary;

    /** The command, if available.
     *
     */
    private String command;

    /** Check whether it is leaf node.
     *
     *
     */
    private boolean leaf;

    public ReferenceItem() {
    }

    public boolean hasChildren() {
        return !leaf;
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

    public boolean hasCommand() {
        return command != null;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
}
