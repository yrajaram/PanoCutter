package com.herakles.photo;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DiceImage {
    public static void main(String[] args) throws IOException {
    	/*
    	 * from: http://www.montclairphoto.com/resolution.html
    	 * 
    	 * Our Frontier print sizes are set slightly above 300 dpi:
			size    -     pixels
			3.5x5 - 1050x1524
			4x5.4 - 1228x1654
			4x6 - 1228x1818
			5x7 - 1524x2138
			8x10 - 2436x3036
			8x12 - 2434x3638
			10x15 - 3036x4536
    	 */
    	int Height5x7 = 2138, Length5x7 = 1524; // Make a collage of 5x7 images
    	

        File file = new File("SFO.jpg");
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis);
        
        int picHeight = image.getHeight();
        int picLength = image.getWidth();
        float scaleFactor = 0.0f;
        if (picHeight<picLength) {
        	Height5x7 ^= Length5x7; Length5x7 ^= Height5x7; Height5x7 ^= Length5x7; // matching dimensions
        	scaleFactor = picHeight/((float)Height5x7);
        } else {
        	scaleFactor = picLength/((float)Length5x7);
        }
        
        System.out.println("Scale Factor:"+scaleFactor+" "+(int)scaleFactor);

        if (scaleFactor!=1){
        	image = scaleImage(image, ((float)((int)scaleFactor)/scaleFactor));
        }    

        int rows = image.getHeight()/Height5x7; 
        int cols = image.getWidth()/Length5x7;
        
        rows=((image.getHeight() % Height5x7)!=0)?++rows:rows;
        cols = ((image.getWidth()%Length5x7)!=0)?++cols:cols;
        
        int chunks = rows * cols;

        System.out.println("Height:"+image.getHeight()+" "+Height5x7);
        System.out.println("Length:"+image.getWidth()+" "+Length5x7);
        
        System.out.println("Rows:"+rows+" Cols:"+cols);
           
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks
                imgs[count] = new BufferedImage(Length5x7, Height5x7, image.getType());

                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, Length5x7, Height5x7, Length5x7 * y, Height5x7 * x, Length5x7 * y + Length5x7, Height5x7 * x + Height5x7, null);
                gr.dispose();
            }
        }
        System.out.println("Diced");

        //write chunks as jpgs
        for (int i = 0; i < imgs.length; i++) {
            ImageIO.write(imgs[i], "jpg", new File(file.getName().substring(0, file.getName().indexOf("."))+"-tile-" + i + ".jpg"));
        }
        System.out.println("Tile images created");
    }
    
    static BufferedImage scaleImage(BufferedImage before, float scale) {
    	System.out.println("Scale:"+scale);
    	int w = (int) (before.getWidth()*scale);
    	int h = (int) (before.getHeight()*scale);
    	BufferedImage after = new BufferedImage(w, h, before.getType());
    	AffineTransform at = new AffineTransform();
    	at.scale(scale, scale);
    	AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    	after = scaleOp.filter(before, after);
    	System.out.println("Scaled from "+before.getHeight()+" to "+after.getHeight());
    	return after;
    }
}
