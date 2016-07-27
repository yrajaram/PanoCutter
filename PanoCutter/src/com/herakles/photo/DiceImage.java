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
	private static enum TileSize {	// Available sizes for the tiles
		p4x6, p5x7, p8x12, p12x18  
	}
	
	//---------------Change only the three below to get desired results
	static final TileSize PhotoSize = TileSize.p4x6;   // What is each tile size?
	static final int RequiredScaleFactor = 1;          // By how much the image should be scaled?
	static final String ImageFileName = "SFO.jpg";     // What is the image file name?
	
    public static void main(String[] args) throws IOException {
    	
    	/*
    	 * from: http://www.montclairphoto.com/resolution.html
    	 * 
    	 * Our Frontier print sizes are set slightly above 300 dpi:
			size	-	pixels
			3.5x5	-	1050x1524
			4x5.4	-	1228x1654
			4x6		-	1228x1818
			5x7		-	1524x2138
			8x10	-	2436x3036
			8x12	-	2434x3638
			10x15	-	3036x4536
    	 */
    	int tileHeight = 2138, tileLength = 1524; // Make a collage of 5x7 images
    	
    	switch (PhotoSize){
    	case p4x6:
    		tileHeight = 1818; tileLength = 1228;
    		break;
    	case p5x7:
    		tileHeight = 2138; tileLength = 1524;
    		break;
    	case p8x12:
    		tileHeight = 3638; tileLength = 2434;
    		break;
    	case p12x18:
    		tileHeight = 5454; tileLength = 3684;
    		break;
    	}
        File file = new File(ImageFileName);
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis);
        
        int picHeight = image.getHeight();
        int picLength = image.getWidth();
        float scaleFactor = 0.0f;
        if (picHeight<picLength) {
        	tileHeight ^= tileLength; tileLength ^= tileHeight; tileHeight ^= tileLength; // matching dimensions
        	scaleFactor = picHeight/((float)tileHeight);
        } else {
        	scaleFactor = picLength/((float)tileLength);
        }
        
        if (scaleFactor>=1){
        	image = scaleImage(image, RequiredScaleFactor*((float)((int)scaleFactor)/scaleFactor));
        } else {
        	image = scaleImage(image, RequiredScaleFactor/scaleFactor);
        }

        int rows = image.getHeight()/tileHeight; 
        int cols = image.getWidth()/tileLength;
        
        rows = ((image.getHeight() % tileHeight)!=0)?++rows:rows;
        cols = ((image.getWidth() % tileLength)!=0)?++cols:cols;
                
        System.out.println("Rows:"+rows+" Cols:"+cols);
           
        BufferedImage img = new BufferedImage(tileLength, tileHeight, image.getType());
        Graphics2D gr = null;
        gr = img.createGraphics();
    	
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
            	if (y == cols-1) {
            		gr.clearRect(0, 0, tileLength, tileLength);
            	}
                gr.drawImage(image, 0, 0, tileLength, tileHeight, tileLength * y, tileHeight * x, tileLength * y + tileLength, tileHeight * x + tileHeight, null);
                ImageIO.write(img, "jpg", new File(file.getName().substring(0, file.getName().indexOf("."))+"-tile-" + x + y + ".jpg"));
            }
        }
        gr.dispose();
        System.out.println("Diced");
    }
    
    static BufferedImage scaleImage(BufferedImage before, float scale) {
    	int w = (int) (before.getWidth()*scale);
    	int h = (int) (before.getHeight()*scale);
    	BufferedImage after = new BufferedImage(w, h, before.getType());
    	AffineTransform at = new AffineTransform();
    	at.scale(scale, scale);
    	AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    	after = scaleOp.filter(before, after);
    	System.out.println("Scaled from "+before.getHeight()+"px to "+after.getHeight()+"px");
    	return after;
    }
}
