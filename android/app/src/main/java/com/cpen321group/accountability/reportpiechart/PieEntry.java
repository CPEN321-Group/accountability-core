package com.cpen321group.accountability.reportpiechart;

public class PieEntry {
    private float value;
    private int color;
    private float degreeStart;
    private float degreeEnd;
    private boolean isSelected;

    public PieEntry(float value, int color, boolean isSelected) {
        this.value = value;
        this.color = color;
        this.isSelected = isSelected;
    }

    public float getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public float getDegreeStart() {
        return degreeStart;
    }

    public float getDegreeEnd() {
        return degreeEnd;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDegreeStart(float degreeStart) {
        this.degreeStart = degreeStart;
    }

    public void setDegreeEnd(float degreeEnd) {
        this.degreeEnd = degreeEnd;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
