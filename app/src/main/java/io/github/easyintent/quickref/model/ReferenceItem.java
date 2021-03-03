package io.github.easyintent.quickref.model;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceItem that = (ReferenceItem) o;

        if (leaf != that.leaf) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (parentId != null ? !parentId.equals(that.parentId) : that.parentId != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        return command != null ? command.equals(that.command) : that.command == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (command != null ? command.hashCode() : 0);
        result = 31 * result + (leaf ? 1 : 0);
        return result;
    }
}
