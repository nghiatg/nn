package nn;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageSplitor {
	public static void mainOfThis() throws Exception {
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
		
		System.out.println("Done split lines");
		
		//split character
		ArrayList<BufferedImage> imgChar;
		File lineFolder = new File("E:\\study\\img\\line");
		File[] fileList = lineFolder.listFiles();
		for(int j = 0 ; j < fileList.length ; ++j) {
			if(j % 500 == 0) { 
				System.out.println(j);
			}
			img = ImageIO.read(fileList[j]);
			imgChar = splitVer(img);
			for(int i = 0 ; i < imgChar.size() ; ++i) {
				writeImg(imgChar.get(i), "E:\\study\\img\\char\\" + j + "_" + i + ".jpg");
			}
		}
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
		ArrayList<BufferedImage> rs = new ArrayList<BufferedImage>();
		img = toGray(img);
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
		
		double threshold = (double)max * 0.98;
		
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
	
	public static ArrayList<double[]> handleList(int threshold, ArrayList<Integer> values) {
		ArrayList<double[]> rs = new ArrayList<double[]>();
		int end;
		int former;
		while(!values.isEmpty()) {
			while(values.get(0) >= threshold) {
				values.remove((int)0);
			}
		}
		
		return rs;	
	}

}
