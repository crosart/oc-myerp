package com.dummy.myerp.model.bean.comptabilite;


import lombok.Getter;
import lombok.Setter;

/**
 * Bean représentant une séquence pour les références d'écriture comptable
 */
@Getter
@Setter
public class SequenceEcritureComptable {

    // ==================== Attributs ====================
    /** Le code du journal comptable */
    private String codeJournal;
    /** L'année */
    private Integer annee;
    /** La dernière valeur utilisée */
    private Integer derniereValeur;

    // ==================== Constructeurs ====================
    /**
     * Constructeur
     */
    public SequenceEcritureComptable() {
    }

    /**
     * Constructeur
     *
     * @param pCodeJournal -
     * @param pAnnee -
     * @param pDerniereValeur -
     */
    public SequenceEcritureComptable(String pCodeJournal, Integer pAnnee, Integer pDerniereValeur) {
        annee = pAnnee;
        derniereValeur = pDerniereValeur;
    }


    // ==================== Méthodes ====================
    @Override
    public String toString() {
        final StringBuilder vStB = new StringBuilder(this.getClass().getSimpleName());
        final String vSEP = ", ";
        vStB.append("{")
            .append("codeJournal=").append(codeJournal)
            .append(vSEP).append("annee=").append(annee)
            .append(vSEP).append("derniereValeur=").append(derniereValeur)
            .append("}");
        return vStB.toString();
    }
}
