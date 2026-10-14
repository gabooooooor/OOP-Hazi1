package ro.umsft.gabriel.databases;

import java.util.*;
import java.sql.*;
import ro.umsft.gabriel.news.News;
import ro.umsft.gabriel.news.Event;

public class DatabaseManager {

    private static final Database database = new Database();

    // Inserts one news item, duplicates (same source and url) are ignored
    public static void insertNews(News news) {
        //Insetring the new data
        String sql = "INSERT OR IGNORE INTO news (title, description, source,"
                + "author, publisher, publish_date, url, creation_date,"
                + "last_modified_date, language, cover_image) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = database.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getDescription());
            ps.setString(3, news.getSource());
            ps.setString(4, news.getAuthor());
            ps.setString(5, news.getPublisher());
            ps.setTimestamp(6, news.getPublishDate());
            ps.setString(7, news.getUrl());
            ps.setTimestamp(8, news.getCreationDate());
            ps.setTimestamp(9, news.getLastModifiedDate());
            ps.setString(10, news.getLanguage());
            ps.setString(11, news.getCoverImage());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while inserting news: " + e.getMessage());
        }
    }

    // Updates a news item, the key (source, url) identifies which row to change
    public static void updateNews(News news) {
        news.touch();
        String sql = "UPDATE news SET title = ?, description = ?, author = ?,"
                + " publisher = ?, publish_date = ?, last_modified_date = ?,"
                + " language = ?, cover_image = ? WHERE source = ? AND url = ?";
        try (Connection conn = database.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, news.getTitle());
            ps.setString(2, news.getDescription());
            ps.setString(3, news.getAuthor());
            ps.setString(4, news.getPublisher());
            ps.setTimestamp(5, news.getPublishDate());
            ps.setTimestamp(6, news.getLastModifiedDate());
            ps.setString(7, news.getLanguage());
            ps.setString(8, news.getCoverImage());
            ps.setString(9, news.getSource());
            ps.setString(10, news.getUrl());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while updating news: " + e.getMessage());
        }
    }

    // Deletes the news item identified by source and url
    public static void deleteNews(String source, String url) {
        String sql = "DELETE FROM news WHERE source = ?"
                + " AND url = ?";
        try (Connection conn = database.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, source);
            ps.setString(2, url);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while deleting news: " + e.getMessage());
        }
    }
    
    // Builds one News object from the current row of the ResultSet
    private static News readNews(ResultSet rs) throws SQLException
    {
        return new News(
        rs.getString("title"),
        rs.getString("description"),
        rs.getString("source"),
        rs.getString("author"),
        rs.getString("publisher"),
        rs.getTimestamp("publish_date"),
        rs.getString("url"),
        rs.getTimestamp("creation_date"),
        rs.getTimestamp("last_modified_date"),
        rs.getString("language"),
        rs.getString("cover_image")
        );
    }

    // Reads every news item from the database, newest first
    public static List<News> getAllNews() {
        List<News> newsList = new ArrayList<>();
        String sql = "SELECT * FROM news ORDER BY publish_date DESC";
        try (Connection conn = database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                newsList.add(readNews(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error while getting the news: " + e.getMessage());
        }
        return newsList;
    }
    
    // Returns the news whose title or description contains the keyword
    public static List<News> searchNews(String keyword)
    {
        String sql = "SELECT * FROM news WHERE title LIKE ? OR description LIKE ? "
                + "ORDER BY publish_date DESC";
        String pattern = "%" + keyword + "%";
        List<News> newsList = new ArrayList<>();
        try(Connection conn = database.connect();
                PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try(ResultSet rs = ps.executeQuery())
            {
                while(rs.next())
                {
                    newsList.add(readNews(rs));
                }
            }
            
        }
        catch(SQLException e)
            {
                System.out.println("Error while searching news: " + e.getMessage());
            }
        return newsList;
    }
    
    // Deletes every news and event from the database
    public static void clearAll()
    {
        try(Connection conn = database.connect();
                Statement stmt = conn.createStatement())
        {
            stmt.executeUpdate("DELETE FROM news");
            stmt.executeUpdate("DELETE FROM events");
        }
        catch(SQLException e)
        {
            System.out.println("Error while clearing the database: " + e.getMessage());
        }
    }
    
    // Returns the news identified by source and url, or null if it does not exist
    public static News getNews(String source, String url)
    {
        String sql = "SELECT * FROM news WHERE source = ? "
                + "AND url = ?";
        try(Connection conn = database.connect();
                PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, source);
            ps.setString(2, url);
            try(ResultSet rs = ps.executeQuery())
            {
                if(rs.next())
                {
                    return readNews(rs);
                }
                else {return null;}
            }
            
        }
        catch(SQLException e)
        {
            System.out.println("Error while getting news: " + e.getMessage());
        }
        return null;
    }

    // Inserts one event, duplicates (same title, date time and location) are ignored
    public static void insertEvent(Event event) {
        //Insetring the new data
        String sql = "INSERT OR IGNORE INTO events (title, description, source,"
                + "author, publisher, publish_date, url, creation_date,"
                + "last_modified_date, language, cover_image, event_date_time, location) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = database.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getTitle());
            ps.setString(2, event.getDescription());
            ps.setString(3, event.getSource());
            ps.setString(4, event.getAuthor());
            ps.setString(5, event.getPublisher());
            ps.setTimestamp(6, event.getPublishDate());
            ps.setString(7, event.getUrl());
            ps.setTimestamp(8, event.getCreationDate());
            ps.setTimestamp(9, event.getLastModifiedDate());
            ps.setString(10, event.getLanguage());
            ps.setString(11, event.getCoverImage());
            ps.setTimestamp(12, event.getEventDateTime());
            ps.setString(13, event.getLocation());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while inserting events: " + e.getMessage());
        }
    }

    // Reads every event from the database, earliest event first
    public static List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date_time ASC";
        try (Connection conn = database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Event event = new Event(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("source"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getTimestamp("publish_date"),
                        rs.getString("url"),
                        rs.getTimestamp("creation_date"),
                        rs.getTimestamp("last_modified_date"),
                        rs.getString("language"),
                        rs.getString("cover_image"),
                        rs.getTimestamp("event_date_time"),
                        rs.getString("location"));
                eventList.add(event);
            }
        } catch (SQLException e) {
            System.out.println("Error while getting the events: " + e.getMessage());
        }
        return eventList;
    }
    
    // Updates an event, the key (title, event_datetime, location) identifies the row
    public static void updateEvent(Event event) {
        event.touch();
        String sql = "UPDATE events SET description = ?, source = ?, author = ?,"
                + " publisher = ?, publish_date = ?, url = ?, last_modified_date = ?,"
                + " language = ?, cover_image = ? "
                + "WHERE title = ? AND event_date_time = ? AND location = ?";
        try (Connection conn = database.connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.getDescription());
            ps.setString(2, event.getSource());
            ps.setString(3, event.getAuthor());
            ps.setString(4, event.getPublisher());
            ps.setTimestamp(5, event.getPublishDate());
            ps.setString(6, event.getUrl());
            ps.setTimestamp(7, event.getLastModifiedDate());
            ps.setString(8, event.getLanguage());
            ps.setString(9, event.getCoverImage());
            ps.setString(10, event.getTitle());
            ps.setTimestamp(11, event.getEventDateTime());
            ps.setString(12, event.getLocation());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while updating events: " + e.getMessage());
        }
    }
    
    // Deletes the event identified by title, date time and location
    public static void deleteEvent(String title,Timestamp dateTime, String location)
    {
        String sql = "DELETE FROM events WHERE title = ? "
                + "AND event_date_time = ? AND location = ?";
        try(Connection conn = database.connect();
                PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, title);
            ps.setTimestamp(2, dateTime);
            ps.setString(3, location);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error while deleting events: " + e.getMessage());
        }
    }
}
