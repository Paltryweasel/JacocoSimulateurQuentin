package com.kerware.simulateurreusine;

/**
 * Applique le plafonnement du quotient familial.
 *
 * L'avantage fiscal procuré par les parts supplémentaires (enfants, handicap,
 * parent isolé) est plafonné à un certain montant par demi-part.
 * Si l'avantage calculé dépasse ce plafond, l'impôt est recalculé en
 * soustrayant uniquement le plafond autorisé.
 */
public class PlafonnementQuotientFamilial {

    private final double plafondParDemiPart;

    /**
     * Construit le plafonnement avec le montant maximal par demi-part.
     *
     * @param plafondParDemiPart avantage maximal accordé par demi-part (euros)
     */
    public PlafonnementQuotientFamilial(double plafondParDemiPart) {
        this.plafondParDemiPart = plafondParDemiPart;
    }

    /**
     * Applique le plafonnement et retourne l'impôt plafonné.
     *
     * @param impotDeclarants impôt calculé avec les seules parts des déclarants
     * @param impotFoyer      impôt calculé avec toutes les parts du foyer
     * @param partsDeclarants nombre de parts des déclarants
     * @param partsTotales    nombre de parts totales du foyer
     * @return impôt après plafonnement du quotient familial
     */
    public double appliquer(double impotDeclarants, double impotFoyer,
                             double partsDeclarants, double partsTotales) {
        double baisseImpot = impotDeclarants - impotFoyer;
        double partsSupplementaires = partsTotales - partsDeclarants;
        double plafondTotal = (partsSupplementaires / ParametresFiscaux2024.DEMI_PART_ENFANT)
            * plafondParDemiPart;

        if (baisseImpot >= plafondTotal) {
            return impotDeclarants - plafondTotal;
        }
        return impotFoyer;
    }
}
