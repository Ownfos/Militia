/**
 * 
 */
package Militia;

/**
 * <pre>
 * Militia
 *   |_ Move.java
 * 1. 개요 : 이동/텔레포트 가능한 칸
 * 2. 작성일 : 2017. 6. 17.
 * </pre>
 *
 * @author : jjjj
 * @version : 1.0
 */
public class Move {
	public int x, y; // 이동할 칸의 좌표
	public int unitX, unitY; // 이동할 유닛의 좌표

	public Move(int x, int y, int unitX, int unitY) {
		this.x = x;
		this.y = y;
		this.unitX = unitX;
		this.unitY = unitY;
	}
}
