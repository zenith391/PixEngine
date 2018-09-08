package org.powerhigh.swing;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import org.powerhigh.graphics.Interface;

public class SwingInterfaceImpl extends Interface {

	private boolean legacyFullscreen;
	private JFrame win;
	private boolean fullscreen;
	private int fullscreenWidth, fullscreenHeight;
	private GraphicsDevice device;
	
	public SwingInterfaceImpl() {
		init();
	}
	
	protected void init() {
		super.init();
		win = new JFrame();
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		fullscreenWidth = device.getDisplayMode().getWidth();
		fullscreenHeight = device.getDisplayMode().getHeight();
		fullscreen = false;
	}
	
	@Override
	public void setBackground(Color color) {
		win.setBackground(color);
	}

	@Override
	public Color getBackground() {
		return win.getBackground();
	}
	
	public void setFullscreen(boolean fullscreen) {
		try {
			if (System.getProperty("sun.java2d.opengl", "false").equals("true")) {
				legacyFullscreen = true;
			}
			if (fullscreen == true) {
				if (device.isFullScreenSupported() && !legacyFullscreen) {
					device.setFullScreenWindow(win);
					DisplayMode found = device.getDisplayMode();
					if (fullscreenWidth != 0 && fullscreenHeight != 0) {
						for (DisplayMode mode : device.getDisplayModes()) {
							if ((mode.getWidth() >= fullscreenWidth && mode.getWidth() < found.getWidth())) {
								if (mode.getHeight() >= fullscreenHeight && mode.getHeight() < found.getHeight()) {
									found = mode;
								}
							}
						}
					}
					if (!found.equals(device.getDisplayMode())) {
						device.setDisplayMode(found);
					}

					win.createBufferStrategy(1);

				} else {
					win.dispose();

					win.setUndecorated(true);
					win.setExtendedState(JFrame.MAXIMIZED_BOTH);

					show();
				}
			} else {
				if (device.isFullScreenSupported() && !legacyFullscreen) {
					device.setFullScreenWindow(null);
					fullscreen = false;
				} else {
					win.dispose();

					win.setUndecorated(false);
					win.setExtendedState(JFrame.NORMAL);

					show();
				}
			}
			this.fullscreen = fullscreen;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isFullscreen() {
		return fullscreen;
	}

	@Override
	public void show() {
		win.setVisible(true);
	}

	@Override
	public void hide() {
		win.setVisible(false);
	}

	@Override
	public boolean isCloseRequested() {
		return false;
	}

	@Override
	public boolean isVisible() {
		return win.isVisible();
	}

	@Override
	public int getWidth() {
		return win.getWidth();
	}

	@Override
	public int getHeight() {
		return win.getHeight();
	}

}
