package org.lggl.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.VolatileImage;

import javax.swing.JPanel;

class WindowPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Window win;
	private boolean stretch;

	public boolean isStretch() {
		return stretch;
	}

	public void setStretch(boolean stretch) {
		this.stretch = stretch;
	}

	WindowPanel(Window w) {
		setBackground(Color.BLACK);
		win = w;
	}
	
	private VolatileImage img;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!stretch) {
			Window.getRenderer().render(win, (Graphics2D) g);
		} else {
			if (img == null) {
				img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(win.getWidth(), win.getHeight());
			}
			Window.getRenderer().render(win, (Graphics2D) img.createGraphics());
			g.drawImage(img, 0, 0, win.getWidth(), win.getHeight(), null);
		}
	}
}
