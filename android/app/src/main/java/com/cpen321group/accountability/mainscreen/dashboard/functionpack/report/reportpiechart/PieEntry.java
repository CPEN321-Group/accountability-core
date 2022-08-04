package com.cpen321group.accountability.mainscreen.dashboard.functionpack.report.reportpiechart;

public class PieEntry {
    private float value;
    private int color;
    private float degreeStart;
    private float degreeEnd;
    private boolean isSelected;
    private String entryName;

    public PieEntry(float value, int color, boolean isSelected, String entryName) {
        this.value = value;
        this.color = color;
        this.isSelected = isSelected;
        this.entryName = entryName;
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

    public String getEntryName() {
        return entryName;
    }

    //    public void setValue(float value) {
//        this.value = value;
//    }

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
