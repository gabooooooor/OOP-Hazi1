/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ro.umsft.gabriel.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;
import ro.umsft.gabriel.news.CoverImageResolver;
import ro.umsft.gabriel.service.NewsService;
import ro.umsft.gabriel.news.News;
/**
 *
 * @author ministar
 */

// Main window: news list on the left, details on the right
public class MainWindow extends JFrame{
    
    private final NewsService newsService;
    private final DefaultListModel<News> listModel = new DefaultListModel<>();
    private final JList<News> newsList = new JList<>(listModel);
    private final JPanel detailPanel = new JPanel();
    
    private final JButton addButton = new JButton("Add");
    private final JButton editButton = new JButton("Edit");
    private final JButton deleteButton = new JButton("Delete");
    
    private final JTextField searchField = new JTextField(18);
    private final JButton searchButton = new JButton("Search");
    
    private final JButton refreshButton = new JButton("Refresh");
    private final JLabel loadingLabel = new JLabel(" ");
    
    private final JButton clearButton = new JButton("Clear DB");
    
    private final JProgressBar loadingBar = new JProgressBar();
    
    public MainWindow(NewsService newsService)
    {
        this.newsService = newsService;
        setTitle("News Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        
        loadingBar.setIndeterminate(true);
        loadingBar.setVisible(false);
        
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(searchField);
        toolbar.add(searchButton);
        toolbar.add(refreshButton);
        toolbar.add(loadingLabel);
        toolbar.add(loadingBar);
        toolbar.add(clearButton);
        add(toolbar, BorderLayout.NORTH);
        
        add(buildSplitPane(), BorderLayout.CENTER);
        newsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        newsList.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus)
            {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if(value instanceof News)
                {
                    News news = (News) value;
                    
                    String title = news.getTitle().replace("&", "&amp;").replace("<", "&lt;");
                    
                    label.setText("<html><div style='width:280px'><b>" + title + "</b><br>"
                    + "<span style='color:gray; font-size:9px;'>" + news.getSource() + "</span></div></html>");
                    
                    label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                }
                return label;
            }
        });
        newsList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()){
                showDetails(newsList.getSelectedValue());
            }
        });
        
        addButton.addActionListener(e -> onAdd());
        editButton.addActionListener(e -> onEdit());
        deleteButton.addActionListener(e -> onDelete());
        searchButton.addActionListener(e -> onSearch());
        refreshButton.addActionListener(e -> onRefresh());
        clearButton.addActionListener(e -> onClear());
        loadInBackground(false);
    }
    
    private JSplitPane buildSplitPane()
    {
        JScrollPane left = new JScrollPane(newsList);
        JPanel right = detailPanel;
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        splitPane.setDividerLocation(366);
        splitPane.setResizeWeight(0.33);
        return splitPane;
    }
    
    private void reloadNews()
    {
        listModel.clear();
        for(News news : newsService.getAllNews())
        {
            listModel.addElement(news);
        }
    }
    
    private void showDetails(News news)
    {
        detailPanel.removeAll();
        detailPanel.setLayout(new BorderLayout());
        if(news == null)
        {
            detailPanel.add(new JLabel("Select a news item"));
            detailPanel.revalidate();
            detailPanel.repaint();
            return;
        }
        String imagePath = CoverImageResolver.resolve(news.getCoverImage());
        ImageIcon original = new ImageIcon(imagePath);
        JLabel imageLabel;
        if(original.getIconWidth() <= 0)
        {
            imageLabel = new JLabel("No cover image");
        }
        else
        {
            Image scaled = original.getImage().getScaledInstance(400, -1, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(scaled));
        }
        
        
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        String description = news.getDescription() == null
                ? ""
                : news.getDescription().replaceAll("<[^>]+>", " ").trim();
        text.setText(
                news.getTitle() + "\n\n" 
                        + description + "\n\n" 
                                + "Author: " + news.getAuthor() + "\n\n" 
                                        + "Source: " + news.getSource() + "\n\n" 
                                                + "URL: " + news.getUrl()
        );
        
        detailPanel.add(imageLabel, BorderLayout.NORTH);
        detailPanel.add(new JScrollPane(text), BorderLayout.CENTER);
        detailPanel.revalidate();
        detailPanel.repaint();
    }
    
    private void addFormRow(JPanel panel, int row, String labelText, JComponent field)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(4, 4, 4, 4);
        
        c.gridx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel(labelText), c);
        
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, c);
    }
    
    private News showNewsForm(News existing)
    {
        JTextField title = new JTextField();
        JTextArea description = new JTextArea(5, 30);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        JTextField source = new JTextField();
        JTextField author = new JTextField();
        JTextField publisher = new JTextField();
        JTextField url = new JTextField();
        JTextField language = new JTextField();
        
        title.setColumns(30);
        source.setColumns(30);
        author.setColumns(30);
        publisher.setColumns(30);
        url.setColumns(30);
        language.setColumns(30);
        
        if(existing != null)
        {
            title.setText(existing.getTitle());
            description.setText(existing.getDescription());
            source.setText(existing.getSource());
            author.setText(existing.getAuthor());
            publisher.setText(existing.getPublisher());
            url.setText(existing.getUrl());
            language.setText(existing.getLanguage());
            source.setEditable(false);
            url.setEditable(false);
        }
        
        JPanel panel = new JPanel(new GridBagLayout());
        addFormRow(panel, 0, "Title:", title);
        addFormRow(panel, 1, "Description:", new JScrollPane(description));
        addFormRow(panel, 2, "Source:", source);
        addFormRow(panel, 3, "Author:", author);
        addFormRow(panel, 4, "Publisher:", publisher);
        addFormRow(panel, 5, "URL:", url);
        addFormRow(panel, 6, "Language:", language);   
        
        int result = JOptionPane.showConfirmDialog(
            this, panel, existing == null ? "Add news" : "Edit news",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result != JOptionPane.OK_OPTION){return null;}
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return new News(
            title.getText(),
            description.getText(),
            source.getText(),
            author.getText(),
            publisher.getText(),
            existing == null ? now : existing.getPublishDate(),
            url.getText(),
            existing == null ? now : existing.getCreationDate(),
            now,
            language.getText(),
            CoverImageResolver.resolve(existing == null ? null : existing.getCoverImage())
    );
    }
    
    private void onAdd()
    {
        try
        {
            News news = showNewsForm(null);
            if( news == null){return;}
            newsService.addNews(news);
            reloadNews();
        }
        catch(IllegalArgumentException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onEdit()
    {
        News selected = newsList.getSelectedValue();
        if(selected == null)
        {
            JOptionPane.showMessageDialog(this, "Select a news item first.");
            return;
        }
        try
        {
            News news = showNewsForm(selected);
            if( news == null){return;}
            newsService.updateNews(news);
            reloadNews();
        }
        catch(IllegalArgumentException e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onDelete()
    {
        News selected = newsList.getSelectedValue();
        if(selected == null)
        {
            JOptionPane.showMessageDialog(this, "Select a news item first.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(
            this, "Delete this news?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) { return; }
        newsService.deleteNews(selected);
        reloadNews();
    }
    
    private void onSearch()
    {
        listModel.clear();
        for(News news : newsService.search(searchField.getText()))
        {
            listModel.addElement(news);
        }
    }
    
    private void setLoading(boolean loading)
    {
        addButton.setEnabled(!loading);
        editButton.setEnabled(!loading);
        deleteButton.setEnabled(!loading);
        searchButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);
        clearButton.setEnabled(!loading);
        loadingBar.setVisible(loading);
        loadingLabel.setText(loading ? "Loading" : " ");
    }
    
    private void loadInBackground(boolean isRefresh)
    {
        setLoading(true);
        SwingWorker<List<News>, Void> worker = new SwingWorker<>(){
            @Override
            protected List<News> doInBackground()
            {
                if(isRefresh)
                {
                    return newsService.refresh();
                }
                return newsService.loadNews();
            }
            
            @Override
            protected void done()
            {
                try
                {
                    listModel.clear();
                    for(News news : get())
                    {
                        listModel.addElement(news);
                    }
                }
                catch(Exception e)
                {
                    JOptionPane.showMessageDialog(MainWindow.this, e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                setLoading(false);
            }
        };
        worker.execute();
    }
    
    private void onRefresh()
    {
        loadInBackground(true);
    }
    
    private void onClear()
    {
        int ok = JOptionPane.showConfirmDialog(
            this,
            "Delete all news from the database?",
            "Clear DB",
            JOptionPane.YES_NO_OPTION);
        if(ok != JOptionPane.YES_OPTION){return;}
        newsService.clearAll();
        listModel.clear();
        showDetails(null);
    }
}
