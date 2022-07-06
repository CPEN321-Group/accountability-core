package com.cpen321group.accountability;

public class GoalsModel {

    private String goal_name;
    private String goal_description;
    private double goal_price;

    /**
     *
     * @param goal_name: Name of a single gaal set by user
     * @param goal_description: short description of a single goal set by user
     * @param goal_price: Price of saving of a single goal set by user
     * @author <Yisheng Liu><liuyishengalan@hotmail.com/>
     */
    public GoalsModel(String goal_name, String goal_description, double goal_price) {
        this.goal_name = goal_name;
        this.goal_description = goal_description;
        this.goal_price = goal_price;
    }

    public String getGoal_name() {
        return goal_name;
    }

    public void setGoal_name(String goal_name) {
        this.goal_name = goal_name;
    }

    public String getGoal_description() {
        return goal_description;
    }

    public void setGoal_description(String goal_description) {
        this.goal_description = goal_description;
    }

    public double getGoal_price() {
        return goal_price;
    }

    public void setGoal_price(double goal_price) {
        this.goal_price = goal_price;
    }
}

