package com.example.myworkapplication;

public class CollectItem {
    private String title;
    private String content;

    // 构造函数
    public CollectItem(String title, String content) {
        this.title = title;
        this.content = content;
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
