package org.dea.fimgstoreclient.utils;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * a simple image display panel
 */
public class ImagePanel extends JPanel{
	private static final long serialVersionUID = -5582787151369689975L;
	private BufferedImage image;

    public ImagePanel(String imgPath) {
       try {                
          image = ImageIO.read(new File(imgPath));
       } catch (IOException ex) {
            ex.printStackTrace();
       }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }
    
    public static void showImage(final String path) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	JFrame frame = new JFrame();
                // frame settings like size, close operation etc.
            	ImagePanel panel = new ImagePanel(path);
                // init textfields and buttons
                // add listeners or whatever
                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
                panel.setLayout(layout);
                // layout settings goes here
                frame.add(panel);
                frame.setVisible(true);
                
                frame.setSize(800, 600);
                
            }
        });
    	
    }

}