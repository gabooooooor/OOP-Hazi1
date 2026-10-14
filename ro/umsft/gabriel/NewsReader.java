/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package ro.umsft.gabriel;

import ro.umsft.gabriel.databases.Database;
import ro.umsft.gabriel.service.NewsService;
import ro.umsft.gabriel.ui.MainWindow;
import ro.umsft.gabriel.repository.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.Color;

/**
 *
 * @author ministar
 */
public class NewsReader {

    public static void main(String[] args) {
        
        UIManager.put("nimbusBase", new Color(30, 80, 150));
        try
        {
            for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("Error while setting the look and feel: " + e.getMessage());
        }
        
        new Database().createTables();
        NewsRepository repository = new SQLiteNewsRepository();
        NewsService newsService = new NewsService(repository);
        SwingUtilities.invokeLater(() -> new MainWindow(newsService).setVisible(true));
    }
}
