package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.dummy.myerp.model.bean.comptabilite.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================

    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl() {
    }

    // ==================== Getters/Setters ====================
    @Override
    public List<CompteComptable> getListCompteComptable() {
        return getDaoProxy().getComptabiliteDao().getListCompteComptable();
    }

    @Override
    public List<JournalComptable> getListJournalComptable() {
        return getDaoProxy().getComptabiliteDao().getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) throws NotFoundException, FunctionalException {
        Integer refDerniereValeur;

        // Date.getYear() est actuellement Deprecated
        // Conversion de la Date de l'écriture comptable en LocalDate pour en extraire l'année
        LocalDate ecDate = pEcritureComptable.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Si la séquence existe déjà, on récupère la dernière valeur de la séquence
        // - Si la dernière valeur de la séquence a atteint 99999 (MAX), on renvoie une exception
        // - Sinon on récupère la valeur et on update la séquence à la valeur suivante
        // Sinon, on crée la séquence et on l'initialise à 1
        try {
            SequenceEcritureComptable vSEC = getDaoProxy().getComptabiliteDao().getSequenceJournalEcritureComptable(pEcritureComptable);
            if (vSEC.getDerniereValeur() == 99999) {
                throw new FunctionalException("Ce journal ne peut pas contenir plus d'écritures pour cette année");
            } else {
                refDerniereValeur = vSEC.getDerniereValeur();
                vSEC.setDerniereValeur(refDerniereValeur + 1);
                getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(vSEC);
            }
        } catch (NotFoundException vEx) {
            refDerniereValeur = 1;
            getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(pEcritureComptable.getJournal().getCode(), ecDate.getYear());
        }

        // On builde la référence suivant RG5
        pEcritureComptable.setReference(String.format("%s-%d/%05d", pEcritureComptable.getJournal().getCode(), ecDate.getYear(), refDerniereValeur));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }

    /**
     * Vérifie que l'écriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendamment du contexte (unicité de la référence, exercice comptable non clôturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'écriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
                                          new ConstraintViolationException(
                                              "L'écriture comptable ne respecte pas les contraintes de validation",
                                              vViolations));
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On teste le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (pEcritureComptable.getListLigneEcriture().size() < 2
            || vNbrCredit < 1
            || vNbrDebit < 1) {
            throw new FunctionalException(
                "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
        String[] parsedReference = pEcritureComptable.getReference().split("-|/");
        LocalDate ecDate = pEcritureComptable.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (!parsedReference[0].equals(pEcritureComptable.getJournal().getCode())) {
            throw new FunctionalException("Le code du journal compris dans la référence ne correspond pas à celui de l'écriture comptable. (RG5)");
        }
        if (Integer.parseInt(parsedReference[1]) != ecDate.getYear()) {
            throw new FunctionalException("L'année comprise dans la référence ne correspond pas à celle de l'écriture comptable. (RG5)");
        }
    }

    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getDaoProxy().getComptabiliteDao().getEcritureComptableByRef(
                    pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                    || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        this.checkEcritureComptable(pEcritureComptable);
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
}
