package com.kerware.simulateurreusine;

/**
 * Applique le barème progressif de l'impôt sur le revenu.
 *
 * Le calcul repose sur le quotient familial : le revenu fiscal de référence
 * est divisé par le nombre de parts, taxé tranche par tranche, puis
 * multiplié en retour par le nombre de parts.
 */
public class CalculateurImpotBrut {

    private final int[] limitesTranches;
    private final double[] tauxTranches;

    /**
     * Construit le calculateur avec le barème fourni.
     *
     * @param limitesTranches limites basses de chaque tranche (euros)
     * @param tauxTranches    taux marginaux correspondants
     */
    public CalculateurImpotBrut(int[] limitesTranches, double[] tauxTranches) {
        this.limitesTranches = limitesTranches.clone();
        this.tauxTranches = tauxTranches.clone();
    }

    /**
     * Calcule l'impôt brut pour un revenu fiscal de référence donné.
     *
     * @param revenuFiscalReference revenu fiscal de référence (après abattement)
     * @param nombreParts           nombre de parts du quotient familial
     * @return impôt brut arrondi à l'euro
     */
    public long calculer(double revenuFiscalReference, double nombreParts) {
        double revenuParPart = revenuFiscalReference / nombreParts;
        double impotParPart = appliquerBareme(revenuParPart);
        return Math.round(impotParPart * nombreParts);
    }

    /**
     * Applique le barème progressif à un revenu par part.
     *
     * @param revenuParPart revenu imposable ramené à une part
     * @return montant d'impôt correspondant à une part
     */
    private double appliquerBareme(double revenuParPart) {
        double impot = 0.0;
        for (int tranche = 0; tranche < tauxTranches.length; tranche++) {
            int limiteHaute = obtenirLimiteHaute(tranche);
            if (revenuParPart <= limitesTranches[tranche]) {
                break;
            }
            double revenuDansTranche = Math.min(revenuParPart, limiteHaute)
                - limitesTranches[tranche];
            impot += revenuDansTranche * tauxTranches[tranche];
        }
        return impot;
    }

    /**
     * Retourne la limite haute d'une tranche.
     * Pour la dernière tranche, retourne Integer.MAX_VALUE.
     *
     * @param tranche index de la tranche
     * @return limite haute de la tranche
     */
    private int obtenirLimiteHaute(int tranche) {
        if (tranche + 1 < limitesTranches.length) {
            return limitesTranches[tranche + 1];
        }
        return Integer.MAX_VALUE;
    }
}
