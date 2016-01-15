package com.gxb.iwill.model;

public class Goal {


	private long id;
	private String description,type;

	public Goal(long id, String description, String type){
		this.id=id;
		this.description=description;
		this.type=type;
	}
	public Goal(){
	}

	public long getId(){
		return id;
	}
	public String getDescription(){
		return description;
	}

    /**
     *
     * @return String of 'Year' or 'Day'
     */
    public String getType() { return type; }

	public void setDescription(String description) {
		this.description=description;
	}
    public void setId(long id) {
        this.id=id;
    }

    /**
     * Set the type of goal it is
     * <p>
     *     'Year' is the yearly goal
     *     'Day' is a daily goal
     * </p>
     * Will set value to NULL if text isnt year or day
     * @param type - 'Year' or 'Day'
     */
    public void setType(String type) {
        if( type.equals("Year") || type.equals("Day"))
            this.type=type;
        else this.type=null;
	}
}
