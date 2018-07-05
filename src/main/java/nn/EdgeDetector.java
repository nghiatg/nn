package nn;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import javax.imageio.ImageIO;

public class EdgeDetector {
	static int[] horizontalMask = new int[] { -1, 0, 1, -2, 0, 2, -1, 0, 1 };
	static int[] verticalMask = new int[] { -1, -2, -1, 0, 0, 0, 1, 2, 1 };

	public static void mainOfThis() throws Exception {
		String input = "cannyStuff\\81841.jpg";
		String output = "cannyStuff\\canny81841_2Threshold.jpg";
		imageCreator.writeImage(cannyDetector(ImageSplitor.toGray(loadImg(input)),200,50), output);
	}

	public static BufferedImage loadImg(String inputPath) throws Exception {
		BufferedImage img = ImageIO.read(new File(inputPath));
		return img;
	}

	public static BufferedImage imgOfDerivative(BufferedImage img) throws Exception {
		int height = img.getHeight();
		int width = img.getWidth();
		BufferedImage rs = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		for (int h = 0; h < height; ++h) {
			for (int w = 0; w < width; ++w) {
				rs.setRGB(w, h, getDerivative(img, w, h, 1));
			}
		}
		return rs;
	}

	public static BufferedImage imgOfNonNegative(BufferedImage img) throws Exception {
		int height = img.getHeight();
		int width = img.getWidth();
		BufferedImage rs = new BufferedImage(width, height, img.getType());

		for (int h = 0; h < height; ++h) {
			for (int w = 0; w < width; ++w) {
				rs.setRGB(w, h, valueAtPixel(img, w, h));
			}
		}
		return rs;
	}

	// type 1 : horizontal
	// type 2 : vertical
	public static int getDerivative(BufferedImage img, int w, int h, int type) throws Exception {
		int width = img.getWidth();
		int height = img.getHeight();
		int rs = 0;
		if (type == 1) {
			rs += (w > 0 && h > 0) ? (valueAtPixel(img, w - 1, h - 1) * horizontalMask[0]) : 0;
			rs += (h > 0 && w < width - 1) ? (valueAtPixel(img, w + 1, h - 1) * horizontalMask[2]) : 0;
			rs += (w > 0) ? (valueAtPixel(img, w - 1, h) * horizontalMask[3]) : 0;
			rs += (w < width - 1) ? (valueAtPixel(img, w + 1, h) * horizontalMask[5]) : 0;
			rs += (w > 0 && h < height - 1) ? (valueAtPixel(img, w - 1, h + 1) * horizontalMask[6]) : 0;
			rs += (w < width - 1 && h < height - 1) ? (valueAtPixel(img, w + 1, h + 1) * horizontalMask[8]) : 0;
		} else if (type == 2) {
			rs += (w > 0 && h > 0) ? (valueAtPixel(img, w - 1, h - 1) * verticalMask[0]) : 0;
			rs += (h > 0) ? (valueAtPixel(img, w, h - 1) * verticalMask[1]) : 0;
			rs += (h > 0 && w < width - 1) ? (valueAtPixel(img, w + 1, h - 1) * verticalMask[2]) : 0;
			rs += (w > 0 && h < height - 1) ? (valueAtPixel(img, w - 1, h + 1) * verticalMask[6]) : 0;
			rs += (h < height - 1) ? (valueAtPixel(img, w, h + 1) * verticalMask[7]) : 0;
			rs += (w < width - 1 && h < height - 1) ? (valueAtPixel(img, w + 1, h + 1) * verticalMask[8]) : 0;
		} else {
			return 1 / 0;
		}
		return rs;
	}

	public static int valueAtPixel(BufferedImage img, int w, int h) throws Exception {
		return img.getRGB(w, h) & 0xFF;
		// return img.getRGB(w, h);
	}

	// input: gray images;
	public static BufferedImage cannyDetector(BufferedImage img, int upperThreshold, int lowerThreshold) throws Exception {
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage rs = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		ArrayList<Double> magnitude = new ArrayList<Double>();
		ArrayList<Double> direction = new ArrayList<Double>();

		// {x,y} <--> {w,h}
		ArrayList<Integer> idEdgePoint = new ArrayList<Integer>();

		for (int h = 0; h < height; ++h) {
			for (int w = 0; w < width; ++w) {
				magnitude.add(Math.sqrt(getDerivative(img, w, h, 1) * getDerivative(img, w, h, 1)
						+ getDerivative(img, w, h, 2) * getDerivative(img, w, h, 2)));
				direction.add(Math.toDegrees(
						Math.atan((double) getDerivative(img, w, h, 2) / (double) getDerivative(img, w, h, 1))));
				// System.out.println(magnitude.get(magnitude.size() - 1));
			}
		}

		// get thin edge with upper threshold

		for (int h = 0; h < height; ++h) {
			for (int w = 0; w < width; ++w) {
				int index = h * width + w;
				if (direction.get(index) <= 112.5 && direction.get(index) > 67.5) {
					double top = getInfo(magnitude, direction, w, h - 1, width, height)[0];
					double bottom = getInfo(magnitude, direction, w, h + 1, width, height)[0];
					if (top < magnitude.get(index) && bottom < magnitude.get(index)
							&& magnitude.get(index) > upperThreshold) {
						idEdgePoint.add(index);
					}
				} else if (direction.get(index) <= 67.5 && direction.get(index) > 22.5) {
					double topLeft = getInfo(magnitude, direction, w - 1, h - 1, width, height)[0];
					double bottomRight = getInfo(magnitude, direction, w + 1, h + 1, width, height)[0];
					if (topLeft < magnitude.get(index) && bottomRight < magnitude.get(index)
							&& magnitude.get(index) > upperThreshold) {
						idEdgePoint.add(index);
					}
				} else if (direction.get(index) <= 22.5 || direction.get(index) > 157.5) {
					double left = getInfo(magnitude, direction, w - 1, h, width, height)[0];
					double right = getInfo(magnitude, direction, w + 1, h, width, height)[0];
					if (left < magnitude.get(index) && right < magnitude.get(index)
							&& magnitude.get(index) > upperThreshold) {
						idEdgePoint.add(index);
					}
				} else if (direction.get(index) <= 157.5 && direction.get(index) > 112.5) {
					double topRight = getInfo(magnitude, direction, w + 1, h - 1, width, height)[0];
					double bottomLeft = getInfo(magnitude, direction, w - 1, h + 1, width, height)[0];
					if (topRight < magnitude.get(index) && bottomLeft < magnitude.get(index)
							&& magnitude.get(index) > upperThreshold) {
						idEdgePoint.add(index);
					}
				}
			}
		}

		// more edge with lower threshold
		ArrayList<Integer> extendablePoints = new ArrayList<Integer>();
		extendablePoints.addAll(idEdgePoint);
		ArrayList<Integer> additionalEdgePoints = new ArrayList<Integer>();
		HashSet<Integer> notEdgePoint = new HashSet<Integer>();
		int loopCount = 0;
		while (true) {
			additionalEdgePoints.clear();
			for (int index : extendablePoints) {
				int w = getWH(width, index)[0];
				int h = getWH(width, index)[1];
				if (!idEdgePoint.contains(index)) {
					continue;
				}

				if (direction.get(index) <= 22.5 || direction.get(index) > 157.5) {
					double[] top = getInfo(magnitude, direction, w, h - 1, width, height);
					double[] bottom = getInfo(magnitude, direction, w, h + 1, width, height);

					if ((top[1] <= 22.5 || top[1] > 157.5) && !idEdgePoint.contains(getIndex(width, h - 1, w))
							&& !notEdgePoint.contains(getIndex(width, h - 1, w))) {
						if (top[0] > getInfo(magnitude, direction, w - 1, h - 1, width, height)[0]
								&& top[0] > getInfo(magnitude, direction, w + 1, h - 1, width, height)[0]
								&& top[0] > lowerThreshold) {
							additionalEdgePoints.add(getIndex(width, h - 1, w));
							//extendablePoints.add(getIndex(width, h - 1, w));
						} else {
							notEdgePoint.add(getIndex(width, h - 1, w));
						}
					}
					if ((bottom[1] <= 22.5 || bottom[1] > 157.5) && !idEdgePoint.contains(getIndex(width, h + 1, w))
							&& !notEdgePoint.contains(getIndex(width, h + 1, w))) {
						if (bottom[0] > getInfo(magnitude, direction, w - 1, h + 1, width, height)[0]
								&& bottom[0] > getInfo(magnitude, direction, w + 1, h + 1, width, height)[0]
								&& bottom[0] > lowerThreshold) {
							additionalEdgePoints.add(getIndex(width, h + 1, w));
							//extendablePoints.add(getIndex(width, h + 1, w));
						} else {
							notEdgePoint.add(getIndex(width, h + 1, w));
						}
					}
				} else if (direction.get(index) <= 67.5) {
					double[] topRight = getInfo(magnitude, direction, w + 1, h - 1, width, height);
					double[] bottomLeft = getInfo(magnitude, direction, w - 1, h + 1, width, height);
					if (topRight[1] <= 67.5 && topRight[1] > 22.5
							&& !idEdgePoint.contains(getIndex(width, h - 1, w + 1))
							&& !notEdgePoint.contains(getIndex(width, h - 1, w + 1))) {
						if (topRight[0] > lowerThreshold
								&& topRight[0] > getInfo(magnitude, direction, w, h - 2, width, height)[0]
								&& topRight[0] > getInfo(magnitude, direction, w + 2, h, width, height)[0]) {
							additionalEdgePoints.add(getIndex(width, h - 1, w + 1));
							//extendablePoints.add(getIndex(width, h - 1, w + 1));
						} else {
							notEdgePoint.add(getIndex(width, h - 1, w + 1));
						}
					}

					if (bottomLeft[1] <= 67.5 && bottomLeft[1] > 22.5
							&& !idEdgePoint.contains(getIndex(width, h + 1, w - 1))
							&& !notEdgePoint.contains(getIndex(width, h + 1, w - 1))) {
						if (bottomLeft[0] > lowerThreshold
								&& bottomLeft[0] > getInfo(magnitude, direction, w, h + 2, width, height)[0]
								&& bottomLeft[0] > getInfo(magnitude, direction, w - 2, h, width, height)[0]) {
							additionalEdgePoints.add(getIndex(width, h + 1, w - 1));
							//extendablePoints.add(getIndex(width, h + 1, w - 1));
						} else {
							notEdgePoint.add(getIndex(width, h + 1, w - 1));
						}
					}
				} else if (direction.get(index) <= 112.5) {
					double[] left = getInfo(magnitude, direction, w - 1, h, width, height);
					double[] right = getInfo(magnitude, direction, w + 1, h, width, height);
					if (left[1] <= 112.5 && left[1] > 67.5 && !idEdgePoint.contains(getIndex(width, h, w - 1))
							&& !notEdgePoint.contains(getIndex(width, h, w - 1))) {
						if (left[0] > lowerThreshold
								&& left[0] > getInfo(magnitude, direction, w - 1, h - 1, width, height)[0]
								&& left[0] > getInfo(magnitude, direction, w - 1, h + 1, width, height)[0]) {
							additionalEdgePoints.add(getIndex(width, h, w - 1));
							//extendablePoints.add(getIndex(width, h, w - 1));
						} else {
							notEdgePoint.add(getIndex(width, h, w - 1));
						}
					}
					if (right[1] <= 112.5 && right[1] > 67.5 && !idEdgePoint.contains(getIndex(width, h, w + 1))
							&& !notEdgePoint.contains(getIndex(width, h, w + 1))) {
						if (right[0] > lowerThreshold
								&& right[0] > getInfo(magnitude, direction, w + 1, h + 1, width, height)[0]
								&& right[0] > getInfo(magnitude, direction, w + 1, h - 1, width, height)[0]) {
							additionalEdgePoints.add(getIndex(width, h, w + 1));
							//extendablePoints.add(getIndex(width, h, w + 1));
						} else {
							notEdgePoint.add(getIndex(width, h, w + 1));
						}
					}
				} else if (direction.get(index) <= 157.5) {
					double[] topLeft = getInfo(magnitude, direction, w - 1, h - 1, width, height);
					double[] bottomRight = getInfo(magnitude, direction, w + 1, h + 1, width, height);
					if (topLeft[1] <= 157.5 && topLeft[1] > 112.5
							&& !idEdgePoint.contains(getIndex(width, h - 1, w - 1))
							&& !notEdgePoint.contains(getIndex(width, h - 1, w - 1))) {
						if (topLeft[0] > lowerThreshold
								&& topLeft[0] > getInfo(magnitude, direction, w, h - 2, width, height)[0]
								&& topLeft[0] > getInfo(magnitude, direction, w - 2, h, width, height)[0]) {
							additionalEdgePoints.add(getIndex(width, h - 1, w - 1));
							//extendablePoints.add(getIndex(width, h - 1, w - 1));
						} else {
							notEdgePoint.add(getIndex(width, h - 1, w - 1));
						}
					}
					if (bottomRight[1] <= 157.5 && bottomRight[1] > 112.5
							&& !idEdgePoint.contains(getIndex(width, h + 1, w + 1))
							&& !notEdgePoint.contains(getIndex(width, h + 1, w + 1))) {
						if (bottomRight[0] > lowerThreshold
								&& bottomRight[0] > getInfo(magnitude, direction, w + 2, h, width, height)[0]
								&& bottomRight[0] > getInfo(magnitude, direction, w, h + 2, width, height)[0]) {
							additionalEdgePoints.add(getIndex(width, h + 1, w + 1));
							//extendablePoints.add(getIndex(width, h + 1, w + 1));
						} else {
							notEdgePoint.add(getIndex(width, h + 1, w + 1));
						}
					}
				}
			}
			extendablePoints.clear();

			// add edge points
			idEdgePoint.addAll(additionalEdgePoints);
			extendablePoints.addAll(additionalEdgePoints);

			// loop tracking
			loopCount++;
			System.out.println("loop : " + loopCount + "\nAddition : " + additionalEdgePoints.size() + "\n\n\n");

			// stop condition
			if (additionalEdgePoints.size() == 0) {
				break;
			}
		}
		
		System.out.println(idEdgePoint.size());

		// redraw edges
		for (int h = 0; h < height; ++h) {
			for (int w = 0; w < width; ++w) {
				if (idEdgePoint.contains(getIndex(width, h, w))) {
					rs.setRGB(w, h, Color.BLACK.getRGB());
				} else {
					rs.setRGB(w, h, Color.WHITE.getRGB());
				}
			}
		}

		return rs;
	}

	public static int getIndex(int width, int h, int w) {
		return width * h + w;
	}

	// {magnitude , direction}
	public static double[] getInfo(ArrayList<Double> magnitude, ArrayList<Double> direction, int w, int h, int width,
			int height) throws Exception {
		double[] rs = new double[2];
		if (h < 0 || w < 0 || h >= height || w >= width) {
			rs[0] = 0;
			rs[1] = 0;
		} else {
			rs[0] = magnitude.get(getIndex(width, h, w));
			rs[1] = direction.get(getIndex(width, h, w));
		}
		return rs;
	}

	// {w,h}
	public static int[] getWH(int width, int index) {
		int[] rs = new int[2];
		rs[1] = index / width;
		rs[0] = index % width;
		return rs;
	}

}
