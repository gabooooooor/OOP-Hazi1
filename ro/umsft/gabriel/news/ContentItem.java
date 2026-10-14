/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ro.umsft.gabriel.news;

import java.sql.Timestamp;

/**
 *
 * @author ministar
 */
public abstract class ContentItem {
    
    //Declaring the class's fields
    private final String title;
    private String description;
    private final String source;
    private String author;
    private String publisher;
    private Timestamp publishDate;
    private final String url;
    private final Timestamp creationDate;
    private Timestamp lastModifiedDate;
    private final String language;
    private String coverImage;
    
    public ContentItem(String title, String description,
            String source, String author, String publisher,
            Timestamp publishDate,
            String url, Timestamp creationDate,
            Timestamp lastModifiedDate, String language, 
            String coverImage)
    {
        // Validate the required fields before storing them
        if(title == null || title.isBlank()){
            throw new IllegalArgumentException("Title is required");
        }
        if(source == null || source.isBlank()){
            throw new IllegalArgumentException("Source is required");
        }
        if(url == null || url.isBlank()){
            throw new IllegalArgumentException("Url is required");
        }
        if(language == null || language.isBlank()){
            throw new IllegalArgumentException("Language is required");
        }
        this.title = title;
        this.description = description;
        this.source = source;
        this.author = author;
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.url = url;
        this.creationDate = creationDate;
        this.lastModifiedDate = lastModifiedDate;
        this.language = language;
        this.coverImage = coverImage;
    }
    
    //getters
    public String getTitle(){return title;}
    public String getDescription(){return description;}
    public String getSource(){return source;}
    public String getAuthor(){return author;}
    public String getPublisher(){return publisher;}
    public Timestamp getPublishDate(){return publishDate;}
    public String getUrl(){return url;}
    public Timestamp getCreationDate(){return creationDate;}
    public Timestamp getLastModifiedDate(){return lastModifiedDate;}
    public String getLanguage(){return language;}
    public String getCoverImage(){return coverImage;}
    
    
    public void touch()
    {
        this.lastModifiedDate = new Timestamp(System.currentTimeMillis());
    }
    
    public void updateContent(String description,
            String author, String publisher,
            String coverImage)
    {
        this.description = description;
        this.author = author;
        this.publisher = publisher;
        this.coverImage = coverImage;
        touch();
    }
    
    @Override
    public String toString()
    {
        return title;
    }
}
