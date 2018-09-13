package Militia;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * <pre>
 * Militia
 *   |_ Window.java
 * 1. 개요 : 창을 띄우는 부분
 * 2. 작성일 : 2017. 6. 16.
 * </pre>
 *
 * @author : jjjj
 * @version : 1.0
 */
public class Window {

	public static void main(String[] args) {
		// JFrame에 연결할 GLCanvas 생성 및 게임 부착(?)
		GLProfile glProfile = GLProfile.get(GLProfile.GL2);
		GLCapabilities glCapabilities = new GLCapabilities(glProfile);
		GLCanvas glcanvas = new GLCanvas(glCapabilities);
		Game game = new Game();
		glcanvas.addGLEventListener(game);
		glcanvas.addKeyListener(game);
		glcanvas.setSize(1500, 900);
		// FPSAnimator로 렌더링 루프 제작
		FPSAnimator animator = new FPSAnimator(glcanvas, 100);
		animator.start();
		// Frame으로 윈도우 생성
		Frame frame = new Frame("I am a window");
		frame.add(glcanvas);
		frame.setSize(frame.getPreferredSize());
		frame.setVisible(true);
		// 윈도우를 끄면 프로그램 종료
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

}
