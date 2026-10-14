/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ro.umsft.gabriel.repository;

import ro.umsft.gabriel.news.News;
import java.util.List;
/**
 *
 * @author ministar
 */
public interface NewsRepository {
    
    void add(News news);
    void update(News news);
    void delete(String source, String  url);
    List<News> findAll();
    List<News> search(String keyword);
    void clearAll();
}
