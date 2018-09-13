/**
 * 
 */
package Militia;

/**
 * <pre>
 * Militia
 *   |_ Particle.java
 * 1. 개요 : 버프, 디버프 뿅뿅이와 타격 이펙트
 * 2. 작성일 : 2017. 6. 17.
 * </pre>
 *
 * @author : jjjj
 * @version : 1.0
 */
public class Particle {
	public double x, y; // 현재 좌표
	public double angle; // 이동할 각도
	public double speed; // 이동 속도
	public double size; // 파티클 크기
	public int life; // 현재 나이(?)
	public int maxLife; // 최대 수명

	public Particle(double x, double y, double angle, double speed, double size, int maxLife) {
		this.x = x;
		this.y = y;
		this.angle = angle;
		this.speed = speed;
		this.size = size;
		this.maxLife = maxLife;
		life = 0;
	}
}
