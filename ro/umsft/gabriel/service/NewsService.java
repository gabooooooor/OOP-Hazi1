/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ro.umsft.gabriel.service;

import java.util.*;
import ro.umsft.gabriel.repository.NewsRepository;
import ro.umsft.gabriel.news.News;
import ro.umsft.gabriel.rss.RSSReader;
import ro.umsft.gabriel.rss.RSSSource;

/**
 *
 * @author ministar
 */

// Coordinates RSS downloading and database access for the UI
public class NewsService {
    
    private final NewsRepository repo;
    
    public NewsService(NewsRepository repo)
    {
        this.repo = repo;
    }
    
    // Downloads every RSS source and saves the news into the database
    private void downloadAllNews()
    {
        List<RSSSource> sources = RSSReader.loadSources();
        for(RSSSource source : sources)
        {
            List<News> fetched = RSSReader.fetchNews(source);
            System.out.println("Source name: " + source.getName() + "   " + fetched.size() + " news was fetched");
            for(News news : fetched)
            {
                repo.add(news);
            }
        }
    }
    
    // Loads the news from SQLite, downloads from RSS only if the database is empty
    public List<News> loadNews()
    {
        List<News> newsList = repo.findAll();
        if(newsList.isEmpty())
        {
            System.out.println("First start, downloading the news ...");
            downloadAllNews();
            newsList = repo.findAll();
        }
        return newsList;
    }
    
    public List<News> refresh()
    {
        downloadAllNews();
        return repo.findAll();
    }
    
    public List<News> search(String keyword)
    {
        if(keyword == null || keyword.isBlank())
        {
            return repo.findAll();
        }
        else
        {
            return repo.search(keyword.trim());
        }
    }
    
    public void addNews(News news)
    {
        repo.add(news);
    }
    
    public void updateNews(News news)
    {
        repo.update(news);
    }
    
    public void deleteNews(News news)
    {
        repo.delete(news.getSource(), news.getUrl());
    }
    
    public void clearAll()
    {
        repo.clearAll();
    }
    
    public List<News> getAllNews()
    {
        return repo.findAll();
    }
}
