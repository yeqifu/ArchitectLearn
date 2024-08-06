package com.yeqifu.model.bo;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @author: yeqifu
 * @date: 2024/8/6 16:40
 */
@Builder
public class Book {
    private String id;

    private String name;

    private String title;

    private String tag;

    private String content;

    public Book() {
    }

    public Book(String id, String name, String title, String tag, String content) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.tag = tag;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
