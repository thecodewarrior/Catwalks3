package catwalks.movement.capability;

public interface ICWEntityData {
	// interface will always be casted down to a CWEntityData object, no getters or setters required
	public static class CWEntityData implements ICWEntityData {
		public int jumpTimer = 0;
		public double lastTickLadderSpeed = -1;
		public double nerdPoleMoveHeight, nerdPoleOrigionalY;
		public int nerdPoleTicks;
		public int nerdPoleTicksPassed;
	}
}
