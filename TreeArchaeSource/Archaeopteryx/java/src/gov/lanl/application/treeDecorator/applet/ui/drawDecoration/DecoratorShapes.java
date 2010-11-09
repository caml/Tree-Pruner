package gov.lanl.application.treeDecorator.applet.ui.drawDecoration;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class DecoratorShapes {
	
	// SHAPES
	
	public static void drawTriangleHollow(Graphics g, Point location, int height,
			int width, Color color) {

		g.setColor(color);
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		Point point2 = new Point(location.x + halfWidth, location.y
				+ halfHeight);
		Point point3 = new Point(location.x - halfWidth, location.y
				+ halfHeight);
		Point point1 = new Point(location.x, location.y - halfHeight);
		g.drawLine(point1.x, point1.y, point2.x, point2.y);
		g.drawLine(point1.x, point1.y, point3.x, point3.y);
		g.drawLine(point2.x, point2.y, point3.x, point3.y);
	}

	public static void drawTriangleFilled(Graphics g, Point location, int height,
			int width, Color color) {
		g.setColor(color);
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		Point point2 = new Point(location.x + halfWidth, location.y
				+ halfHeight);
		Point point3 = new Point(location.x - halfWidth, location.y
				+ halfHeight);
		Point point1 = new Point(location.x, location.y - halfHeight);
		Polygon poly = new Polygon();
		poly.addPoint(point1.x, point1.y);
		poly.addPoint(point2.x, point2.y);
		poly.addPoint(point3.x, point3.y);
		g.fillPolygon(poly);
	}

	public static void drawInvertedTriangleHollow(Graphics g, Point location,
			int height, int width, Color color) {

		g.setColor(color);
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		Point point2 = new Point(location.x + halfWidth, location.y
				- halfHeight);
		Point point3 = new Point(location.x - halfWidth, location.y
				- halfHeight);
		Point point1 = new Point(location.x, location.y + halfHeight);
		g.drawLine(point1.x, point1.y, point2.x, point2.y);
		g.drawLine(point1.x, point1.y, point3.x, point3.y);
		g.drawLine(point2.x, point2.y, point3.x, point3.y);
	}

	public static void drawInvertedTriangleFilled(Graphics g, Point location,
			int height, int width, Color color) {
		g.setColor(color);
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		Point point2 = new Point(location.x + halfWidth, location.y
				- halfHeight);
		Point point3 = new Point(location.x - halfWidth, location.y
				- halfHeight);
		Point point1 = new Point(location.x, location.y + halfHeight);
		Polygon poly = new Polygon();
		poly.addPoint(point1.x, point1.y);
		poly.addPoint(point2.x, point2.y);
		poly.addPoint(point3.x, point3.y);
		g.fillPolygon(poly);
	}

	public static void drawSquareHollow(Graphics g, Point location, int side,
			Color color) {
		g.setColor(color);
		int halfSide = side / 2;

		Point point1 = new Point(location.x - halfSide, location.y + halfSide);
		Point point2 = new Point(location.x + halfSide, location.y + halfSide);
		Point point3 = new Point(location.x - halfSide, location.y - halfSide);
		Point point4 = new Point(location.x + halfSide, location.y - halfSide);
		g.drawLine(point1.x, point1.y, point2.x, point2.y);
		g.drawLine(point1.x, point1.y, point3.x, point3.y);
		g.drawLine(point2.x, point2.y, point4.x, point4.y);
		g.drawLine(point3.x, point3.y, point4.x, point4.y);
	}

	public static void drawSquareFilled(Graphics g, Point location, int side,
			Color color) {
		g.setColor(color);
		int halfSide = side / 2;

		Point point1 = new Point(location.x - halfSide, location.y + halfSide);
		Point point2 = new Point(location.x + halfSide, location.y + halfSide);
		Point point3 = new Point(location.x - halfSide, location.y - halfSide);
		Point point4 = new Point(location.x + halfSide, location.y - halfSide);
		Polygon poly = new Polygon();
		poly.addPoint(point1.x, point1.y);
		poly.addPoint(point2.x, point2.y);
		poly.addPoint(point4.x, point4.y);
		poly.addPoint(point3.x, point3.y);

		g.fillPolygon(poly);
	}

	public static void drawCircleHollow(Graphics g, Point location, int diameter,
			Color color) {
		g.setColor(color);
		int radius = diameter / 2;
		g.drawOval(location.x - radius, location.y - radius, diameter,
						diameter);
	}

	public static void drawCircleFilled(Graphics g, Point location, int diameter,
			Color color) {
		g.setColor(color);
		int radius = diameter / 2;
		g.fillOval(location.x - radius, location.y - radius, diameter,
						diameter);
	}

	public static void drawDiamondHollow(Graphics g, Point location, int median,
			Color color) {
		g.setColor(color);
		int halfMedian = median / 2;

		Point point1 = new Point(location.x, location.y + halfMedian);
		Point point2 = new Point(location.x + halfMedian, location.y);
		Point point3 = new Point(location.x, location.y - halfMedian);
		Point point4 = new Point(location.x - halfMedian, location.y);
		g.drawLine(point1.x, point1.y, point2.x, point2.y);
		g.drawLine(point2.x, point2.y, point3.x, point3.y);
		g.drawLine(point3.x, point3.y, point4.x, point4.y);
		g.drawLine(point1.x, point1.y, point4.x, point4.y);
	}

	public static void drawDiamondFilled(Graphics g, Point location, int median,
			Color color) {
		g.setColor(color);
		int halfMedian = median / 2;

		Point point1 = new Point(location.x, location.y + halfMedian);
		Point point2 = new Point(location.x + halfMedian, location.y);
		Point point3 = new Point(location.x, location.y - halfMedian);
		Point point4 = new Point(location.x - halfMedian, location.y);
		Polygon poly = new Polygon();

		poly.addPoint(point1.x, point1.y);
		poly.addPoint(point2.x, point2.y);
		poly.addPoint(point3.x, point3.y);
		poly.addPoint(point4.x, point4.y);

		g.fillPolygon(poly);
	}
	
	//for legend
	public static void drawTriangleFilledpPointingToRight(Graphics g, Point location,
			int height, int width, Color color) {
		g.setColor(color);
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		Point point2 = new Point(location.x + halfWidth, location.y);
		Point point3 = new Point(location.x - halfWidth, location.y
				- halfHeight);
		Point point1 = new Point(location.x-halfWidth, location.y + halfHeight);
		Polygon poly = new Polygon();
		poly.addPoint(point1.x, point1.y);
		poly.addPoint(point2.x, point2.y);
		poly.addPoint(point3.x, point3.y);
		g.fillPolygon(poly);
	}
}
