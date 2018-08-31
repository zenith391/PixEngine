package org.powerhigh.graphics.renderers.lightning;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.WeakHashMap;

import org.powerhigh.graphics.PostProcessor;
import org.powerhigh.graphics.Interface;
import org.powerhigh.graphics.renderers.IRenderer;
import org.powerhigh.objects.GameObject;
import org.powerhigh.utils.debug.DebugLogger;

public final class Lightning implements IRenderer {

	private WeakHashMap<Interface, LightningRenderBuffer> buffers = new WeakHashMap<>();
	private boolean paused;
	private boolean debug = false;
	private boolean pp = true;
	private Interface lastWin;
	private ArrayList<PostProcessor> postProcessors = new ArrayList<>();

	private void render(Interface win, Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g;
		BufferedImage buff = null;
		if (pp) {
			buff = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(rect.width, rect.height);
			g2 = buff.createGraphics();
		}
		g2.setColor(win.getBackground());
		g2.fillRect(0, 0, rect.width, rect.height);
		g2.rotate(Math.toRadians(win.getCamera().getRotation()), win.getWidth()/2, win.getHeight()/2);
		g2.translate(win.getCamera().getXOffset(), win.getCamera().getYOffset());
		g2.scale(win.getCamera().getScale(), win.getCamera().getScale());
		for (GameObject obj : win.getObjects()) {
			if (obj.getX() < rect.width && obj.getY() < rect.height) {
				if (obj.isVisible()) {
					AffineTransform old = g2.getTransform();
					g2.rotate(Math.toRadians(obj.getRotation()), obj.getX()+(obj.getWidth() / 2), obj.getY()+(obj.getHeight() / 2));
					
					obj.paint(g2, win);
					
					float a = obj.getMaterial().reflectance;
					a /= 2;
					g2.setColor(new Color(.0f, 0f, 0f, a));
					//g2.fillRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
					
					g2.setTransform(old);
				}
			}
		}
		if (pp) {
			for (PostProcessor pp : postProcessors) {
				buff = pp.process(buff);
			}
			g.fillRect(0, 0, rect.width, rect.height);
			g.drawImage(buff, 0, 0, null);
		}
	}

	@Override
	public void render(Interface win, Graphics2D g) {
		lastWin = win;
		render(win, g, win.getViewport());
	}

	public void drawOnBuffer(Interface win) {
		LightningRenderBuffer buffer = buffers.get(win);
		BufferedImage img = buffer.getImage();
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(win.getBackground());
		g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
		render(win, g2d, win.getViewport());
	}

	@Override
	public boolean shouldRender(Interface w, GameObject obj) {
		Rectangle rect = w.getViewport();
		return shouldRender(rect, obj);
	}

	private boolean shouldRender(Rectangle rect, GameObject obj) {
		return obj.isVisible() && obj.getX() + obj.getWidth() >= 0 && obj.getY() + obj.getHeight() >= 0
				&& obj.getX() <= rect.getWidth() && obj.getY() <= rect.getHeight();
	}

	@Override
	public void unpause() {
		paused = false;
	}
	
	public void turnOffDebug() {
		debug = false;
	}
	
	public void turnOnDebug() {
		debug = true;
	}

	@Override
	public void pause() {
		drawOnBuffer(lastWin);
		paused = true;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}
	
	public void setUsePostProcessing(boolean enable) {
		pp = enable;
	}

	@Override
	public void addPostProcessor(PostProcessor processor) {
		postProcessors.add(processor);
	}

	@Override
	public PostProcessor[] getPostProcessors() {
		return postProcessors.toArray(new PostProcessor[postProcessors.size()]);
	}

	@Override
	public boolean isUsingPostProcessing() {
		return pp;
	}

}