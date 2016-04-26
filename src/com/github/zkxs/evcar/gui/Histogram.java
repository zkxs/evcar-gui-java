package com.github.zkxs.evcar.gui;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.github.zkxs.evcar.Util;
import com.github.zkxs.evcar.data.DataPoint;
import com.github.zkxs.evcar.data.DataReceiver;

public class Histogram extends Canvas implements DataReceiver
{
	private static final long serialVersionUID = 1L;
	
	private final static int DATA_POINT_WIDTH = 2;
	private final static int Y_AXIS_LABEL_WIDTH = 75; // width of y-axis's reserved drawing area
	private final static int Y_AXIS_WIDTH = 2; // width of y-axis
	private final static int Y_AXIS_TICK_WIDTH = 5; // width of y-axis tickmarks
	private final static int Y_AXIS_TICK_X_COORD = Y_AXIS_LABEL_WIDTH - Y_AXIS_TICK_WIDTH - Y_AXIS_WIDTH;
	private final static int Y_AXIS_LABEL_RIGHT_BOUND = Y_AXIS_TICK_X_COORD - 3;
	private final static Font Y_AXIS_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	private final static boolean DRAW_Y_AXIS_LABEL_BOUNDING_BOXES = false;
	
	// I assume this value cannot change
	private final static int MAX_DATA_POINTS = Toolkit.getDefaultToolkit().getScreenSize().width / DATA_POINT_WIDTH;
	
	private LinkedList<DataPoint> dataPoints = new LinkedList<>();
	private Queue<DataPoint> newDataQueue = new ConcurrentLinkedQueue<>();
	private GaugeParameters gaugeParameters;
	private Color dataColor;
	
	
	public Histogram(GaugeParameters gaugeParameters)
	{
		this.gaugeParameters = gaugeParameters;
		dataColor = gaugeParameters.getColor();
	}
	
	@Override
	public void update(Graphics g)
	{
		/* This magically fixes flickering. AWT is trying to be smart, but it is too smart for its
		 * own good. If you don't override this method AWT will clear the screen in a non-double buffered way.
		 */
		paint(g);
	}
	
	@Override
	public void paint(Graphics screen)
	{
		// prepare data for drawing
		{
			DataPoint dp;
			while ((dp = newDataQueue.poll()) != null)
			{
				/* We need to iterate over the list starting with the most recent element. It is much easier
				 * to iterate over lists in forward order, so lets order our data queue appropriately. New items
				 * will be added to the left, and old items will be removed from the right.
				 */
				dataPoints.addFirst(dp);
				
				/* Note that 'if' is used here instead of 'while'. This is because this check is performed every
				 * time something is inserted, guaranteeing we will never be more than one element over capacity.
				 */
				if (dataPoints.size() > MAX_DATA_POINTS)
				{
					dataPoints.removeLast();
				}
			}
			assert(dataPoints.size() <= MAX_DATA_POINTS) : "failed to destroy old data points";
		}
		
		// get size of component
		int width = getWidth();
		int height = getHeight();
		int maxY = height - 1;
		
		// find maximum and minimum values of visible data
		double maxValue = gaugeParameters.getMaximum();
		double minValue = gaugeParameters.getMinimum();
		
		{
			int position = width;
			for (DataPoint dp : dataPoints)
			{
				double value = gaugeParameters.getValue(dp);
				if (value > maxValue) maxValue = value;
				if (value < minValue) minValue = value;
				position -= DATA_POINT_WIDTH;
				if (position < Y_AXIS_LABEL_WIDTH) break;
			}
		}
		
		// set up double buffering
		BufferStrategy bufferStrategy = getBufferStrategy();
		if (bufferStrategy == null)
		{
			createBufferStrategy(2);
			bufferStrategy = getBufferStrategy();
		}
		
		// get the graphic object for the back buffer
		Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
		
		// actually perform drawing
		
		
		/* More recent points are shown at the right. Each tick the graph is effectively shifted
		 * one unit to the left.
		 */
		
		/* here we set up a shifting lens of 2 datapoints. dp1 is on the left, dp2 is on the right.
		 */
		DataPoint dpNewer;
		int xOldest, yOldest, xOlder, yOlder, xNewer, yNewer;
		
		xOlder = width - 1;
		xNewer = xOlder - DATA_POINT_WIDTH;
		
		xOldest = -1;
		yOlder = -1;
		
		ListIterator<DataPoint> iter = dataPoints.listIterator();
		Polygon poly = new Polygon();
		boolean oldestValid = false;
		
		// clear screen
		g.clearRect(0, 0, width, height); //TODO: we don't need to clear the axis part
		
		/*------------------------------------------------------------------------------------------
		 * Draw the histogram itself
		 *----------------------------------------------------------------------------------------*/
		
		if (iter.hasNext())
		{
			dpNewer = iter.next();
			yNewer = getYCoordinate(dpNewer, maxY, minValue, maxValue);
			while (iter.hasNext() && xOlder >= Y_AXIS_LABEL_WIDTH)
			{
				dpNewer = iter.next();
				yOldest = yOlder;
				yOlder = yNewer;
				yNewer = getYCoordinate(dpNewer, maxY, minValue, maxValue);
				
				poly.addPoint(xNewer, height);
				poly.addPoint(xOlder + 1, height); // +1 required because fillPolygon is for some reason smaller than drawPolygon
				poly.addPoint(xOlder + 1, yOlder);
				poly.addPoint(xNewer, yNewer);
				
				// first we draw the area below the trapezoid
				g.setColor(dataColor);
				g.fillPolygon(poly);
				poly.reset();
				
				// then we draw the top edge of the trapezoid
				if (oldestValid)
				{
					g.setColor(Color.BLACK);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable anti-aliasing
					Stroke oldStroke = g.getStroke();
					g.setStroke(new BasicStroke(2));	
					g.drawLine(xOldest, yOldest, xOlder, yOlder);
					g.setStroke(oldStroke);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // disable anti-aliasing
				}
				
				xOldest = xOlder;
				xOlder = xNewer;
				xNewer -= DATA_POINT_WIDTH;
				oldestValid = true;
			}
			
			if (oldestValid)
			{
				g.setColor(Color.BLACK);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // enable anti-aliasing
				Stroke oldStroke = g.getStroke();
				g.setStroke(new BasicStroke(2));	
				g.drawLine(xNewer + DATA_POINT_WIDTH, yNewer, xOlder + DATA_POINT_WIDTH, yOlder);
				g.setStroke(oldStroke);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // disable anti-aliasing
			}
		}
		
		/*------------------------------------------------------------------------------------------
		 * Draw the Y-axis to the left of the histogram
		 *----------------------------------------------------------------------------------------*/
		
		// clear any histogram pieces that overlap with the y-axis space
		g.clearRect(0, 0, Y_AXIS_LABEL_WIDTH, height);
		
		// draw the axis itself
		g.setColor(Color.BLACK);
		g.fillRect(Y_AXIS_LABEL_WIDTH - Y_AXIS_WIDTH, 0, Y_AXIS_WIDTH, height);
		
		// calculate y coordinates of the center tick mark
		int y0 = getYCoordinate(0, maxY, minValue, maxValue);
		
		// draw the tick marks
		g.setColor(Color.BLACK);
		g.fillRect(Y_AXIS_TICK_X_COORD, 0,                       Y_AXIS_TICK_WIDTH, Y_AXIS_WIDTH);
		g.fillRect(Y_AXIS_TICK_X_COORD, y0 - Y_AXIS_WIDTH / 2,   Y_AXIS_TICK_WIDTH, Y_AXIS_WIDTH);
		g.fillRect(Y_AXIS_TICK_X_COORD, maxY - Y_AXIS_WIDTH + 1, Y_AXIS_TICK_WIDTH, Y_AXIS_WIDTH);
		
		// draw the tick mark labels
		String maxLabel = String.format("%.0f", maxValue);
		String minLabel = String.format("%.0f", minValue);
		String zeroLabel = "0";
		
		g.setFont(Y_AXIS_FONT);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		
		Rectangle maxBounds = Util.getStringBounds(g, maxLabel);
		maxBounds.x = Y_AXIS_LABEL_RIGHT_BOUND - maxBounds.width;
		maxBounds.y = 0;
		
		Rectangle minBounds = Util.getStringBounds(g, minLabel);
		minBounds.x = Y_AXIS_LABEL_RIGHT_BOUND - minBounds.width;
		minBounds.y = maxY - minBounds.height;
		
		Rectangle zeroBounds = Util.getStringBounds(g, zeroLabel);
		zeroBounds.x = Y_AXIS_LABEL_RIGHT_BOUND - zeroBounds.width;
		zeroBounds.y = Math.min(y0 - zeroBounds.height / 2, maxY - zeroBounds.height);
		
		g.drawString(maxLabel, maxBounds.x, maxBounds.y + maxBounds.height);
		if (DRAW_Y_AXIS_LABEL_BOUNDING_BOXES) g.draw(maxBounds);
		
		// don't draw the min label if it is too close to the zero label
		if (!zeroBounds.intersects(minBounds))
		{
			g.drawString(minLabel, minBounds.x, minBounds.y + minBounds.height);
			if (DRAW_Y_AXIS_LABEL_BOUNDING_BOXES) g.draw(minBounds);
		}
		
		g.drawString(zeroLabel, zeroBounds.x, zeroBounds.y + zeroBounds.height);
		if (DRAW_Y_AXIS_LABEL_BOUNDING_BOXES) g.draw(zeroBounds);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		
		/*------------------------------------------------------------------------------------------
		 * Done with drawing
		 *----------------------------------------------------------------------------------------*/
		
		// show the back buffer
		g.dispose();
		bufferStrategy.show();
	}

	@Override
	public void acceptDataPoint(DataPoint dp)
	{
		/* In order to deal with synchronization issues, we don't actually put the data in the list at this point.
		 * We just add it to a queue that will be efficiently transfered into the real list during paint().
		 */
		newDataQueue.add(dp);
		repaint();
	}
	
	private int getYCoordinate(DataPoint dp, int maxY, double minValue, double maxValue)
	{
		return getYCoordinate(gaugeParameters.getValue(dp), maxY, minValue, maxValue);
	}
	
	private int getYCoordinate(double value, int maxY, double minValue, double maxValue)
	{
		double range = maxValue - minValue;
		double normalizedValue = (value - minValue) / range;
		int yCoord = maxY - ((int) (normalizedValue * maxY));
		return yCoord;
	}
}
