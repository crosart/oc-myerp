package com.dummy.myerp.model.bean.comptabilite;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;


/**
 * Bean représentant une Écriture Comptable
 */
@Data
public class EcritureComptable {

    // ==================== Attributs ====================
    /** The Id. */
    private Integer id;
    /** Journal comptable */
    @NotNull private JournalComptable journal;
    /** The Reference. */
    @Pattern(regexp = "[a-zA-Z]{2,5}-\\d{4}\\/\\d{5}")
    private String reference;
    /** The Date. */
    @NotNull private Date date;

    /** The Libelle. */
    @NotNull
    @Size(min = 1, max = 200)
    private String libelle;

    /** La liste des lignes d'écriture comptable. */
    @Valid
    @Size(min = 2)
    private final List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();

    /**
     * Calcul et renvoie le total des montants au débit des lignes d'écriture
     *
     * @return {@link BigDecimal}, {@link BigDecimal#ZERO} si aucun montant au débit
     */
    public BigDecimal getTotalDebit() {
        BigDecimal totalDebit = BigDecimal.ZERO;
        for (LigneEcritureComptable vLEC : listLigneEcriture) {
            if (vLEC.getDebit() != null) {
                totalDebit = totalDebit.add(vLEC.getDebit());
            }
        }
        return totalDebit.setScale(2, BigDecimal.ROUND_UP);
    }

    /**
     * Calcul et renvoie le total des montants au crédit des lignes d'écriture
     *
     * @return {@link BigDecimal}, {@link BigDecimal#ZERO} si aucun montant au crédit
     */
    public BigDecimal getTotalCredit() {
        BigDecimal totalCredit = BigDecimal.ZERO;
        for (LigneEcritureComptable vLEC : listLigneEcriture) {
            if (vLEC.getCredit() != null) {
                totalCredit = totalCredit.add(vLEC.getCredit());
            }
        }
        return totalCredit.setScale(2, BigDecimal.ROUND_UP);
    }

    /**
     * Renvoie si l'écriture est équilibrée (TotalDebit = TotalCrédit)
     * @return boolean
     */
    public boolean isEquilibree() {
        return this.getTotalDebit().equals(getTotalCredit());
    }

    // ==================== Méthodes ====================
    @Override
    public String toString() {
        final StringBuilder vStB = new StringBuilder(this.getClass().getSimpleName());
        final String vSEP = ", ";
        vStB.append("{")
            .append("id=").append(id)
            .append(vSEP).append("journal=").append(journal)
            .append(vSEP).append("reference='").append(reference).append("'")
            .append(vSEP).append("date=").append(date)
            .append(vSEP).append("libelle='").append(libelle).append("'")
            .append(vSEP).append("totalDebit=").append(this.getTotalDebit().toPlainString())
            .append(vSEP).append("totalCredit=").append(this.getTotalCredit().toPlainString())
            .append(vSEP).append("listLigneEcriture=[\n")
            .append(StringUtils.join(listLigneEcriture, "\n")).append("\n]")
            .append("}");
        return vStB.toString();
    }
}
