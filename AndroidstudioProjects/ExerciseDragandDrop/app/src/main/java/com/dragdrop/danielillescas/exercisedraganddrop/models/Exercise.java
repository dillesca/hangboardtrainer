package com.dragdrop.danielillescas.exercisedraganddrop.models;


public class Exercise {

	private long workoutId;
	private int exerciseNum, numSeconds=0, amount=0;
	private String type, right, left;

	/**
	 * Constructor for if exertcise is hanging or resting
	 * @param id
	 * @param exerciseNum
	 * @param time
	 * @param type
	 * @param rightHand
	 * @param leftHand
	 */
	public Exercise(int id, int exerciseNum, int time, String type, String rightHand, String leftHand){
		this.workoutId=id;
		this.exerciseNum=exerciseNum;
		this.numSeconds=time;
		this.type=type;
		this.right=rightHand;
		this.left=leftHand;
	}

	/**
	 * Constructor for if exercise is leg lifts or pull ups
	 * @param id
	 * @param exerciseNum
	 * @param type
	 * @param rightHand
	 * @param leftHand
	 * @param amount
	 */
	public Exercise(int id, int exerciseNum, String type, String rightHand, String leftHand, int amount){
		this.workoutId=id;
		this.exerciseNum=exerciseNum;
		this.type=type;
		this.right=rightHand;
		this.left=leftHand;
		this.amount=amount;
	}

	public Exercise(){

	}

	public String getRightHand() {
		return right;
	}

	/**
	 * Gets the exercise type
	 * Hanging, PullUps, LegLifts, Rest
	 * @return
	 */
	public String getExerciseType() {
		return type;
	}

	public void setTime(int seconds) {
		this.numSeconds=seconds;

	}

	public int getTime(){		
		return numSeconds;
	}

	/**
	 * Sets the type of exercise
	 * Hanging, PullUps, LegLifts, Rest
	 * @param string
	 */
	public void setExerciseType(String string) {
		this.type=string;

	}

	public void setRightHand(String string) {
		this.right=string;

	}

	public void setLeftHand(String string) {
		this.left=string;

	}
	public void setWorkoutId(long int1) {
		this.workoutId=int1;

	}
	public int getExerciseNumber() {
		return exerciseNum;
	}

	public void setExerciseNumber(int num){
		this.exerciseNum=num;
	}

	public long getWorkoutId(){
		return workoutId;
	}

	public String getLeftHand() {
		return left;
	}

	/**
	 * sets the amount of pull ups or leg lifts
	 */
	public void setAmount(int amount){
		this.amount=amount;
	}

	public int getAmount(){
		return amount;
	}

	public long getHours() {
		int length = getTime();
		int hours = length/3600;
		return hours;
	}

	public long getMinutes() {
		int length = getTime();
		return length/60 - getHours() * 60;
	}

	public long getFormattedSeconds(){
		int length = getTime();
		return length - getMinutes()*60;
	}

	@Override 
	public String toString() {
		return getMessage();
	}

	private String getMessage() {
		String message = "";

		//Check if new exercise is rest
		if(getExerciseType().equals("Rest")) {
			String plural = getTime() == 1 ? "second" : "seconds";
			message = String.format("Rest for %d %s.", getTime(), plural);
		} //Check if old exercise was rest and Check if both hands changed
		else { 
			String right = getRightHand();
			String left = getLeftHand();
			message += getTypeSentence();
			message += " " + getHandSentence(true,right);
			message += " " + getHandSentence(false,left);
		}
		return message;
	}
	
	public String getTypeSentence() {
		String message = "";
		if(getExerciseType().equals("Leg Lifts")){
			String plural = getAmount() == 1 ? "leg lift" : "leg lifts";
			message += String.format("Do %d  %s.", getAmount(), plural);
		}
		else if(getExerciseType().equals("Pull Ups")) {
			String plural = getAmount() == 1 ? "pull up" : "pull ups";
			message += String.format("Do %d  %s.", getAmount(), plural);
		} else {
			String plural = getTime() == 1 ? "second" : "seconds";
			message += String.format("%s for %d  %s.", getExerciseType(), getTime(), plural);
		}
		return message;
	}
	
	public String getHandSentence(boolean isRight, String hold) {
		String sentence = "";
		String hand = "left";
		if(isRight) hand = "right";

		if(hold.equals("Off"))
			sentence = String.format("%s hand off.",hand);
		else if(hold.equals("Mini-Jug"))
			sentence = hand + " hand on a mini jug.";
		else if(hold.equals("Sloper"))
			sentence = hand + " hand on sloeper";
		else 
			sentence = String.format("%s hand on %s.",hand, hold);
		return sentence;
	}

}
