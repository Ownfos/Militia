/**
 * 
 */
package Militia;

/**
 * <pre>
 * Militia
 *   |_ Attack.java
 * 1. 개요 : 공격 가능한 칸
 * 2. 작성일 : 2017. 6. 17.
 * </pre>
 *
 * @author : jjjj
 * @version : 1.0
 */
public class Attack {
	public int type; // 공격의 종류
	public int direction; // 공격의 방향 0-오른쪽 1-위 2-왼쪽 3-아래
	public int x, y; // 공격할 칸의 좌표
	public int unitX, unitY; // 공격하는 유닛의 좌표

	public Attack(int type, int direction, int x, int y, int unitX, int unitY) {
		this.type = type;
		this.direction = direction;
		this.x = x;
		this.y = y;
		this.unitX = unitX;
		this.unitY = unitY;
	}
}
