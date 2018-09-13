/**
 * 
 */
package Militia;

/**
 * <pre>
 * Militia
 *   |_ Wizard.java
 * 1. 개요 : 마법사
 * 2. 작성일 : 2017. 6. 17.
 * </pre>
 *
 * @author : jjjj
 * @version : 1.0
 */
public class Wizard extends Unit {
	public Wizard(int team, double x, double y) {
		Game.units[team]++;
		this.team = team;
		this.x = x;
		this.y = y;
		desX = x;
		desY = y;
		maxHp = 2;
		maxMp = 4;
		frozen = 0;
		invincible = 0;
		type = 2;
		basicAttackType = 2;
		attackType = basicAttackType;
		hp = maxHp;
		mp = maxMp;
		attack = (team == 0);
		move = (team == 0);
	}
}
