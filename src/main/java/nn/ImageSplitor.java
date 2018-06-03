package nn;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageSplitor {
	public static void mainOfThis() throws Exception {

		long start = System.nanoTime();
		
		// split lines
		BufferedImage img;
//		File[] entireTextList = new File("E:\\study\\img\\entireText").listFiles();
//		int count = 0;
//		for(File f : entireTextList) {
//			img = ImageIO.read(f);
//			ArrayList<BufferedImage> imgLines = splitHor(img);
//			for(BufferedImage oneImg : imgLines) {
//				writeImg(oneImg, "E:\\study\\img\\line\\" + count + ".jpg");
//				count++;
//			}
//		}
//		
//		
//		
//		System.out.println("Done split lines");
//		
//		//split character
		ArrayList<BufferedImage> imgChar;
		File lineFolder = new File("E:\\study\\img\\line");
		File[] fileList = lineFolder.listFiles();
		for(int j = 0 ; j < fileList.length ; ++j) {
			if(j % 500 == 0) { 
				System.out.println(j);
			}
			img = ImageIO.read(fileList[j]);
			if(img.getHeight() < 5) {
				continue;
			}
			imgChar = splitVer2(img);
			for(int i = 0 ; i < imgChar.size() ; ++i) {
				writeImg(imgChar.get(i), "E:\\study\\img\\char\\" + j + "_" + i + ".jpg");
			}
		}
		long end = System.nanoTime();
		System.out.println((end -start) / 1000000);
		
//		img = ImageIO.read(new File("E:\\study\\img\\line\\1.jpg"));
//		writeImg(filterNoise(img), "E:\\study\\img\\output.jpg");
//		test();
		
		
	}

	public static void loadImg() throws Exception {
		BufferedImage img = ImageIO.read(new File("C:\\Users\\kid 4ever\\Desktop\\dkt\\Capture.PNG"));
		img = toGray(img);
		int height = img.getHeight();
		int width = img.getWidth();
		int sum;
		int min = 10000000;
		int max = 0;
		for (int i = 0; i < height; ++i) {
			sum = 0;
			for (int j = 0; j < width; ++j) {
				sum += img.getRGB(j, i) & 0xFF;
			}
			System.out.println(i + "\t" + sum);
			if (min > sum) {
				min = sum;
			}
			if (max < sum) {
				max = sum;
			}
		}
		System.out.println();
		System.out.println();
		System.out.println(min);
		System.out.println(max);

	}

	public static BufferedImage toGray(BufferedImage img) {
		BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		gray.getGraphics().drawImage(img, 0, 0, null);
		return gray;
	}

	public static void writeImg(BufferedImage img, String outputPath) throws Exception {
		File outputfile = new File(outputPath);
		ImageIO.write(img, "jpg", outputfile);
	}

	public static ArrayList<BufferedImage> splitHor(BufferedImage img) throws Exception {
		img = toGray(img);
		img = filterNoise(img);
		ArrayList<BufferedImage> rs = new ArrayList<BufferedImage>();
		int height = img.getHeight();
		int width = img.getWidth();
		int sum;
		ArrayList<Integer> grayValues = new ArrayList<Integer>();
		for (int i = 0; i < height; ++i) {
			sum = 0;
			for (int j = 0; j < width; ++j) {
				sum += img.getRGB(j, i) & 0xFF;
			}
			grayValues.add(sum);
		}
		int max = grayValues.get(0);
		for (int v : grayValues) {
			if (max < v) {
				max = v;
			}
		}
		
		double threshold = (double)max * 0.995;
		
		for(int i = 0 ; i < grayValues.size() ; ++i) {
			if(i != 0 && i != grayValues.size()-1) {
				if(grayValues.get(i) >= threshold && grayValues.get(i - 1) < threshold && grayValues.get(i + 1) < threshold
						&& (grayValues.get(i - 2) < threshold || grayValues.get(i + 2) < threshold)) {
					grayValues.remove((int)i);
					grayValues.add((int)i, (int)threshold - 1);
				}
			}
		}
		
		int start = 0;
		int end = 0;
		boolean state = true; // true if in line, false if in blank
		for(int i = 0 ; i < grayValues.size() ; ++i) {
			if(grayValues.get(i) < threshold) {
				if(state == true) {
					end = i;
				}else {
					start = i;
					state = true;
				}
			}else {
				if(state == true) {
					end  = i;
					try {
						rs.add(img.getSubimage(0, start, width, (end-start)));
					}catch(Exception e) {}
					state = false;
				}else {
					
				}
			}
		}
		return rs;

	}
	
	public static ArrayList<BufferedImage> splitVer(BufferedImage img) throws Exception {
		img = filterNoise(img);
		ArrayList<BufferedImage> rs = new ArrayList<BufferedImage>();
		img = toGray(img);
		int height = img.getHeight();
		int width = img.getWidth();
		int sum;
		ArrayList<Integer> grayValues = new ArrayList<Integer>();
		for (int i = 0; i < width; ++i) {
			sum = 0;
			for (int j = 0; j < height; ++j) {
				sum += img.getRGB(i, j) & 0xFF;
			}
//			System.out.println(i + "\t" + sum);
			grayValues.add(sum);
		}
		int max = grayValues.get(0);
		for (int v : grayValues) {
			if (max < v) {
				max = v;
			}
		}
		
		double threshold = (double)max * 0.93;
		
//		for(int i = 0 ; i < grayValues.size() ; ++i) {
//			if(i != 0 && i != grayValues.size()-1) {
//				if(grayValues.get(i) >= threshold && grayValues.get(i - 1) < threshold && grayValues.get(i + 1) < threshold
//						&& (grayValues.get(i - 2) < threshold || grayValues.get(i + 2) < threshold)) {
////					System.out.println(i);
//					grayValues.remove((int)i);
//					grayValues.add((int)i, (int)threshold - 1);
//				}
//			}
//		}
		
		
		
		int start = 0;
		int end = 0;
		boolean state = true; // true if in line, false if in blank
		for(int i = 0 ; i < grayValues.size() ; ++i) {
			if(grayValues.get(i) < threshold) {
				if(state == true) {
					end = i;
				}else {
					start = i;
					state = true;
				}
			}else {
				if(state == true) {
					end  = i;
					try {
						rs.add(img.getSubimage(start, 0, (end-start), height));
					}catch(Exception e) {
//						System.out.println(e.toString());
					}
					state = false;
				}else {
					
				}
			}
		}
		return rs;

	}

	public static BufferedImage cropImage(BufferedImage src, Rectangle rect, int x, int y) {
		BufferedImage dest = src.getSubimage(x, y, rect.width, rect.height);
		return dest;
	}
	
	public static BufferedImage filterNoise(BufferedImage img) throws Exception {
		BufferedImage rs = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
		int max = 0 , min = -1;
		int originMax = 0, originMin = 0;
		for(int w = 0 ; w < img.getWidth() ; ++w) {
			for(int h = 0 ; h < img.getHeight() ; ++h) {
				if((img.getRGB(w, h) & 0xFF) > max) {
					max = img.getRGB(w, h) & 0xFF;
					originMax = img.getRGB(w, h);
				}
				if((img.getRGB(w, h) & 0xFF) < min || min == -1) {
					min = img.getRGB(w, h) & 0xFF;
					originMin = img.getRGB(w, h);
				}
			}
		}
		int average = (max + min) / 2;
		
		for(int w = 0 ; w < rs.getWidth() ; ++w) {
			for(int h = 0 ; h < rs.getHeight() ; ++h) {
				if((img.getRGB(w, h) & 0xFF) > average) {
					rs.setRGB(w, h, originMax);
				}else if((img.getRGB(w, h) & 0xFF) <= average) {
					rs.setRGB(w, h, originMin);
				}
			}
		}
		return rs;
		
	}
	
	public static ArrayList<BufferedImage> splitVer2(BufferedImage img) throws Exception { 
		img = toGray(img);
		img = filterNoise(img);
//		System.out.println(111);
//		for(int i : listAllPixel(img)) {
//			System.out.println(i);
//		}
		ArrayList<BufferedImage> rs = new ArrayList<BufferedImage>();
		int height = img.getHeight();
		int width = img.getWidth();
		
		int fontColor = findFontColor(img);

		boolean isSplitter;
		int start = 0 ;
		while(isBlank(img,start,fontColor)) {
			start++;
		}
		for(int w = start ; w < width-1 ; ++w) {
			isSplitter = true;
			for(int h = 0 ; h < height ; ++h) {
				if((img.getRGB(w,h) & 0xFF) == fontColor && connectToRight(img, w, h) && connectToLeft(img, w, h)) {
					h = height;
					isSplitter = false;
				}
			}
			if(!isSplitter) {
				continue;
			}
			rs.add(img.getSubimage(start, 0, w - start + 1, height));
			w++;
			while(w < width && isBlank(img,w,fontColor)) {
				w++;
			}
			start = w;
		}
		return rs;
	}
	
	public static boolean connectToRight(BufferedImage img , int x , int y) {
		int pixelValue = img.getRGB(x, y);
		if(y != 0 && y != img.getHeight()-1 
				&& (img.getRGB(x+1, y) == pixelValue 
						|| img.getRGB(x+1, y+1) == pixelValue 
						|| img.getRGB(x+1, y-1) == pixelValue)) {
			return true;
		}
		if(y == 0 && (img.getRGB(x+1, y+1) == pixelValue || img.getRGB(x+1, y) == pixelValue)) {
			return true;
		}
		if(y == img.getHeight()-1 && (img.getRGB(x+1, y) == pixelValue || img.getRGB(x+1, y-1) == pixelValue)) {
			return true;
		}
		return false;
	}
	
	public static boolean connectToLeft(BufferedImage img , int x , int y) {
		if(x == 0) {
			return true;
		}
		int pixelValue = img.getRGB(x, y);
		if(y != 0 && y != img.getHeight()-1 
				&& (img.getRGB(x-1, y) == pixelValue 
						|| img.getRGB(x-1, y+1) == pixelValue 
						|| img.getRGB(x-1, y-1) == pixelValue)) {
			return true;
		}
		if(y == 0 && (img.getRGB(x-1, y+1) == pixelValue || img.getRGB(x-1, y) == pixelValue)) {
			return true;
		}
		if(y == img.getHeight()-1 && (img.getRGB(x-1, y) == pixelValue || img.getRGB(x-1, y-1) == pixelValue)) {
			return true;
		}
		return false;
	}
	
	public static int findFontColor(BufferedImage img) {
		int fontColor = 100000;
		for(int w = 0 ; w < img.getWidth() ; ++w) {
			for(int h = 0 ; h < img.getHeight() ; ++h) {
				if((img.getRGB(w, h) & 0xFF) < fontColor) {
					fontColor = img.getRGB(w, h) & 0xFF;
				}
			}
		}
		return fontColor;
	}
	
	public static boolean isBlank(BufferedImage img , int x, int fontColor) {
		for(int h = 0 ; h < img.getHeight() ; ++h) {
			if((img.getRGB(x, h) & 0xFF) == fontColor) {
				return false;
			}
		}
		return true;
	}
	
	public static void test() throws Exception{
		BufferedImage img = ImageIO.read(new File("E:\\study\\img\\char\\0_28.jpg"));
		System.out.println(findFontColor(img));
		
		for(int h = 0 ; h < img.getHeight() ; ++h) {
			System.out.println(h + "\t" + (img.getRGB(11, h) & 0xFF));
		}
		
		
		
		
		
//		int fontColor = findFontColor(img);
//		System.out.println(fontColor);
//		boolean flag;
//		for(int x = 0 ; x < img.getWidth()-1 ; ++x) {
//			flag = false;
//			System.out.println("\n\n\n" + x);
//			System.out.println(isBlank(img, x, fontColor));
//			for(int y = 0 ; y < img.getHeight() ; ++y) {
//				if((img.getRGB(x, y) & 0xFF) == fontColor && connectToRight(img, x, y)) {
//					System.out.println(x+"\t"+y+"\t"+true);
//					flag = true;
//					break;
//				}
//			}
//			if(flag == false) {
//				System.out.println(false);
//			}
//		}
	}
	
	public static ArrayList<Integer> listAllPixel(BufferedImage img){
		ArrayList<Integer> rs = new ArrayList<Integer>();
		for(int i = 0 ; i < img.getWidth() ; ++i) {
			for(int j = 0 ; j < img.getHeight() ; ++j) {
				rs.add(img.getRGB(i, j) & 0xFF);
			}
		}
		return rs;
	}

}
