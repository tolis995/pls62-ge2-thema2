package ge2thema4;

public class WSNode {   
	private boolean isCH;
	private boolean wasCH;
	private int roundsCounter;
	private double threshold;
	private double randNum;
	private boolean belongToG;
	
	
	public WSNode() {
		this.isCH = false;
		this.wasCH = false;
		this.roundsCounter = 0;
		this.randNum=0;
		this.threshold = 1;
		this.belongToG = true;
	}
	
	
	
	public double getRandNum() {
		return randNum;
	}



	public void setRandNum(double randNum) {
		this.randNum = randNum;
	}



	public boolean isCH() {
		return isCH;
	}
	public void setCH(boolean isCH) {
		this.isCH = isCH;
	}
	public boolean isOldCH() {
		return wasCH;
	}
	public void setOldCH(boolean wasCH) {
		this.wasCH = wasCH;
	}
	public int getRoundsCounter() {
		return roundsCounter;
	}
	public void setRoundsCounter(int roundsCounter) {
		this.roundsCounter = roundsCounter;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public boolean belongsToG() {
		return belongToG;
	}
	public void setBelongsToG(boolean belongToG) {
		this.belongToG = belongToG;
	}
	

}
