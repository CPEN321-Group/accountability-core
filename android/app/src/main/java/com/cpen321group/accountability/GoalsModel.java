package com.cpen321group.accountability;

public class GoalsModel {

    private String goal_name;
    private String goal_id;
    private String user_id;
    private double goal_price;

    /**
     *
     * @param goal_name: Name of a single gaal set by user
     * @param goal_id: id of a single goal set by user
     * @param user_id: Price of saving of a single goal set by user
     * @author <Yisheng Liu><liuyishengalan@hotmail.com/>
     */
    public GoalsModel(String goal_name, String goal_id, String user_id, double goal_price) {
        this.goal_name = goal_name;
        this.goal_id = goal_id;
        this.goal_price = goal_price;
        this.user_id = user_id;
    }

    public String getGoal_name() {
        return goal_name;
    }

    public void setGoal_name(String goal_name) {
        this.goal_name = goal_name;
    }

    public String getGoal_id() {
        return goal_id;
    }

    public void setGoal_id(String goal_id) {
        this.goal_id = goal_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id(){
        return user_id;
    }

    public double getGoal_price() {
        return goal_price;
    }

    public void setGoal_price(double goal_price) {
        this.goal_price = goal_price;
    }
}

