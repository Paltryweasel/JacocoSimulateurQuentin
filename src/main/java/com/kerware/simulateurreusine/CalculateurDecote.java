package com.kerware.simulateurreusine;

/**
 * Calcule la décote applicable à l'impôt brut.
 *
 * La décote bénéficie aux contribuables dont l'impôt est modeste.
 * Son montant dépend de la situation (seul ou couple) et diminue
 * progressivement à mesure que l'impôt augmente.
 */
public class CalculateurDecote {

    private final double seuilSeul;
    private final double seuilCouple;
    private final double decoteMaxSeul;
    private final double decoteMaxCouple;
    private final double tauxDecote;

    /**
     * Construit le calculateur de décote avec les paramètres fiscaux.
     *
     * @param seuilSeul    seuil d'impôt à partir duquel la décote s'applique (seul)
     * @param seuilCouple  seuil d'impôt à partir duquel la décote s'applique (couple)
     * @param decoteMaxSeul décote maximale pour un déclarant seul
     * @param decoteMaxCouple décote maximale pour un couple
     * @param tauxDecote   taux appliqué à l'impôt pour réduire la décote
     */
    public CalculateurDecote(double seuilSeul, double seuilCouple,
                              double decoteMaxSeul, double decoteMaxCouple,
                              double tauxDecote) {
        this.seuilSeul = seuilSeul;
        this.seuilCouple = seuilCouple;
        this.decoteMaxSeul = decoteMaxSeul;
        this.decoteMaxCouple = decoteMaxCouple;
        this.tauxDecote = tauxDecote;
    }

    /**
     * Calcule la décote pour un impôt brut et un nombre de parts déclarants.
     * La décote ne peut pas dépasser le montant de l'impôt lui-même.
     *
     * @param impotBrut      montant de l'impôt avant décote
     * @param partsDeclarants nombre de parts des déclarants (1 = seul, 2 = couple)
     * @return montant de la décote arrondi à l'euro
     */
    public long calculer(double impotBrut, double partsDeclarants) {
        double decote = 0.0;
        if (estDeclarantSeul(partsDeclarants) && impotBrut < seuilSeul) {
            decote = decoteMaxSeul - (impotBrut * tauxDecote);
        } else if (estCouple(partsDeclarants) && impotBrut < seuilCouple) {
            decote = decoteMaxCouple - (impotBrut * tauxDecote);
        }
        decote = Math.round(decote);
        if (impotBrut <= decote) {
            decote = impotBrut;
        }
        return (long) decote;
    }

    /**
     * Vérifie si le foyer fiscal est composé d'un seul déclarant.
     *
     * @param partsDeclarants nombre de parts des déclarants
     * @return vrai si déclarant seul
     */
    private boolean estDeclarantSeul(double partsDeclarants) {
        return partsDeclarants == ParametresFiscaux2024.PARTS_DECLARANT_SEUL;
    }

    /**
     * Vérifie si le foyer fiscal est un couple.
     *
     * @param partsDeclarants nombre de parts des déclarants
     * @return vrai si couple
     */
    private boolean estCouple(double partsDeclarants) {
        return partsDeclarants == ParametresFiscaux2024.PARTS_DECLARANTS_COUPLE;
    }
}
