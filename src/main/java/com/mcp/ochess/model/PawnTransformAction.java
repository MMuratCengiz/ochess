package com.mcp.ochess.model;

public class PawnTransformAction {
    private String transformTo;

    public PawnTransformAction() {

    }

    public PawnTransformAction(String transformTo) {
        this.transformTo = transformTo;
    }

    public void setTransformTo(String transformTo) {
        this.transformTo = transformTo;
    }

    public String getTransformTo() {
        return transformTo;
    }
}
