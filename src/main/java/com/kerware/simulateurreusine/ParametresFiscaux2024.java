package com.kerware.simulateurreusine;

/**
 * Paramètres fiscaux pour le calcul de l'impôt sur le revenu 2024
 * (sur les revenus perçus en 2023).
 *
 * Toutes les constantes sont issues du Bulletin Officiel des Finances Publiques.
 * Centraliser ces valeurs ici permet de mettre à jour facilement le barème
 * chaque année sans toucher à la logique de calcul.
 */
public final class ParametresFiscaux2024 {

    // -------------------------------------------------------------------------
    // Barème progressif de l'impôt (tranches et taux)
    // -------------------------------------------------------------------------

    /** Nombre de tranches du barème progressif. */
    public static final int NB_TRANCHES = 5;

    /**
     * Limites basses de chaque tranche du barème (en euros).
     * La dernière limite haute est considérée comme infinie.
     */
    public static final int[] LIMITES_TRANCHES = {0, 11294, 28797, 82341, 177106};

    /** Taux marginaux d'imposition associés à chaque tranche. */
    public static final double[] TAUX_TRANCHES = {0.0, 0.11, 0.30, 0.41, 0.45};

    // -------------------------------------------------------------------------
    // Abattement forfaitaire pour frais professionnels
    // -------------------------------------------------------------------------

    /** Taux d'abattement forfaitaire de 10 %. */
    public static final double TAUX_ABATTEMENT = 0.10;

    /** Plafond de l'abattement forfaitaire (en euros). */
    public static final int ABATTEMENT_MAX = 14171;

    /** Plancher de l'abattement forfaitaire (en euros). */
    public static final int ABATTEMENT_MIN = 495;

    // -------------------------------------------------------------------------
    // Plafonnement du quotient familial (demi-part)
    // -------------------------------------------------------------------------

    /** Avantage fiscal maximal accordé par demi-part supplémentaire (en euros). */
    public static final double PLAFOND_AVANTAGE_DEMI_PART = 1759.0;

    // -------------------------------------------------------------------------
    // Décote
    // -------------------------------------------------------------------------

    /** Seuil de déclenchement de la décote pour un contribuable seul (en euros). */
    public static final double SEUIL_DECOTE_SEUL = 1929.0;

    /** Seuil de déclenchement de la décote pour un couple (en euros). */
    public static final double SEUIL_DECOTE_COUPLE = 3191.0;

    /** Décote maximale pour un contribuable seul (en euros). */
    public static final double DECOTE_MAX_SEUL = 873.0;

    /** Décote maximale pour un couple (en euros). */
    public static final double DECOTE_MAX_COUPLE = 1444.0;

    /** Taux appliqué à l'impôt brut pour calculer la décote. */
    public static final double TAUX_DECOTE = 0.4525;

    // -------------------------------------------------------------------------
    // Nombre de parts des déclarants selon la situation familiale
    // -------------------------------------------------------------------------

    /** Nombre de parts pour un foyer avec un seul déclarant. */
    public static final double PARTS_DECLARANT_SEUL = 1.0;

    /** Nombre de parts pour un foyer avec deux déclarants (marié ou pacsé). */
    public static final double PARTS_DECLARANTS_COUPLE = 2.0;

    /** Nombre de demi-parts par enfant (dans la limite des 2 premiers). */
    public static final double DEMI_PART_ENFANT = 0.5;

    /** Nombre de parts entières accordées à partir du 3e enfant. */
    public static final double PART_ENFANT_SUPPLEMENTAIRE = 1.0;

    /** Seuil à partir duquel les enfants donnent droit à une part entière. */
    public static final int SEUIL_ENFANT_PART_ENTIERE = 2;

    /** Demi-part supplémentaire pour parent isolé ayant des enfants à charge. */
    public static final double DEMI_PART_PARENT_ISOLE = 0.5;

    /** Demi-part supplémentaire par enfant en situation de handicap. */
    public static final double DEMI_PART_ENFANT_HANDICAPE = 0.5;

    private ParametresFiscaux2024() {
        // Classe utilitaire : instanciation interdite
    }
}
