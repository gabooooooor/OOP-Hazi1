/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ro.umsft.gabriel.repository;

import java.util.List;
import ro.umsft.gabriel.databases.DatabaseManager;
import ro.umsft.gabriel.news.News;
/**
 *
 * @author ministar
 */
public class SQLiteNewsRepository implements NewsRepository{
    
    @Override
    public void add(News news)
    {
        DatabaseManager.insertNews(news);
    }
    
    @Override
    public void update(News news)
    {
        DatabaseManager.updateNews(news);
    }
    
    @Override
    public void delete(String soource, String url)
    {
        DatabaseManager.deleteNews(soource, url);
    }
    
    @Override
    public List<News> findAll()
    {
        return DatabaseManager.getAllNews();
    }
    
    @Override
    public List<News> search(String keyword)
    {
        return DatabaseManager.searchNews(keyword);
    }
    
    @Override
    public void clearAll()
    {
        DatabaseManager.clearAll();
    }
}
