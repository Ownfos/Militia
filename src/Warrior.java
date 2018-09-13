package Militia;

/**
 * <pre>
 * Militia
 *   |_ Warrior.java
 * 1. 개요 : 이건 한국어로 뭐라 했더라...
 * 2. 작성일 : 2017. 6. 16.
 * </pre>
 *
 * @author : jjjj
 * @version : 1.0
 */
public class Warrior extends Unit {
	public Warrior(int team, double x, double y) {
		Game.units[team]++;
		this.team = team;
		this.x = x;
		this.y = y;
		desX = x;
		desY = y;
		maxHp = 3;
		maxMp = 4;
		frozen = 0;
		invincible = 0;
		type = 0;
		basicAttackType = 0;
		attackType = basicAttackType;
		hp = maxHp;
		mp = maxMp;
		attack = (team == 0);
		move = (team == 0);
	}
}
