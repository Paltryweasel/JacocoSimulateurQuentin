package com.kerware.simulateurreusine;

/**
 * Enumération des situations familiales reconnues par le fisc français.
 * Chaque valeur correspond à une case de la déclaration de revenus.
 */
public enum SituationFamiliale {
    /** Célibataire : 1 part de quotient familial. */
    CELIBATAIRE,
    /** Pacsé(e) : 2 parts de quotient familial (traitement identique au marié). */
    PACSE,
    /** Marié(e) : 2 parts de quotient familial. */
    MARIE,
    /** Divorcé(e) : 1 part de quotient familial. */
    DIVORCE,
    /** Veuf(ve) : 1 part de quotient familial. */
    VEUF
}
