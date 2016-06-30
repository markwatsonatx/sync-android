package com.cloudant.sync.replication;

/**
 * Created by markwatson on 6/30/16.
 */
public class ActiveDoc {

    private String id;
    private String revision;

    public ActiveDoc() {
    }

    public ActiveDoc(String id, String revision) {
        this.id = id;
        this.revision = revision;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }
}
