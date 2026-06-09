package br.com.spacereport.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Alerta")
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "IdAlerta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdAnalise", nullable = false)
    private RiskAnalysisEntity riskAnalysis;

    @Column(name = "MensagemAlerta", nullable = false, length = 500)
    private String message;

    @Column(name = "DataEmissao", nullable = false)
    private LocalDate issueDate;

    @Column(name = "StatusAlerta", nullable = false, length = 20)
    private String status;

    public AlertEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public RiskAnalysisEntity getRiskAnalysis() { return riskAnalysis; }
    public void setRiskAnalysis(RiskAnalysisEntity riskAnalysis) { this.riskAnalysis = riskAnalysis; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
