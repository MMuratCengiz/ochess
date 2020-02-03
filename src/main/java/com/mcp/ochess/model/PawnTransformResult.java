package com.mcp.ochess.model;

public class PawnTransformResult extends ActionResult {
    private String newPieceType;
    private String actionResult;
    private String transformPos;

    public PawnTransformResult() {
        super("PawnTransform");
    }

    public String getTransformPos() {
        return transformPos;
    }

    public void setTransformPos(String transformPos) {
        this.transformPos = transformPos;
    }

    public String getActionResult() {
        return actionResult;
    }

    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }

    public void setNewPieceType(String newPieceType) {
        this.newPieceType = newPieceType;
    }

    public String getNewPieceType() {
        return newPieceType;
    }
}
