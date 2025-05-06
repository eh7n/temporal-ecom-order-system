package io.halvorsen.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = FinalFraudDecision.class)
public class FinalFraudDecision {

    private String decision;

    public FinalFraudDecision() {

    }

    public FinalFraudDecision(String decision) {
        this.decision = decision;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

}
