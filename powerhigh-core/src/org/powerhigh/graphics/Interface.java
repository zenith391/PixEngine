package org.powerhigh.graphics;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.powerhigh.Camera;
import org.powerhigh.ViewportManager;
import org.powerhigh.graphics.renderers.IRenderer;
import org.powerhigh.graphics.renderers.lightning.Lightning;
import org.powerhigh.input.Keyboard;
import org.powerhigh.input.Mouse;
import org.powerhigh.objects.Container;
import org.powerhigh.objects.GameObject;

/**
 * 
 *
 */
public abstract class Interface {

	protected Keyboard input = new Keyboard(this);
	protected Mouse mouse = new Mouse(-1, -1, this);
	private ArrayList<GameObject> objects = new ArrayList<GameObject>();
	private GameObject focusedObj;
	private WindowEventThread thread = new WindowEventThread(this);
	private ViewportManager viewport;
	private Graphics customGraphics;
	
	private Container objectContainer;
	
	private Camera camera;

	private int vW, vH;
	
	public Container getObjectContainer() {
		return objectContainer;
	}

	public Graphics getCustomGraphics() {
		return customGraphics;
	}

	public void setCustomGraphics(Graphics customGraphics) {
		this.customGraphics = customGraphics;
	}

	private static IRenderer render;

	public static IRenderer getRenderer() {
		return render;
	}

	public static void setRenderer(IRenderer render) {
		Interface.render = render;
	}

	static {
		try {
			if (true)
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

	}

	public boolean shouldRender(GameObject obj) {
		return render.shouldRender(this, obj);
	}

	public int getFPS() {
		return thread.getFPS();
	}

	public float getSPF() {
		return 1.0f / getFPS();
	}

	public abstract void setBackground(Color color);
	public abstract Color getBackground();

	public ViewportManager getViewportManager() {
		return viewport;
	}

	public void setViewportManager(ViewportManager manager) {
		viewport = manager;
	}
	
	public WindowEventThread getEventThread() {
		return thread;
	}

	/**
	 * Set whether or not if the Window is visible. If visible equals to true, it's
	 * will execute <code>show()</code>. If visible equals to false, it's will
	 * execute <code>hide()</code>.
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible) {
		if (visible == true) {
			show();
		}
		if (visible == false) {
			hide();
		}
	}

	protected void init() {
		if (render == null)
			setRenderer(new Lightning());
		thread.start();
		objectContainer = new Container();
		camera = new Camera();
	}

	public void setViewport(int x, int y, int width, int height) {
		objectContainer.setSize(width, height);
		vW = width;
		vH = height;
	}

	public Rectangle getViewport() {
		return new Rectangle(0, 0, vW, vH);
	}

	public abstract void show();
	public abstract void hide();

	public Keyboard getKeyboard() {
		return input;
	}

	public abstract boolean isCloseRequested();
	public abstract boolean isVisible();

	public void add(GameObject obj) {
		objectContainer.add(obj);
	}
	
	public Camera getCamera() {
		return camera;
	}
	
	public void setCamera(Camera cam) {
		camera = cam;
	}

	public void update() {
		if (viewport != null) {
			
			// Re-working on this
			
//			Rectangle view = viewport.getViewport(this);
//			if (viewport.getSpecialProperties().containsKey("stretchToWindow")) {
//				view = new Rectangle(getWidth(), getHeight());
//			}
//			if (!view.equals(panel.getBounds())) {
//				if (!viewport.getSpecialProperties().containsKey("stretchToWindow")) {
//					setViewport(view.x, view.y, view.width, view.height);
//					panel.setStretch(false);
//				} else {
//					if (viewport.getSpecialProperties().containsKey("stretchToWindow")) {
//						setViewport(view.x, view.y, getWidth(), getHeight());
//						panel.setStretch(true);
//					}
//				}
//			}
		}
	}

	public GameObject[] getObjects() {
		return objectContainer.getObjects();
	}

	public void remove(GameObject obj) {
		try {
			objectContainer.remove(obj);
		} catch (Exception e) {
			throw e;
		}
	}

	public abstract int getWidth();
	public abstract int getHeight();

	public Mouse getMouse() {
		return mouse;
	}

	public void fireEvent(String type, Object... args) {
		if (type.equals("mousePressed")) {
			GameObject[] a = getObjects();
			int mx = (int) args[0];
			int my = (int) args[1];
			focusedObj = null;
			for (GameObject b : a) {

				if (mx > b.getX() && my > b.getY() && mx < b.getX() + b.getWidth() && my < b.getY() + b.getHeight()) {
					focusedObj = b;
					break;
				}
			}
		}
		if (focusedObj != null)
			focusedObj.onEvent(type, args);
	}

	public void removeAll() {
		for (GameObject obj : objects) {
			remove(obj);
		}
	}
}