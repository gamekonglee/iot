package com.bean;

public class DeviceCategoryBean {
    /**
     * Copyright 2019 bejson.com
     */

    /**
     * Auto-generated: 2019-07-23 15:43:39
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private int id;
        private String name;
        private String pic;
        private int cate_id;
        private String trans;

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }
        public String getPic() {
            return pic;
        }

        public void setCate_id(int cate_id) {
            this.cate_id = cate_id;
        }
        public int getCate_id() {
            return cate_id;
        }

}
