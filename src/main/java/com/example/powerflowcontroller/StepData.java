package com.example.powerflowcontroller;

public class StepData {
    private String stepNumber;
    private String action;
    private String condition1;
    private String condition2;
    private String condition3;

    public StepData(String stepNumber, String action, String condition1, String condition2, String condition3) {
        this.stepNumber = stepNumber;
        this.action = action;
        this.condition1 = condition1;
        this.condition2 = condition2;
        this.condition3 = condition3;
    }

    public String getStepNumber() { return stepNumber; }
    public String getAction() { return action; }
    public String getCondition1() { return condition1; }
    public String getCondition2() { return condition2; }
    public String getCondition3() { return condition3; }
}