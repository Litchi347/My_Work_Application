package com.example.myworkapplication;

public class CollectItem {
    // 数据库存储时使用
    private int id;
    private String title;
    private String content;

    // 构造函数
    public CollectItem(int id,String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public CollectItem(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String description) {
        this.content = description;
    }


}
