package com.github.zkxs.evcar.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.github.zkxs.evcar.Driver;
import com.github.zkxs.evcar.data.DataProvider;

public class GraphFrame extends JFrame
{
	private static final long serialVersionUID = 0L;
	private JPanel contentPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		Driver.main(args);
	}
	
	/**
	 * Create the frame.
	 */
	public GraphFrame(DataProvider dataProvider)
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		Histogram currentHistogram = new Histogram(GaugeParameters.CURRENT_GAGUE_PARAMETERS);
		contentPane.add(currentHistogram, BorderLayout.CENTER);
		
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleFullscreen");
		getRootPane().getActionMap().put("toggleFullscreen", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				toggleFullscreen();
			}
		});
		
		dataProvider.addListener(currentHistogram);
		
		Timer updateTimer = new Timer("Update Timer");
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run()
			{
				dataProvider.dispatchData();
			}
		}, 0, 1000);
	}
	
	private void toggleFullscreen()
	{
		System.out.println("hey");
	}
	
}
