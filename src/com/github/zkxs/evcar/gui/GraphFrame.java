package com.github.zkxs.evcar.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class GraphFrame extends JFrame
{
	private static final long serialVersionUID = 0L;
	private JPanel contentPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		// fixes a flickering problem
		System.setProperty("sun.awt.noerasebackground", "true");
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					GraphFrame frame = new GraphFrame();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public GraphFrame()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		Histogram histogram = new Histogram();
		contentPane.add(histogram, BorderLayout.CENTER);
	}
	
}
