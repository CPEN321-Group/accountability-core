package com.cpen321group.accountability.mainscreen.dashboard.functionpack;

public class ReportModel {
    private String report_name;
    private String report_detail;
    private String user_id;
    private String report_id;

    public ReportModel(String report_name, String report_detail, String user_id, String report_id) {
        this.report_name = report_name;
        this.report_detail = report_detail;
        this.user_id = user_id;
        this.report_id = report_id;
    }

    public String getReport_name() {
        return report_name;
    }

    public String getReport_detail() {
        return report_detail;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getReport_id() {
        return report_id;
    }

    public void setReport_name(String report_name) {
        this.report_name = report_name;
    }

    public void setReport_detail(String report_detail) {
        this.report_detail = report_detail;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }
}
