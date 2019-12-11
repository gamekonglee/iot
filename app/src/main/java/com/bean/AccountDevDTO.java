package com.bean;

import java.io.Serializable;

/**
 * Created by gamekonglee on 2018/7/30.
 */

public class AccountDevDTO implements Serializable {
    String iotId;
    String productKey;
    String name;
    String deviceName;
    String productImage;
    String productModel;
    String categoryImage;
    String nickName;
    String type;
    String thingType;
    String nodeType;
    String status;
    Byte owned;
    String identityAlias;
    String gmtModified;
    String productName;
    String netType;
    String identityId;
    String description;

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getThingType() {
        return thingType;
    }

    public void setThingType(String thingType) {
        this.thingType = thingType;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Byte getOwned() {
        return owned;
    }

    public void setOwned(Byte owned) {
        this.owned = owned;
    }

    public String getIdentityAlias() {
        return identityAlias;
    }

    public void setIdentityAlias(String identityAlias) {
        this.identityAlias = identityAlias;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getIotId() {
        return iotId;
    }

    public void setIotId(String iotId) {
        this.iotId = iotId;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AccountDevDTO(String iotId, String productKey, String name, String deviceName, String type, String status) {
        this.iotId = iotId;
        this.productKey = productKey;
        this.name = name;
        this.deviceName = deviceName;
        this.type = type;
        this.status = status;
    }
    public AccountDevDTO(){

    }
}
