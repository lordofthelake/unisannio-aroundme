package it.unisannio.aroundme.model;

class CompatibilityImpl implements Compatibility {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long userId;
	private float rank;
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}

	public void setRank(float rank) {
		this.rank = rank;
	}
	
	public float getRank() {
		return rank;
	}
}
