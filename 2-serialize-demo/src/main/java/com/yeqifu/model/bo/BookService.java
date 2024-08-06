package com.yeqifu.model.bo;

/**
 * @author: yeqifu
 * @date: 2024/8/6 16:36
 */
public interface BookService {
    /**
     * 根据编号查询图书
     *
     * @param id 图书编号
     * @return
     */
    Book findBook(String id);
}
