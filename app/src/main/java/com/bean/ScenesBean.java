package com.bean;

import java.util.List;

public class ScenesBean {
    /**
     * Copyright 2019 bejson.com
     */

    /**
     * Auto-generated: 2019-07-10 16:14:44
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */

        private int total;
        private int pageNo;
        private List<Scenes> scenes;
        private int pageSize;
        public void setTotal(int total) {
            this.total = total;
        }
        public int getTotal() {
            return total;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }
        public int getPageNo() {
            return pageNo;
        }

        public void setScenes(List<Scenes> scenes) {
            this.scenes = scenes;
        }
        public List<Scenes> getScenes() {
            return scenes;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
        public int getPageSize() {
            return pageSize;
        }

}
