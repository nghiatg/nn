package nn;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class imageCreator {
	public static void mainOfImg() throws Exception{
		for(String s : getFonts()) {
			System.out.println(s);
		}
		create();
		
	}
	
	public static ArrayList<String> getFonts() throws Exception { 
		String[] fonts =  GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		ArrayList<String> uniFonts = getUnicodeFonts();
		ArrayList<String> rs = new ArrayList<String>();
		for(String f : fonts) {
			if(uniFonts.contains(f.toLowerCase())) {
				rs.add(f);
			}
		}
		return rs;
	}
	
	
	// unicode fonts at lower-case
	public static ArrayList<String> getUnicodeFonts() throws Exception{
		ArrayList<String> rs = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("E:\\study\\text\\uni_font.txt"));
		String line = br.readLine();
		while(line != null) {
			rs.add(line.toLowerCase());
			line = br.readLine();
		}
		br.close();
		return rs;
	}
	
	public static void create() throws Exception  { 
		ArrayList<String> fonts = getFonts();		
		System.out.println(fonts.size());

		BufferedImage img = new BufferedImage(1000,4700,BufferedImage.TYPE_BYTE_GRAY);
		Graphics2D g = (Graphics2D)img.getGraphics();
		g.setPaint(new Color(255,255,255));
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.setColor(Color.BLACK);
		g.setFont(new Font(fonts.get(0), Font.PLAIN, 25));
		int offset = 100;
		for(String s : getContentWrite()) {
			g.drawString(s, 100, offset);
			offset += 30;
		}
		writeImage(img, "E:\\study\\img\\entireText\\font0.jpg");
	}
	public static void writeImage(BufferedImage img, String outputPath) throws Exception {
		File outputfile = new File(outputPath);
		ImageIO.write(img, "jpg", outputfile);
	}
	
	public static ArrayList<String> getContentWrite() throws Exception { 
		ArrayList<String> rs = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader("E:\\study\\text\\data.txt"));
		String line = br.readLine();
		while(line != null) {
			if(line.contains("title : ")) {
				rs.add(line.substring(8));
			}
			if(rs.size() > 150) {
				break;
			}
			line = br.readLine();
		}
		br.close();
		return rs;
	}

}
