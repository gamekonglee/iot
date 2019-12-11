package com.bean;

/**
 * Copyright 2019 bejson.com
 */
import java.util.List;

/**
 * Auto-generated: 2019-07-11 16:22:9
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class SceneDetail {

    private boolean disableCancel;
    private boolean editable;
    private String icon;
    private Triggers triggers;
    private boolean valid;
    private String sceneType;
    private boolean enable;
    private String name;
    private String sceneId;
    private List<Actions> actions;
    private int status;
    public void setDisableCancel(boolean disableCancel) {
        this.disableCancel = disableCancel;
    }
    public boolean getDisableCancel() {
        return disableCancel;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    public boolean getEditable() {
        return editable;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getIcon() {
        return icon;
    }

    public void setTriggers(Triggers triggers) {
        this.triggers = triggers;
    }
    public Triggers getTriggers() {
        return triggers;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
    public boolean getValid() {
        return valid;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }
    public String getSceneType() {
        return sceneType;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public boolean getEnable() {
        return enable;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    public String getSceneId() {
        return sceneId;
    }

    public void setActions(List<Actions> actions) {
        this.actions = actions;
    }
    public List<Actions> getActions() {
        return actions;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

}
