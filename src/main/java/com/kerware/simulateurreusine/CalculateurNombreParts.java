package com.kerware.simulateurreusine;

/**
 * Calcule le nombre de parts du quotient familial pour un foyer fiscal.
 *
 * Le quotient familial tient compte de la situation familiale,
 * du nombre d'enfants à charge, des enfants handicapés et
 * de la situation de parent isolé.
 */
public class CalculateurNombreParts {

    /**
     * Détermine le nombre de parts attribuées aux seuls déclarants
     * selon leur situation familiale.
     *
     * @param situation situation familiale du contribuable
     * @return nombre de parts des déclarants (1 ou 2)
     */
    public double calculerPartsDeclarants(SituationFamiliale situation) {
        switch (situation) {
            case MARIE:
            case PACSE:
                return ParametresFiscaux2024.PARTS_DECLARANTS_COUPLE;
            case CELIBATAIRE:
            case DIVORCE:
            case VEUF:
            default:
                return ParametresFiscaux2024.PARTS_DECLARANT_SEUL;
        }
    }

    /**
     * Calcule le nombre total de parts du foyer fiscal en tenant compte
     * des enfants, des situations de handicap et du statut de parent isolé.
     *
     * @param partsDeclarants nombre de parts des déclarants
     * @param nbEnfants       nombre d'enfants à charge
     * @param nbHandicapes    nombre d'enfants en situation de handicap
     * @param parentIsole     vrai si le contribuable est parent isolé
     * @return nombre total de parts du foyer fiscal
     */
    public double calculerPartsTotales(double partsDeclarants,
                                       int nbEnfants,
                                       int nbHandicapes,
                                       boolean parentIsole) {
        double parts = partsDeclarants;
        parts += calculerPartsEnfants(nbEnfants);
        parts += calculerDemiPartParentIsole(parentIsole, nbEnfants);
        parts += calculerPartsHandicap(nbHandicapes);
        return parts;
    }

    /**
     * Calcule les parts supplémentaires liées aux enfants à charge.
     * Les 2 premiers enfants donnent chacun 0,5 part ;
     * à partir du 3e, chaque enfant donne 1 part entière.
     *
     * @param nbEnfants nombre d'enfants à charge
     * @return parts supplémentaires attribuées pour les enfants
     */
    private double calculerPartsEnfants(int nbEnfants) {
        if (nbEnfants <= ParametresFiscaux2024.SEUIL_ENFANT_PART_ENTIERE) {
            return nbEnfants * ParametresFiscaux2024.DEMI_PART_ENFANT;
        }
        return ParametresFiscaux2024.PART_ENFANT_SUPPLEMENTAIRE
            + (nbEnfants - ParametresFiscaux2024.SEUIL_ENFANT_PART_ENTIERE)
            * ParametresFiscaux2024.PART_ENFANT_SUPPLEMENTAIRE;
    }

    /**
     * Attribue la demi-part supplémentaire au parent isolé ayant des enfants.
     *
     * @param parentIsole vrai si parent isolé
     * @param nbEnfants   nombre d'enfants à charge
     * @return 0,5 si parent isolé avec enfants, 0 sinon
     */
    private double calculerDemiPartParentIsole(boolean parentIsole, int nbEnfants) {
        if (parentIsole && nbEnfants > 0) {
            return ParametresFiscaux2024.DEMI_PART_PARENT_ISOLE;
        }
        return 0.0;
    }

    /**
     * Calcule les parts supplémentaires pour les enfants handicapés.
     *
     * @param nbHandicapes nombre d'enfants en situation de handicap
     * @return demi-parts supplémentaires pour le handicap
     */
    private double calculerPartsHandicap(int nbHandicapes) {
        return nbHandicapes * ParametresFiscaux2024.DEMI_PART_ENFANT_HANDICAPE;
    }
}
