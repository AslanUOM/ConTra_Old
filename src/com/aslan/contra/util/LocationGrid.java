package com.aslan.contra.util;

public class LocationGrid {
	protected static int gridSize = 256;
	private static final double MinLatitude = -85.05112878;
	private static final double MaxLatitude = 85.05112878;
	private static final double MinLongitude = -180;
	private static final double MaxLongitude = 180;

	private static double clip(final double n, final double minValue, final double maxValue) {
		return Math.min(Math.max(n, minValue), maxValue);
	}

	public static int gridSize(final int levelOfDetail) {
		return gridSize * levelOfDetail;
	}

	public static int[] toCartesianCoordinate(double latitude, double longitude, final int levelOfDetail) {

		latitude = clip(latitude, MinLatitude, MaxLatitude);
		longitude = clip(longitude, MinLongitude, MaxLongitude);

		final double x = (longitude + 180) / 360;
		final double sinLatitude = Math.sin(latitude * Math.PI / 180);
		final double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

		final int mapSize = gridSize(levelOfDetail);
		int xx = (int) clip(x * mapSize + 0.5, 0, mapSize - 1);
		int yy = (int) clip(y * mapSize + 0.5, 0, mapSize - 1);
		return new int[] { xx, yy };
	}

	public static double[] toLatitudeLongitude(final int pixelX, final int pixelY, final int levelOfDetail) {

		final double mapSize = gridSize(levelOfDetail);
		final double x = (clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
		final double y = 0.5 - (clip(pixelY, 0, mapSize - 1) / mapSize);

		final double latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
		final double longitude = 360 * x;

		return new double[] { latitude, longitude };
	}

	public static int toGridNumber(int pixelX, int pixelY, int levelOfDetail) {
		int mapSize = gridSize(levelOfDetail);
		int n = pixelX + pixelY * mapSize;
		return n;
	}

	public static int toGridNumber(double latitude, double longitude, int levelOfDetail) {
		int mapSize = gridSize(levelOfDetail);
		int[] pixelXY = toCartesianCoordinate(latitude, longitude, levelOfDetail);
		int n = pixelXY[0] + pixelXY[1] * mapSize;
		return n;
	}

	public static int[] toCartesianCoordinate(int n, int levelOfDetail) {
		int mapSize = gridSize(levelOfDetail);
		int x = n % mapSize;
		int y = (n - x) / mapSize;
		return new int[] { x, y };
	}

}
