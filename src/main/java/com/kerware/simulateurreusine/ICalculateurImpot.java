package com.kerware.simulateurreusine;

/**
 * Contrat d'un calculateur d'impôt sur le revenu.
 * Les paramètres du foyer fiscal sont injectés via les setters,
 * puis le calcul est déclenché par {@link #calculImpotSurRevenuNet()}.
 */
public interface ICalculateurImpot {

    /**
     * Définit le revenu net annuel du foyer fiscal.
     *
     * @param rn revenu net en euros
     */
    void setRevenusNet(int rn);

    /**
     * Définit la situation familiale du contribuable.
     *
     * @param sf situation familiale
     */
    void setSituationFamiliale(SituationFamiliale sf);

    /**
     * Définit le nombre d'enfants à charge.
     *
     * @param nbe nombre d'enfants
     */
    void setNbEnfantsACharge(int nbe);

    /**
     * Définit le nombre d'enfants en situation de handicap.
     *
     * @param nbesh nombre d'enfants handicapés
     */
    void setNbEnfantsSituationHandicap(int nbesh);

    /**
     * Indique si le contribuable est parent isolé.
     *
     * @param pi vrai si parent isolé
     */
    void setParentIsole(boolean pi);

    /**
     * Déclenche le calcul de l'impôt sur le revenu net.
     * Doit être appelé après avoir renseigné tous les paramètres.
     */
    void calculImpotSurRevenuNet();

    /**
     * Retourne le revenu fiscal de référence (après abattement).
     *
     * @return revenu fiscal de référence en euros
     */
    int getRevenuFiscalReference();

    /**
     * Retourne le montant de l'abattement appliqué.
     *
     * @return abattement en euros
     */
    int getAbattement();

    /**
     * Retourne le nombre de parts du foyer fiscal.
     *
     * @return nombre de parts (peut être décimal)
     */
    int getNbPartsFoyerFiscal();

    /**
     * Retourne l'impôt calculé avant application de la décote.
     *
     * @return impôt avant décote en euros
     */
    int getImpotAvantDecote();

    /**
     * Retourne le montant de la décote appliquée.
     *
     * @return décote en euros
     */
    int getDecote();

    /**
     * Retourne l'impôt final après décote.
     *
     * @return impôt sur le revenu net en euros
     */
    int getImpotSurRevenuNet();
}
