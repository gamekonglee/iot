package com.bean;

public class AreaCodeBean {
    String name;
    String en;
    String tel;
    String pinyin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public AreaCodeBean(String name, String en, String tel, String pinyin) {
        this.name = name;
        this.en = en;
        this.tel = tel;
        this.pinyin = pinyin;
    }
}
