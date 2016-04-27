package com.github.zkxs.evcar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.github.zkxs.evcar.data.*;
import com.github.zkxs.evcar.gui.*;

public class Driver
{	
	/** Time in ms between updates of GUI components */
	private static final long UPDATE_PERIOD = 50;
	private static final Font STRIP_LABEL_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 50);
	private static final Font GAUGE_LABEL_FONT = Assets.getFont7seg().deriveFont(50f);
	private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(300, 500);
	private Rectangle windowBounds = new Rectangle(100, 100, 1024, 640);
	
	public static final String VERSION_NUMBER = "0.1.2";
	public static final String APPLICATION_NAME = "Electric Vehicle Demonstrator";
	private static final String WINDOW_TITLE = APPLICATION_NAME + " v" + VERSION_NUMBER;
	
	private static final GaugeParameters[] stripTypes = {
		GaugeParameters.CURRENT_GAGUE_PARAMETERS,
		GaugeParameters.VOLTAGE_GAGUE_PARAMETERS,
		GaugeParameters.RPM_GAGUE_PARAMETERS
	};
	
	private JFrame frame;
	private JPanel contentPane;
	private GraphicsDevice monitor;
	private boolean isFullscreen = false; // this is also the default mode, so set it to true to start in fullscreen
	private Point windowLocation;
	
	/**
	 * Main method (where the program starts)
	 * @param args Command line arguments
	 */
	public static void main(String[] args)
	{
		Assets.getFont7seg();
		new Driver();
	}
	
	/**
	 * Starting point of the entire application. Intentionally done in instance code in case it is
	 * useful to have multiple application instances running within one VM (for example, this is
	 * nice when unit testing)
	 */
	public Driver()
	{
		// fixes a flickering problem caused by AWT doing things it really doesn't need to
		System.setProperty("sun.awt.noerasebackground", "true");
		
		DataProvider dataProvider = new RandomDataProvider();
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					contentPane = new JPanel();
					contentPane.setBorder(new EmptyBorder(5, 0, 5, 0));
					contentPane.setLayout(new GridLayout(0, 1, 0, 10));
					
					
					for (GaugeParameters gp : stripTypes)
					{
						JPanel currentPane = createGaugeStrip(dataProvider, gp);
						contentPane.add(currentPane);
					}
					
					contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullscreen");
					contentPane.getActionMap().put("toggleFullscreen", new AbstractAction() {
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e)
						{
							toggleFullscreen();
						}
					});
					
					
					// set up timer to send data points to the various GUI components
					Timer updateTimer = new Timer("Update Timer");
					updateTimer.scheduleAtFixedRate(new TimerTask() {
						@Override
						public void run()
						{
							dataProvider.dispatchData();
						}
					}, 0, UPDATE_PERIOD);
					
					frame = getNewFrame(isFullscreen);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	private JPanel createGaugeStrip(DataProvider dp, GaugeParameters gp)
	{
		Histogram histogram = new Histogram(gp);
		dp.addListener(histogram);
		
		GaugeLabel gaugeLabel = new GaugeLabel(gp);
		gaugeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		gaugeLabel.setFont(GAUGE_LABEL_FONT);
		dp.addListener(gaugeLabel);
		
		Gauge gauge = new Gauge(gp);
		dp.addListener(gauge);
		
		JLabel stripLabel = new JLabel(gp.getLabel());
		stripLabel.setFont(STRIP_LABEL_FONT);
		stripLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel gaugeBox = new JPanel();
		gaugeBox.setLayout(new BorderLayout(0, 0));
		gaugeBox.add(gauge, BorderLayout.CENTER);
		gaugeBox.add(gaugeLabel, BorderLayout.SOUTH);
		gaugeBox.add(stripLabel, BorderLayout.NORTH);
		
		JPanel gaugeStrip = new JPanel();
		gaugeStrip.setLayout(new BorderLayout(0, 0));
		gaugeStrip.add(histogram, BorderLayout.CENTER);
		gaugeStrip.add(gaugeBox, BorderLayout.WEST);
		return gaugeStrip;
	}
	
	/**
	 * @return A new, empty windowed frame
	 */
	private JFrame createWindowedFrame()
	{
		JFrame frame = new JFrame(WINDOW_TITLE);
		frame.setUndecorated(false);
		frame.setBounds(windowBounds);
		if (windowLocation != null)
		{
			frame.setLocation(windowLocation);
		}
		frame.setMinimumSize(MINIMUM_FRAME_SIZE);
		frame.setResizable(true);
		return frame;
	}
	
	/**
	 * @return A new, empty fullscreen frame
	 */
	private JFrame createFullscreenFrame()
	{
		assert(monitor == null);
		monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		JFrame frame = new JFrame(WINDOW_TITLE);
		frame.setUndecorated(true);
		frame.setSize(new Dimension(monitor.getDisplayMode().getWidth(), monitor.getDisplayMode().getHeight()));
		frame.setLocation(0, 0);
		monitor.setFullScreenWindow(frame);
		frame.setResizable(false);
		return frame;
	}
	
	/**
	 * Toggle between windowed and fullscreen modes
	 */
	private void toggleFullscreen()
	{
		destroyFrame();
		
		isFullscreen = !isFullscreen;
		frame = getNewFrame(isFullscreen);
	}
	
	/**
	 * Create a new frame of the appropriate type. Note that this will also set the frame as visible.
	 * @param isFullscreen whether or not the frame should be fullscreen
	 * @return The newly constructed frame
	 */
	private JFrame getNewFrame(boolean isFullscreen)
	{
		JFrame frame;
		
		if (isFullscreen)
		{
			frame = createFullscreenFrame();
		}
		else
		{
			frame = createWindowedFrame();
		}
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(contentPane);
		frame.setVisible(true);
		
		return frame;
	}
	
	/**
	 * Destroy the current frame, saving any of its parameters we may need later.
	 */
	private void destroyFrame()
	{
		if (monitor != null)
		{
			monitor.setFullScreenWindow(null);
			monitor = null;
		}
		
		if (!isFullscreen)
		{
			// back up parameters of window
			frame.getBounds(windowBounds);
			frame.getLocation(windowLocation);
		}
		
		frame.removeAll();
		frame.setVisible(false);
		frame.dispose();
		frame = null;
	}
}
