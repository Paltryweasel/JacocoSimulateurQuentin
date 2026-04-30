package com.kerware.simulateurreusine;

/**
 * Calcule l'abattement forfaitaire pour frais professionnels.
 *
 * L'abattement représente 10 % du revenu net, encadré par un plancher
 * et un plafond définis par le barème fiscal en vigueur.
 */
public class CalculateurAbattement {

    private final double taux;
    private final int min;
    private final int max;

    /**
     * Construit un calculateur d'abattement avec les paramètres donnés.
     *
     * @param taux taux d'abattement (ex. 0.10 pour 10 %)
     * @param min  montant minimal de l'abattement
     * @param max  montant maximal de l'abattement
     */
    public CalculateurAbattement(double taux, int min, int max) {
        this.taux = taux;
        this.min = min;
        this.max = max;
    }

    /**
     * Calcule l'abattement pour un revenu net donné.
     *
     * @param revenuNet revenu net annuel du foyer fiscal
     * @return montant de l'abattement plafonné et planché
     */
    public int calculer(int revenuNet) {
        double abattement = revenuNet * taux;
        if (abattement > max) {
            abattement = max;
        }
        if (abattement < min) {
            abattement = min;
        }
        return (int) abattement;
    }
}
