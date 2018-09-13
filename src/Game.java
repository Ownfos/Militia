package Militia;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * <pre>
 * Militia
 *   |_ Game.java
 * 1. 개요 : 실제 게임
 * 2. 작성일 : 2017. 6. 16.
 * </pre>
 *
 * @author : jjjj
 * @version : 1.0
 */
public class Game implements GLEventListener, KeyListener {
	public static int[] units = new int[2]; // 각 플레이어의 전체 유닛 수
	private GLU glu = new GLU();
	private TextRenderer textRenderer = new TextRenderer(new Font("Font", Font.CENTER_BASELINE, 100));
	private boolean showInfo = true; // 선택중인 칸의 유닛의 정보 표시 여부
	private int turn = 0; // 0번 플레이어부터 시작
	private int xSize = 17; // 맵 크기
	private int ySize = 14;
	private int xNow = xSize / 2; // 지금 선택중인(?) 칸 좌표
	private int yNow = ySize / 2;
	private double hitParticleSize = 0.07; // 파티클 크기
	private double invincibleParticleSize = 0.08;
	private double frozenParticleSize = 0.06;
	private double hitParticleSpeed = 0.04; // 파티클 속도
	private double invincibleParticleSpeed = 0.01;
	private double frozenParticleSpeed = 0.02;
	private double tileSize = 1; // 타일 크기
	private double offset = 0.1; // 타일 간 간격
	private double screenShake = 0; // 화면 진동
	private double screenShakeMagnitude = 0.6; // 공격 시 화면 진동의 세기
	private double screenShakeThreshold = 0.05; // 화면 진동 제거 임계치
	private double cameraZHigh = 8; // 카메라 z좌표 최대
	private double cameraZLow = 5; // 카메라 z좌표 최소
	private double cameraX = xNow; // 카메라 좌표
	private double cameraY = yNow;
	private double cameraZ = cameraZLow;
	private int width; // 화면 크기
	private int height;
	private List<Move> moveList = new ArrayList<>(); // 이동 가능한 칸들
	private List<Move> teleportList = new ArrayList<>(); // 텔레포트 가능한 칸들
	private List<Attack> attackList = new ArrayList<>(); // 공격 가능한 칸들
	private List<Particle> hitParticleList = new ArrayList<>(); // 파티클들
	private List<Particle> invincibleParticleList = new ArrayList<>();
	private List<Particle> frozenParticleList = new ArrayList<>();
	private List<Particle> hitParticleRemoveList = new ArrayList<>(); // 지울 파티클들
	private List<Particle> invincibleParticleRemoveList = new ArrayList<>();
	private List<Particle> frozenParticleRemoveList = new ArrayList<>();
	private Unit[][] map = new Unit[xSize][ySize]; // 유닛들을 저장할 배열(맵)

	public Game() {
		// 플레이어1
		map[4][0] = new Warrior(0, 4, 0);
		map[4][2] = new Warrior(0, 4, 2);
		map[4][4] = new Warrior(0, 4, 4);
		map[4][6] = new Warrior(0, 4, 6);
		map[4][7] = new Warrior(0, 4, 7);
		map[4][9] = new Warrior(0, 4, 9);
		map[4][11] = new Warrior(0, 4, 11);
		map[4][13] = new Warrior(0, 4, 13);
		map[5][1] = new Knight(0, 5, 1);
		map[6][3] = new Knight(0, 6, 3);
		map[5][5] = new Knight(0, 5, 5);
		map[5][8] = new Knight(0, 5, 8);
		map[6][10] = new Knight(0, 6, 10);
		map[5][12] = new Knight(0, 5, 12);
		map[2][1] = new Wizard(0, 2, 1);
		map[2][3] = new Wizard(0, 2, 3);
		map[2][5] = new Wizard(0, 2, 5);
		map[2][8] = new Wizard(0, 2, 8);
		map[2][10] = new Wizard(0, 2, 10);
		map[2][12] = new Wizard(0, 2, 12);
		// 플레이어2
		map[12][0] = new Warrior(1, 12, 0);
		map[12][2] = new Warrior(1, 12, 2);
		map[12][4] = new Warrior(1, 12, 4);
		map[12][6] = new Warrior(1, 12, 6);
		map[12][7] = new Warrior(1, 12, 7);
		map[12][9] = new Warrior(1, 12, 9);
		map[12][11] = new Warrior(1, 12, 11);
		map[12][13] = new Warrior(1, 12, 13);
		map[11][1] = new Knight(1, 11, 1);
		map[10][3] = new Knight(1, 10, 3);
		map[11][5] = new Knight(1, 11, 5);
		map[11][8] = new Knight(1, 11, 8);
		map[10][10] = new Knight(1, 10, 10);
		map[11][12] = new Knight(1, 11, 12);
		map[14][1] = new Wizard(1, 14, 1);
		map[14][3] = new Wizard(1, 14, 3);
		map[14][5] = new Wizard(1, 14, 5);
		map[14][8] = new Wizard(1, 14, 8);
		map[14][10] = new Wizard(1, 14, 10);
		map[14][12] = new Wizard(1, 14, 12);
	}

	public void clearLists() { // 초기화
		moveList.clear();
		attackList.clear();
		teleportList.clear();
	}

	public void hitEffect(int x, int y) { // 타격 파티클 생성
		for (int i = 0; i < 15; i++) {
			hitParticleList.add(new Particle(x * (tileSize + offset) + tileSize * 0.5,
					y * (tileSize + offset) + tileSize * 0.5, Math.random() * 360,
					hitParticleSpeed + Math.random() * 0.01, hitParticleSize, 60 + (int) (Math.random() * 30)));
		}
	}

	public void invincibleEffect(int x, int y) { // 무적 파티클 생성
		for (int i = 0; i < 1; i++) {
			invincibleParticleList
					.add(new Particle(x * (tileSize + offset) + tileSize * 0.1 + Math.random() * tileSize * 0.8,
							y * (tileSize + offset) + Math.random() * tileSize * 0.7, 90, invincibleParticleSpeed,
							invincibleParticleSize, 40 + (int) (Math.random() * 30)));
		}
	}

	public void frozenEffect(int x, int y) { // 빙결 파티클 생성
		for (int i = 0; i < 2; i++) {
			frozenParticleList.add(new Particle(x * (tileSize + offset) + tileSize * 0.5,
					y * (tileSize + offset) + tileSize * 0.5, Math.random() * 360,
					frozenParticleSpeed + Math.random() * 0.01, frozenParticleSize, 60 + (int) (Math.random() * 30)));
		}
	}

	public void damage(int x, int y, int damage) { // 데미지
		if (x >= 0 && x < xSize && y >= 0 && y < ySize) {
			hitEffect(x, y);
			if (map[x][y] != null && map[x][y].invincible == 0) {
				map[x][y].hp -= damage;
				if (map[x][y].hp <= 0) {
					units[map[x][y].team]--;
					map[x][y] = null;
				}
			}
		}
	}

	public void freeze(int x, int y) { // 빙결
		if (x >= 0 && x < xSize && y >= 0 && y < ySize) {
			if (map[x][y] != null && map[x][y].invincible == 0) {
				map[x][y].frozen = 2;
				map[x][y].move = false;
				map[x][y].attack = false;
			}
		}
	}

	public void changeView(GL2 gl) { // 카메라 위치로 시점 이동, 화면 진동
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60, (float) width / height, 1, 100);
		double dx = Math.random() * screenShake;
		double dy = Math.random() * screenShake;
		double dz = Math.random() * screenShake;
		glu.gluLookAt(cameraX + dx, cameraY + dy, cameraZ + dz, cameraX + dx, cameraY + dy, 0, 0, 1, 0);
	}

	public void display(GLAutoDrawable glAutoDrawable) {
		GL2 gl = glAutoDrawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		changeView(gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		// 이동 가능한 칸들 표시
		for (Move m : moveList) {
			gl.glColor3d(0.4, 0.9, 0.4);
			gl.glLoadIdentity();
			gl.glTranslated(m.x * (tileSize + offset), m.y * (tileSize + offset), 0);
			// 타일
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(-offset * 0.5, -offset * 0.5, 0);
			gl.glVertex3d(tileSize + offset * 0.5, -offset * 0.5, 0);
			gl.glVertex3d(tileSize + offset * 0.5, tileSize + offset * 0.5, 0);
			gl.glVertex3d(-offset * 0.5, tileSize + offset * 0.5, 0);
			gl.glEnd();
		}
		// 텔레포트 가능한 칸들 표시
		for (Move m : teleportList) {
			gl.glColor3d(0.9, 0.4, 0.9);
			gl.glLoadIdentity();
			gl.glTranslated(m.x * (tileSize + offset), m.y * (tileSize + offset), 0);
			// 타일
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(-offset * 0.5, -offset * 0.5, 0);
			gl.glVertex3d(tileSize + offset * 0.5, -offset * 0.5, 0);
			gl.glVertex3d(tileSize + offset * 0.5, tileSize + offset * 0.5, 0);
			gl.glVertex3d(-offset * 0.5, tileSize + offset * 0.5, 0);
			gl.glEnd();
		}
		// 공격 가능한 칸들 표시
		for (Attack a : attackList) {
			gl.glColor3d(0.9, 0.2, 0.2);
			gl.glLoadIdentity();
			gl.glTranslated(a.x * (tileSize + offset), a.y * (tileSize + offset), 0);
			// 타일
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(-offset * 0.5, -offset * 0.5, 0);
			gl.glVertex3d(tileSize + offset * 0.5, -offset * 0.5, 0);
			gl.glVertex3d(tileSize + offset * 0.5, tileSize + offset * 0.5, 0);
			gl.glVertex3d(-offset * 0.5, tileSize + offset * 0.5, 0);
			gl.glEnd();
		}

		// 타일 그리기
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				if (map[x][y] != null) {
					if (map[x][y].team == turn) {
						gl.glColor3d(0.8, 0.8, 1);
					} else {
						gl.glColor3d(1, 0.8, 0.8);
					}
				} else {
					gl.glColor3d(1, 1, 1);
				}
				gl.glLoadIdentity();
				gl.glTranslated(x * (tileSize + offset), y * (tileSize + offset), 0);
				// 타일
				gl.glBegin(GL2.GL_QUADS);
				gl.glVertex3d(0, 0, 0);
				gl.glVertex3d(tileSize, 0, 0);
				gl.glVertex3d(tileSize, tileSize, 0);
				gl.glVertex3d(0, tileSize, 0);
				gl.glEnd();
				// 선택된 칸이면 표시(꺽쇄 모양?)
				if (x == xNow && y == yNow) {
					gl.glColor3d(0, 0, 0);
					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex3d(tileSize * 0.05, tileSize * 0.05, 0);
					gl.glVertex3d(tileSize * 0.2, tileSize * 0.05, 0);
					gl.glVertex3d(tileSize * 0.2, tileSize * 0.2, 0);
					gl.glVertex3d(tileSize * 0.05, tileSize * 0.2, 0);
					gl.glEnd();
					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex3d(tileSize * 0.95, tileSize * 0.05, 0);
					gl.glVertex3d(tileSize * 0.8, tileSize * 0.05, 0);
					gl.glVertex3d(tileSize * 0.8, tileSize * 0.2, 0);
					gl.glVertex3d(tileSize * 0.95, tileSize * 0.2, 0);
					gl.glEnd();
					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex3d(tileSize * 0.95, tileSize * 0.95, 0);
					gl.glVertex3d(tileSize * 0.8, tileSize * 0.95, 0);
					gl.glVertex3d(tileSize * 0.8, tileSize * 0.8, 0);
					gl.glVertex3d(tileSize * 0.95, tileSize * 0.8, 0);
					gl.glEnd();
					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex3d(tileSize * 0.05, tileSize * 0.95, 0);
					gl.glVertex3d(tileSize * 0.2, tileSize * 0.95, 0);
					gl.glVertex3d(tileSize * 0.2, tileSize * 0.8, 0);
					gl.glVertex3d(tileSize * 0.05, tileSize * 0.8, 0);
					gl.glEnd();
					if (map[x][y] != null) {
						if (map[x][y].team == turn) {
							gl.glColor3d(0.8, 0.8, 1);
						} else {
							gl.glColor3d(1, 0.8, 0.8);
						}
					} else {
						gl.glColor3d(1, 1, 1);
					}
					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex3d(tileSize * 0.1, tileSize * 0.1, 0);
					gl.glVertex3d(tileSize * 0.9, tileSize * 0.1, 0);
					gl.glVertex3d(tileSize * 0.9, tileSize * 0.9, 0);
					gl.glVertex3d(tileSize * 0.1, tileSize * 0.9, 0);
					gl.glEnd();
				}
			}
		}
		// 유닛 그리기
		for (int x = 0; x < xSize; x++) {
			for (int y = 0; y < ySize; y++) {
				if (map[x][y] != null) {
					map[x][y].step();
					if (map[x][y].invincible > 0) {
						invincibleEffect(x, y);
					}
					if (map[x][y].frozen > 0) {
						frozenEffect(x, y);
					}
					gl.glLoadIdentity();
					gl.glTranslated(map[x][y].x * (tileSize + offset), map[x][y].y * (tileSize + offset), 0);
					switch (map[x][y].type) {
					case 0: // warrior
						gl.glColor3d(0, 0, 0);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.1, 0);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.1, 0);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.875, 0);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.875, 0);
						gl.glEnd();
						gl.glColor3d(1, 1, 1);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.475, tileSize * 0.15, 0);
						gl.glVertex3d(tileSize * 0.525, tileSize * 0.15, 0);
						gl.glVertex3d(tileSize * 0.525, tileSize * 0.825, 0);
						gl.glVertex3d(tileSize * 0.475, tileSize * 0.825, 0);
						gl.glEnd();
						gl.glColor3d(0, 0, 0);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.25, tileSize * 0.55, 0);
						gl.glVertex3d(tileSize * 0.75, tileSize * 0.55, 0);
						gl.glVertex3d(tileSize * 0.75, tileSize * 0.8, 0);
						gl.glVertex3d(tileSize * 0.25, tileSize * 0.8, 0);
						gl.glEnd();
						gl.glColor3d(1, 1, 1);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.3, tileSize * 0.6, 0);
						gl.glVertex3d(tileSize * 0.7, tileSize * 0.6, 0);
						gl.glVertex3d(tileSize * 0.7, tileSize * 0.75, 0);
						gl.glVertex3d(tileSize * 0.3, tileSize * 0.75, 0);
						gl.glEnd();
						break;
					case 1: // knight
						gl.glColor3d(0, 0, 0);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.25, tileSize * 0.2, 0);
						gl.glVertex3d(tileSize * 0.75, tileSize * 0.2, 0);
						gl.glVertex3d(tileSize * 0.75, tileSize * 0.8, 0);
						gl.glVertex3d(tileSize * 0.25, tileSize * 0.8, 0);
						gl.glEnd();
						gl.glColor3d(1, 1, 1);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.3, tileSize * 0.475, 0);
						gl.glVertex3d(tileSize * 0.7, tileSize * 0.475, 0);
						gl.glVertex3d(tileSize * 0.7, tileSize * 0.525, 0);
						gl.glVertex3d(tileSize * 0.3, tileSize * 0.525, 0);
						gl.glEnd();
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.475, tileSize * 0.25, 0);
						gl.glVertex3d(tileSize * 0.525, tileSize * 0.25, 0);
						gl.glVertex3d(tileSize * 0.525, tileSize * 0.75, 0);
						gl.glVertex3d(tileSize * 0.475, tileSize * 0.75, 0);
						gl.glEnd();
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.3, tileSize * 0.25, 0);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.25, 0);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.425, 0);
						gl.glVertex3d(tileSize * 0.3, tileSize * 0.425, 0);
						gl.glEnd();
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.3, tileSize * 0.575, 0);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.575, 0);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.75, 0);
						gl.glVertex3d(tileSize * 0.3, tileSize * 0.75, 0);
						gl.glEnd();
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.25, 0);
						gl.glVertex3d(tileSize * 0.7, tileSize * 0.25, 0);
						gl.glVertex3d(tileSize * 0.7, tileSize * 0.425, 0);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.425, 0);
						gl.glEnd();
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.575, 0);
						gl.glVertex3d(tileSize * 0.7, tileSize * 0.575, 0);
						gl.glVertex3d(tileSize * 0.7, tileSize * 0.75, 0);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.75, 0);
						gl.glEnd();
						break;
					case 2: // wizard
						gl.glColor3d(0, 0, 0);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.25, 0);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.25, 0);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.7, 0);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.7, 0);
						gl.glEnd();
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.375, tileSize * 0.65, 0);
						gl.glVertex3d(tileSize * 0.625, tileSize * 0.65, 0);
						gl.glVertex3d(tileSize * 0.625, tileSize * 0.9, 0);
						gl.glVertex3d(tileSize * 0.375, tileSize * 0.9, 0);
						gl.glEnd();
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.4, tileSize * 0.1, 0);
						gl.glVertex3d(tileSize * 0.6, tileSize * 0.1, 0);
						gl.glVertex3d(tileSize * 0.6, tileSize * 0.3, 0);
						gl.glVertex3d(tileSize * 0.4, tileSize * 0.3, 0);
						gl.glEnd();
						gl.glColor3d(1, 1, 1);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.475, tileSize * 0.3, 0);
						gl.glVertex3d(tileSize * 0.525, tileSize * 0.3, 0);
						gl.glVertex3d(tileSize * 0.525, tileSize * 0.65, 0);
						gl.glVertex3d(tileSize * 0.475, tileSize * 0.65, 0);
						gl.glEnd();
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.7, 0);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.7, 0);
						gl.glVertex3d(tileSize * 0.575, tileSize * 0.85, 0);
						gl.glVertex3d(tileSize * 0.425, tileSize * 0.85, 0);
						gl.glEnd();
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3d(tileSize * 0.45, tileSize * 0.15, 0);
						gl.glVertex3d(tileSize * 0.55, tileSize * 0.15, 0);
						gl.glVertex3d(tileSize * 0.55, tileSize * 0.25, 0);
						gl.glVertex3d(tileSize * 0.45, tileSize * 0.25, 0);
						gl.glEnd();
						break;
					}
				}
			}
		}
		// 텍스트 그리기
		textRenderer.setColor(1.0f, 0.65f, 0.77f, 1);
		textRenderer.beginRendering(glAutoDrawable.getSurfaceWidth(), glAutoDrawable.getSurfaceHeight());
		if (units[0] == 0 && units[1] > 0) {
			textRenderer.draw("플레이어2 승리", glAutoDrawable.getSurfaceWidth() * 6 / 23,
					glAutoDrawable.getSurfaceHeight() * 6 / 13);
		} else if (units[1] == 0 && units[0] > 0) {
			textRenderer.draw("플레이어1 승리", glAutoDrawable.getSurfaceWidth() * 6 / 23,
					glAutoDrawable.getSurfaceHeight() * 6 / 13);
		} else {
			textRenderer.draw(units[0] + " : " + units[1], glAutoDrawable.getSurfaceWidth() * 3 / 7,
					glAutoDrawable.getSurfaceHeight() / 20);
			textRenderer.draw("플레이어" + (turn + 1) + " 의 턴", glAutoDrawable.getSurfaceWidth() / 4,
					glAutoDrawable.getSurfaceHeight() * 17 / 20);
		}
		textRenderer.endRendering();
		// 파티클 처리
		for (Particle p : hitParticleList) {
			gl.glColor3d(1, 0.2, 0.2);
			gl.glLoadIdentity();
			gl.glTranslated(p.x, p.y, 0);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(p.size, 0, 0);
			gl.glVertex3d(p.size, p.size, 0);
			gl.glVertex3d(0, p.size, 0);
			gl.glEnd();
			double a = p.angle / 180 * 3.141592;
			p.x += p.speed * Math.cos(a);
			p.y += p.speed * Math.sin(a);
			p.speed *= 0.95;
			p.size *= 0.99;
			p.life++;
			if (p.life >= p.maxLife) {
				hitParticleRemoveList.add(p);
			}
		}
		for (Particle p : hitParticleRemoveList) {
			hitParticleList.remove(p);
		}
		hitParticleRemoveList.clear();
		for (Particle p : invincibleParticleList) {
			gl.glColor3d(1, 1, 0.6);
			gl.glLoadIdentity();
			gl.glTranslated(p.x, p.y, 0);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(p.size, 0, 0);
			gl.glVertex3d(p.size, p.size, 0);
			gl.glVertex3d(0, p.size, 0);
			gl.glEnd();
			double a = p.angle / 180 * 3.141592;
			p.x += p.speed * Math.cos(a);
			p.y += p.speed * Math.sin(a);
			p.speed *= 0.95;
			p.size *= 0.99;
			p.life++;
			if (p.life >= p.maxLife) {
				invincibleParticleRemoveList.add(p);
			}
		}
		for (Particle p : invincibleParticleRemoveList) {
			invincibleParticleList.remove(p);
		}
		invincibleParticleRemoveList.clear();
		for (Particle p : frozenParticleList) {
			gl.glColor3d(0.78, 1, 1);
			gl.glLoadIdentity();
			gl.glTranslated(p.x, p.y, 0);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(p.size, 0, 0);
			gl.glVertex3d(p.size, p.size, 0);
			gl.glVertex3d(0, p.size, 0);
			gl.glEnd();
			double a = p.angle / 180 * 3.141592;
			p.x += p.speed * Math.cos(a);
			p.y += p.speed * Math.sin(a);
			p.speed *= 0.95;
			p.size *= 0.99;
			p.life++;
			if (p.life >= p.maxLife) {
				frozenParticleRemoveList.add(p);
			}
		}
		for (Particle p : frozenParticleRemoveList) {
			frozenParticleList.remove(p);
		}
		frozenParticleRemoveList.clear();
		// 정보 그리기
		if (showInfo && map[xNow][yNow] != null) {
			gl.glLoadIdentity();
			gl.glTranslated(map[xNow][yNow].x * (tileSize + offset) + tileSize * 0.8,
					map[xNow][yNow].y * (tileSize + offset) + tileSize * 0.8, 0);
			gl.glColor3d(0, 0, 0);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(0, 0, 0);
			gl.glVertex3d(tileSize * 1.6, 0, 0);
			gl.glVertex3d(tileSize * 1.6, tileSize * 0.7, 0);
			gl.glVertex3d(0, tileSize * 0.7, 0);
			gl.glEnd();
			gl.glColor3d(1, 1, 1);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.05, tileSize * 0.05, 0);
			gl.glVertex3d(tileSize * 1.55, tileSize * 0.05, 0);
			gl.glVertex3d(tileSize * 1.55, tileSize * 0.65, 0);
			gl.glVertex3d(tileSize * 0.05, tileSize * 0.65, 0);
			gl.glEnd();
			// 이동
			gl.glColor3d(0, 0, 0);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.15, tileSize * 0.4, 0);
			gl.glVertex3d(tileSize * 0.3, tileSize * 0.4, 0);
			gl.glVertex3d(tileSize * 0.3, tileSize * 0.55, 0);
			gl.glVertex3d(tileSize * 0.15, tileSize * 0.55, 0);
			gl.glEnd();
			if (map[xNow][yNow].move) {
				gl.glColor3d(0.37, 0.79, 0.37);
			} else {
				gl.glColor3d(1, 1, 1);
			}
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.17, tileSize * 0.42, 0);
			gl.glVertex3d(tileSize * 0.28, tileSize * 0.42, 0);
			gl.glVertex3d(tileSize * 0.28, tileSize * 0.53, 0);
			gl.glVertex3d(tileSize * 0.17, tileSize * 0.53, 0);
			gl.glEnd();
			// 공격
			gl.glColor3d(0, 0, 0);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.15, tileSize * 0.15, 0);
			gl.glVertex3d(tileSize * 0.3, tileSize * 0.15, 0);
			gl.glVertex3d(tileSize * 0.3, tileSize * 0.3, 0);
			gl.glVertex3d(tileSize * 0.15, tileSize * 0.3, 0);
			gl.glEnd();
			if (map[xNow][yNow].attack) {
				gl.glColor3d(0.37, 0.79, 0.37);
			} else {
				gl.glColor3d(1, 1, 1);
			}
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.17, tileSize * 0.17, 0);
			gl.glVertex3d(tileSize * 0.28, tileSize * 0.17, 0);
			gl.glVertex3d(tileSize * 0.28, tileSize * 0.28, 0);
			gl.glVertex3d(tileSize * 0.17, tileSize * 0.28, 0);
			gl.glEnd();
			// hp
			gl.glColor3d(0, 0, 0);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.45, tileSize * 0.4, 0);
			gl.glVertex3d(tileSize * 1.45, tileSize * 0.4, 0);
			gl.glVertex3d(tileSize * 1.45, tileSize * 0.55, 0);
			gl.glVertex3d(tileSize * 0.45, tileSize * 0.55, 0);
			gl.glEnd();
			gl.glColor3d(1, 1, 1);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.47, tileSize * 0.42, 0);
			gl.glVertex3d(tileSize * 1.43, tileSize * 0.42, 0);
			gl.glVertex3d(tileSize * 1.43, tileSize * 0.53, 0);
			gl.glVertex3d(tileSize * 0.47, tileSize * 0.53, 0);
			gl.glEnd();
			gl.glColor3d(1, 0, 0);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.47, tileSize * 0.42, 0);
			gl.glVertex3d(tileSize * (0.47 + 0.96 * (double) map[xNow][yNow].hp / map[xNow][yNow].maxHp),
					tileSize * 0.42, 0);
			gl.glVertex3d(tileSize * (0.47 + 0.96 * (double) map[xNow][yNow].hp / map[xNow][yNow].maxHp),
					tileSize * 0.53, 0);
			gl.glVertex3d(tileSize * 0.47, tileSize * 0.53, 0);
			gl.glEnd();
			// mp
			gl.glColor3d(0, 0, 0);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.45, tileSize * 0.15, 0);
			gl.glVertex3d(tileSize * 1.45, tileSize * 0.15, 0);
			gl.glVertex3d(tileSize * 1.45, tileSize * 0.3, 0);
			gl.glVertex3d(tileSize * 0.45, tileSize * 0.3, 0);
			gl.glEnd();
			gl.glColor3d(1, 1, 1);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.47, tileSize * 0.17, 0);
			gl.glVertex3d(tileSize * 1.43, tileSize * 0.17, 0);
			gl.glVertex3d(tileSize * 1.43, tileSize * 0.28, 0);
			gl.glVertex3d(tileSize * 0.47, tileSize * 0.28, 0);
			gl.glEnd();
			gl.glColor3d(0, 0, 1);
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3d(tileSize * 0.47, tileSize * 0.17, 0);
			gl.glVertex3d(tileSize * (0.47 + 0.96 * (double) map[xNow][yNow].mp / map[xNow][yNow].maxMp),
					tileSize * 0.17, 0);
			gl.glVertex3d(tileSize * (0.47 + 0.96 * (double) map[xNow][yNow].mp / map[xNow][yNow].maxMp),
					tileSize * 0.28, 0);
			gl.glVertex3d(tileSize * 0.47, tileSize * 0.28, 0);
			gl.glEnd();
		}
		// 카메라 이동
		double dx = xNow * (tileSize + offset) + tileSize * 0.5 - cameraX;
		double dy = yNow * (tileSize + offset) + tileSize * 0.5 - cameraY;
		double dz;
		if (moveList.size() + teleportList.size() + attackList.size() > 0) {
			dz = cameraZHigh - cameraZ;
		} else {
			dz = cameraZLow - cameraZ;
		}
		if (Math.sqrt(dx * dx + dy * dy + dz * dz) < 0.01) {
			cameraX += dx;
			cameraY += dy;
			cameraY += dz;
		} else {
			cameraX += dx * 0.05 + Math.signum(dx) * 0.005;
			cameraY += dy * 0.05 + Math.signum(dy) * 0.005;
			cameraZ += dz * 0.1 + Math.signum(dz) * 0.005;
		}
		// 화면 진동 감소
		if (screenShake > 0) {
			screenShake *= 0.9;
			if (screenShake < screenShakeThreshold) {
				screenShake = 0;
			}
		}
	}

	public void dispose(GLAutoDrawable glAutoDrawable) {

	}

	public void init(GLAutoDrawable glAutoDrawable) {
		GL2 gl = glAutoDrawable.getGL().getGL2();
		gl.glClearColor(0.6f, 0.9f, 0.9f, 1);
	}

	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		GL2 gl = glAutoDrawable.getGL().getGL2();
		if (height <= 0) {
			height = 1;
		}
		this.width = width;
		this.height = height;
		gl.glViewport(0, 0, width, height);
	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
		switch (e.getKeyChar()) {
		case 'd':
			if (xNow < xSize - 1) {
				xNow++;
			}
			break;
		case 'a':
			if (xNow > 0) {
				xNow--;
			}
			break;
		case 'w':
			if (yNow < ySize - 1) {
				yNow++;
			}
			break;
		case 's':
			if (yNow > 0) {
				yNow--;
			}
			break;
		case 'f': // 이동
			if (map[xNow][yNow] != null && map[xNow][yNow].move) {
				// 선택한 유닛을 다시 선택하면 취소
				if (moveList.size() > 0 && moveList.get(0).unitX == xNow && moveList.get(0).unitY == yNow) {
					clearLists();
					break;
				}
				clearLists();
				// 이동 가능한 칸은 리스트에 넣음
				if (xNow < xSize - 1 && map[xNow + 1][yNow] == null) {
					moveList.add(new Move(xNow + 1, yNow, xNow, yNow));
				}
				if (xNow > 0 && map[xNow - 1][yNow] == null) {
					moveList.add(new Move(xNow - 1, yNow, xNow, yNow));
				}
				if (yNow < ySize - 1 && map[xNow][yNow + 1] == null) {
					moveList.add(new Move(xNow, yNow + 1, xNow, yNow));
				}
				if (yNow > 0 && map[xNow][yNow - 1] == null) {
					moveList.add(new Move(xNow, yNow - 1, xNow, yNow));
				}
				if (xNow < xSize - 1 && yNow < ySize - 1 && map[xNow + 1][yNow + 1] == null) {
					moveList.add(new Move(xNow + 1, yNow + 1, xNow, yNow));
				}
				if (xNow > 0 && yNow < ySize - 1 && map[xNow - 1][yNow + 1] == null) {
					moveList.add(new Move(xNow - 1, yNow + 1, xNow, yNow));
				}
				if (xNow > 0 && yNow > 0 && map[xNow - 1][yNow - 1] == null) {
					moveList.add(new Move(xNow - 1, yNow - 1, xNow, yNow));
				}
				if (xNow < xSize - 1 && yNow > 0 && map[xNow + 1][yNow - 1] == null) {
					moveList.add(new Move(xNow + 1, yNow - 1, xNow, yNow));
				}
				switch (map[xNow][yNow].type) {
				case 0:
				case 1:
					if (xNow < xSize - 2 && map[xNow + 1][yNow] == null && map[xNow + 2][yNow] == null) {
						moveList.add(new Move(xNow + 2, yNow, xNow, yNow));
					}
					if (xNow > 1 && map[xNow - 1][yNow] == null && map[xNow - 2][yNow] == null) {
						moveList.add(new Move(xNow - 2, yNow, xNow, yNow));
					}
					if (yNow < ySize - 2 && map[xNow][yNow + 1] == null && map[xNow][yNow + 2] == null) {
						moveList.add(new Move(xNow, yNow + 2, xNow, yNow));
					}
					if (yNow > 1 && map[xNow][yNow - 1] == null && map[xNow][yNow - 2] == null) {
						moveList.add(new Move(xNow, yNow - 2, xNow, yNow));
					}
					break;
				}
			} else {
				// 그냥 빈 공간이나 움직이지 못하는 유닛을 선택하면 취소
				clearLists();
			}
			break;
		case 'q': // 공격
			if (map[xNow][yNow] != null && map[xNow][yNow].attack) {
				// 선택한 유닛을 다시 선택하면 취소
				if (attackList.size() > 0 && attackList.get(0).unitX == xNow && attackList.get(0).unitY == yNow) {
					clearLists();
					break;
				}
				clearLists();
				switch (map[xNow][yNow].type) {
				case 0:
					if (xNow < xSize - 1) {
						attackList.add(new Attack(0, 0, xNow + 1, yNow, xNow, yNow));
					}
					if (xNow > 0) {
						attackList.add(new Attack(0, 2, xNow - 1, yNow, xNow, yNow));
					}
					if (yNow < ySize - 1) {
						attackList.add(new Attack(0, 1, xNow, yNow + 1, xNow, yNow));
					}
					if (yNow > 0) {
						attackList.add(new Attack(0, 3, xNow, yNow - 1, xNow, yNow));
					}
					break;
				case 1:
					if (xNow < xSize - 1) {
						attackList.add(new Attack(1, 0, xNow + 1, yNow, xNow, yNow));
					}
					if (xNow > 0) {
						attackList.add(new Attack(1, 0, xNow - 1, yNow, xNow, yNow));
					}
					if (yNow < ySize - 1) {
						attackList.add(new Attack(1, 0, xNow, yNow + 1, xNow, yNow));
					}
					if (yNow > 0) {
						attackList.add(new Attack(1, 0, xNow, yNow - 1, xNow, yNow));
					}
					if (xNow < xSize - 1 && yNow < ySize - 1) {
						attackList.add(new Attack(1, 0, xNow + 1, yNow + 1, xNow, yNow));
					}
					if (xNow > 0 && yNow < ySize - 1) {
						attackList.add(new Attack(1, 0, xNow - 1, yNow + 1, xNow, yNow));
					}
					if (xNow > 0 && yNow > 0) {
						attackList.add(new Attack(1, 0, xNow - 1, yNow - 1, xNow, yNow));
					}
					if (xNow < xSize - 1 && yNow > 0) {
						attackList.add(new Attack(1, 0, xNow + 1, yNow - 1, xNow, yNow));
					}
					break;
				case 2:
					switch (map[xNow][yNow].attackType) {
					case 1:
						if (xNow < xSize - 1) {
							attackList.add(new Attack(1, 0, xNow + 1, yNow, xNow, yNow));
						}
						if (xNow > 0) {
							attackList.add(new Attack(1, 0, xNow - 1, yNow, xNow, yNow));
						}
						if (yNow < ySize - 1) {
							attackList.add(new Attack(1, 0, xNow, yNow + 1, xNow, yNow));
						}
						if (yNow > 0) {
							attackList.add(new Attack(1, 0, xNow, yNow - 1, xNow, yNow));
						}
						break;
					case 2:
						if (xNow < xSize - 3) {
							attackList.add(new Attack(2, 0, xNow + 3, yNow, xNow, yNow));
						}
						if (xNow < xSize - 2 && yNow < ySize - 1) {
							attackList.add(new Attack(2, 0, xNow + 2, yNow + 1, xNow, yNow));
						}
						if (xNow < xSize - 1 && yNow < ySize - 2) {
							attackList.add(new Attack(2, 0, xNow + 1, yNow + 2, xNow, yNow));
						}
						if (yNow < ySize - 3) {
							attackList.add(new Attack(2, 0, xNow, yNow + 3, xNow, yNow));
						}
						if (xNow > 0 && yNow < ySize - 2) {
							attackList.add(new Attack(2, 0, xNow - 1, yNow + 2, xNow, yNow));
						}
						if (xNow > 1 && yNow < ySize - 1) {
							attackList.add(new Attack(2, 0, xNow - 2, yNow + 1, xNow, yNow));
						}
						if (xNow > 2) {
							attackList.add(new Attack(2, 0, xNow - 3, yNow, xNow, yNow));
						}
						if (xNow > 1 && yNow > 0) {
							attackList.add(new Attack(2, 0, xNow - 2, yNow - 1, xNow, yNow));
						}
						if (xNow > 0 && yNow > 1) {
							attackList.add(new Attack(2, 0, xNow - 1, yNow - 2, xNow, yNow));
						}
						if (yNow > 2) {
							attackList.add(new Attack(2, 0, xNow, yNow - 3, xNow, yNow));
						}
						if (xNow < xSize - 1 && yNow > 1) {
							attackList.add(new Attack(2, 0, xNow + 1, yNow - 2, xNow, yNow));
						}
						if (xNow < xSize - 2 && yNow > 0) {
							attackList.add(new Attack(2, 0, xNow + 2, yNow - 1, xNow, yNow));
						}
						break;
					case 3:
						if (xNow < xSize - 1) {
							attackList.add(new Attack(3, 0, xNow + 1, yNow, xNow, yNow));
						}
						if (xNow > 0) {
							attackList.add(new Attack(3, 0, xNow - 1, yNow, xNow, yNow));
						}
						if (yNow < ySize - 1) {
							attackList.add(new Attack(3, 0, xNow, yNow + 1, xNow, yNow));
						}
						if (yNow > 0) {
							attackList.add(new Attack(3, 0, xNow, yNow - 1, xNow, yNow));
						}
						break;
					}
					break;
				}
			} else {
				// 그냥 빈 공간이나 움직이지 못하는 유닛을 선택하면 취소
				clearLists();
			}
			break;
		case 'e': // 스킬
			if (map[xNow][yNow] != null && map[xNow][yNow].team == turn && map[xNow][yNow].frozen == 0
					&& map[xNow][yNow].mp == map[xNow][yNow].maxMp) {
				switch (map[xNow][yNow].type) {
				case 0:
					map[xNow][yNow].mp = 0;
					screenShake = screenShakeMagnitude;
					damage(xNow + 1, yNow, 1);
					damage(xNow - 1, yNow, 1);
					damage(xNow, yNow + 1, 1);
					damage(xNow, yNow - 1, 1);
					damage(xNow + 1, yNow + 1, 1);
					damage(xNow - 1, yNow + 1, 1);
					damage(xNow - 1, yNow - 1, 1);
					damage(xNow + 1, yNow - 1, 1);
					break;
				case 1:
					map[xNow][yNow].mp = 0;
					map[xNow][yNow].invincible = 2;
					break;
				case 2:
					// 선택한 유닛을 다시 선택하면 취소
					if (teleportList.size() > 0 && teleportList.get(0).unitX == xNow
							&& teleportList.get(0).unitY == yNow) {
						clearLists();
						break;
					}
					clearLists();
					if (xNow > 5 && map[xNow - 5][yNow] == null) {
						teleportList.add(new Move(xNow - 5, yNow, xNow, yNow));
					}
					if (xNow < xSize - 5 && map[xNow + 5][yNow] == null) {
						teleportList.add(new Move(xNow + 5, yNow, xNow, yNow));
					}
					if (yNow > 5 && map[xNow][yNow - 5] == null) {
						teleportList.add(new Move(xNow, yNow - 5, xNow, yNow));
					}
					if (yNow < ySize - 5 && map[xNow][yNow + 5] == null) {
						teleportList.add(new Move(xNow, yNow + 5, xNow, yNow));
					}
					break;
				}
			} else {
				clearLists();
			}
			break;
		case 'i': // 정보 표시 토글
			showInfo = !showInfo;
			break;
		case KeyEvent.VK_ENTER: // 선택
			for (Move m : moveList) {
				if (m.x == xNow && m.y == yNow) {
					map[m.x][m.y] = map[m.unitX][m.unitY];
					map[m.unitX][m.unitY] = null;
					map[m.x][m.y].desX = m.x;
					map[m.x][m.y].desY = m.y;
					map[m.x][m.y].move = false;
					// 공격 타입 변경
					if (map[m.x][m.y].type == 2 && map[m.x][m.y].attackType != 3
							&& (m.x - m.unitX) * (m.y - m.unitY) != 0) {
						map[m.x][m.y].attackType = 1;
					}
					break;
				}
			}
			for (Move m : teleportList) {
				if (m.x == xNow && m.y == yNow) {
					map[m.x][m.y] = map[m.unitX][m.unitY];
					map[m.unitX][m.unitY] = null;
					map[m.x][m.y].desX = m.x;
					map[m.x][m.y].desY = m.y;
					map[m.x][m.y].attackType = 3;
					map[m.x][m.y].mp = 0;
					freeze(m.x + 1, m.y);
					freeze(m.x - 1, m.y);
					freeze(m.x, m.y + 1);
					freeze(m.x, m.y - 1);
					freeze(m.x + 1, m.y + 1);
					freeze(m.x - 1, m.y + 1);
					freeze(m.x - 1, m.y - 1);
					freeze(m.x + 1, m.y - 1);
				}
			}
			for (Attack a : attackList) {
				if (a.x == xNow && a.y == yNow) {
					screenShake = screenShakeMagnitude;
					if (map[a.unitX][a.unitY].type == 2) {
						map[a.unitX][a.unitY].mp = 0;
					}
					switch (a.type) {
					case 0:
						damage(a.x, a.y, 1);
						if (a.direction == 0 || a.direction == 2) {
							damage(a.x, a.y + 1, 1);
							damage(a.x, a.y - 1, 1);
						} else {
							damage(a.x + 1, a.y, 1);
							damage(a.x - 1, a.y, 1);
						}
						break;
					case 1:
						damage(a.x, a.y, 1);
						break;
					case 2:
						damage(a.x, a.y, 1);
						damage(a.x + 1, a.y + 1, 1);
						damage(a.x - 1, a.y + 1, 1);
						damage(a.x - 1, a.y - 1, 1);
						damage(a.x + 1, a.y - 1, 1);
						break;
					case 3:
						damage(a.x, a.y, 2);
						break;
					}
					map[a.unitX][a.unitY].attack = false;
					map[a.unitX][a.unitY].move = false;
					break;
				}
			}
			clearLists();
			break;
		case KeyEvent.VK_SPACE:
			if (turn == 0) {
				turn = 1;
			} else {
				turn = 0;
			}
			clearLists();
			// 턴이 돌아온 유닛들은 이동/공격/빙결 회복
			for (int x = 0; x < xSize; x++) {
				for (int y = 0; y < ySize; y++) {
					if (map[x][y] != null) {
						if (map[x][y].invincible > 0) {
							map[x][y].invincible--;
						}
						if (map[x][y].frozen > 0) {
							map[x][y].frozen--;
							if (map[x][y].frozen == 0) {
								damage(x, y, 1);
							}
						}
						// 빙결 상태이상이 풀릴 때 데미지를 받아 죽을 수 있으므로 한번 더 널인지 체크줘야함
						if (map[x][y] != null && map[x][y].frozen == 0) {
							map[x][y].attackType = map[x][y].basicAttackType;
							map[x][y].attack = (map[x][y].team == turn);
							map[x][y].move = (map[x][y].team == turn);
							if (map[x][y].team == turn && map[x][y].mp < map[x][y].maxMp) {
								map[x][y].mp++;
							}
						}
					}
				}
			}
			break;
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		}
	}
	public void print() {
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				if (map[i][j] == null) {
					System.out.println("0 0 0");
				} else {
					System.out.println(
							map[i][j].type + " " + map[i][j].hp + " " + ((double) map[i][j].mp / map[i][j].maxMp));
				}
			}
		}
		System.out.println();
	}
}
