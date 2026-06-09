package br.com.spacereport.domain.model;

import java.time.LocalDate;

public class Alert {

    private final Long id;
    private final RiskAnalysis riskAnalysis;
    private final String message;
    private final LocalDate issueDate;
    private AlertStatus status;

    public Alert(Long id, RiskAnalysis riskAnalysis, String message,
                 LocalDate issueDate, AlertStatus status) {
        this.id = id;
        this.riskAnalysis = riskAnalysis;
        this.message = message;
        this.issueDate = issueDate;
        this.status = status;
    }

    public void acknowledge() {
        this.status = AlertStatus.ACKNOWLEDGED;
    }

    public void resolve() {
        this.status = AlertStatus.RESOLVED;
    }

    public void markSent() {
        this.status = AlertStatus.SENT;
    }

    public Long getId() { return id; }
    public RiskAnalysis getRiskAnalysis() { return riskAnalysis; }
    public String getMessage() { return message; }
    public LocalDate getIssueDate() { return issueDate; }
    public AlertStatus getStatus() { return status; }

    @Override
    public String toString() {
        return "Alert{id=" + id + ", status=" + status +
               ", level=" + riskAnalysis.getRiskLevel() +
               ", asset=" + riskAnalysis.getSpaceAsset().getName() +
               ", msg='" + message + "'}";
    }
}
