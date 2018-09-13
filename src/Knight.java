/**
 * 
 */
package Militia;

/**
 * <pre>
 * Militia
 *   |_ Knight.java
 * 1. 개요 : 기사
 * 2. 작성일 : 2017. 6. 17.
 * </pre>
 *
 * @author : jjjj
 * @version : 1.0
 */
public class Knight extends Unit {
	public Knight(int team, double x, double y) {
		Game.units[team]++;
		this.team = team;
		this.x = x;
		this.y = y;
		desX = x;
		desY = y;
		maxHp = 5;
		maxMp = 3;
		frozen = 0;
		invincible = 0;
		type = 1;
		basicAttackType = 1;
		attackType = basicAttackType;
		hp = maxHp;
		mp = maxMp;
		attack = (team == 0);
		move = (team == 0);
	}
}
