package Militia;

/**
 * <pre>
 * Militia
 *   |_ Unit.java
 * 1. 개요 : 모든 유닛들이 상속받는 클래스
 * 2. 작성일 : 2017. 6. 16.
 * </pre>
 *
 * @author : jjjj
 * @version : 1.0
 */
public class Unit {
	public int team;
	public int type; // 0-warrior 1-knight 2-wizard
	public int attackType; // 0-3칸 1-1칸 2-5칸(중심 + 대각선 4개) 3-1칸 2데미지
	public int basicAttackType;
	public double x, y; // 지금 좌표
	public double desX, desY; // 움직일 목적지
	public int hp, mp;
	public int maxHp, maxMp;
	public int frozen; // 빙결 상태이상
	public int invincible; // 무적 버프
	public boolean move, attack; // 이동/공격 가능 여부

	public void step() {
		if (x != desX || y != desY) {
			double dx = desX - x;
			double dy = desY - y;
			if (Math.sqrt(dx * dx + dy * dy) < 0.01) {
				x += dx;
				y += dy;
			} else {
				x += dx * 0.07 + Math.signum(dx) * 0.005;
				y += dy * 0.07 + Math.signum(dy) * 0.005;
			}
		}
	}
}
