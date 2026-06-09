package br.com.spacereport.domain.model;

import java.time.LocalDate;

public class ActionHistory extends DomainEntity {

    private final Alert alert;
    private final String description;
    private final LocalDate actionDate;
    private final String responsible;

    public ActionHistory(Long id, Alert alert, String description,
                         LocalDate actionDate, String responsible) {
        super(id);
        this.alert = alert;
        this.description = description;
        this.actionDate = actionDate;
        this.responsible = responsible;
    }

    public Alert getAlert() { return alert; }
    public String getDescription() { return description; }
    public LocalDate getActionDate() { return actionDate; }
    public String getResponsible() { return responsible; }

    @Override
    public String toString() {
        return "ActionHistory{id=" + id + ", responsible='" + responsible +
               "', date=" + actionDate + "}";
    }
}
