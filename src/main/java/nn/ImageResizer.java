package nn;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageResizer {
	public static void mainOfThis() throws Exception { 
		String fixedPath = "E:\\study\\img\\charFixed\\";
		File charNotFixed = new File("E:\\study\\img\\char");
		BufferedImage img;
		for(File f : charNotFixed.listFiles()) {
			img = ImageIO.read(f);
			imageCreator.writeImage(resize(img,30,30), fixedPath + f.getName());
		}
	}
	
	public static BufferedImage resize(BufferedImage img , int width , int height) throws Exception {
		return convert(img.getScaledInstance(width, height, Image.SCALE_DEFAULT),img.getType());
	}
	
	public static BufferedImage convert(Image img, int imgType) {
		BufferedImage rs = new BufferedImage(img.getWidth(null), img.getHeight(null), imgType);
		Graphics2D bGr = rs.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
		return rs;
	}
	
	

}
