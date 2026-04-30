package com.kerware.simulateurreusine;

/**
 * Simulateur d'impôt sur le revenu réusiné pour l'année 2024
 * (revenus perçus en 2023).
 *
 * Cette classe orchestre les différentes étapes du calcul en déléguant
 * chaque responsabilité à un composant spécialisé :
 * <ul>
 *   <li>{@link CalculateurAbattement} : abattement forfaitaire</li>
 *   <li>{@link CalculateurNombreParts} : quotient familial</li>
 *   <li>{@link CalculateurImpotBrut} : barème progressif</li>
 *   <li>{@link PlafonnementQuotientFamilial} : plafonnement des demi-parts</li>
 *   <li>{@link CalculateurDecote} : décote pour les faibles revenus</li>
 * </ul>
 *
 * <p>Implémente {@link ICalculateurImpot} : les paramètres du foyer sont
 * injectés via les setters, puis le calcul est déclenché par
 * {@link #calculImpotSurRevenuNet()}.</p>
 */
public class SimulateurReusine implements ICalculateurImpot {

    // Composants de calcul
    private final CalculateurAbattement calcAbattement;
    private final CalculateurNombreParts calcParts;
    private final CalculateurImpotBrut calcImpotBrut;
    private final PlafonnementQuotientFamilial plafonnement;
    private final CalculateurDecote calcDecote;

    // Paramètres du foyer fiscal
    private int revenuNet;
    private SituationFamiliale situationFamiliale;
    private int nbEnfants;
    private int nbEnfantsHandicapes;
    private boolean parentIsole;

    // Résultats intermédiaires et finaux
    private int abattement;
    private int revenuFiscalReference;
    private double partsDeclarants;
    private double partsTotales;
    private long impotAvantDecote;
    private long decote;
    private long impotFinal;

    /**
     * Construit un simulateur initialisé avec les paramètres fiscaux 2024.
     */
    public SimulateurReusine() {
        this.calcAbattement = new CalculateurAbattement(
            ParametresFiscaux2024.TAUX_ABATTEMENT,
            ParametresFiscaux2024.ABATTEMENT_MIN,
            ParametresFiscaux2024.ABATTEMENT_MAX
        );
        this.calcParts = new CalculateurNombreParts();
        this.calcImpotBrut = new CalculateurImpotBrut(
            ParametresFiscaux2024.LIMITES_TRANCHES,
            ParametresFiscaux2024.TAUX_TRANCHES
        );
        this.plafonnement = new PlafonnementQuotientFamilial(
            ParametresFiscaux2024.PLAFOND_AVANTAGE_DEMI_PART
        );
        this.calcDecote = new CalculateurDecote(
            ParametresFiscaux2024.SEUIL_DECOTE_SEUL,
            ParametresFiscaux2024.SEUIL_DECOTE_COUPLE,
            ParametresFiscaux2024.DECOTE_MAX_SEUL,
            ParametresFiscaux2024.DECOTE_MAX_COUPLE,
            ParametresFiscaux2024.TAUX_DECOTE
        );
    }

    @Override
    public void setRevenusNet(int rn) {
        this.revenuNet = rn;
    }

    @Override
    public void setSituationFamiliale(SituationFamiliale sf) {
        this.situationFamiliale = sf;
    }

    @Override
    public void setNbEnfantsACharge(int nbe) {
        this.nbEnfants = nbe;
    }

    @Override
    public void setNbEnfantsSituationHandicap(int nbesh) {
        this.nbEnfantsHandicapes = nbesh;
    }

    @Override
    public void setParentIsole(boolean pi) {
        this.parentIsole = pi;
    }

    /**
     * Exécute le calcul complet de l'impôt sur le revenu net.
     *
     * <p>Étapes du calcul :</p>
     * <ol>
     *   <li>Validation des entrées</li>
     *   <li>Calcul de l'abattement et du revenu fiscal de référence</li>
     *   <li>Calcul du nombre de parts (déclarants + famille)</li>
     *   <li>Calcul de l'impôt brut (déclarants seuls, puis foyer complet)</li>
     *   <li>Application du plafonnement du quotient familial</li>
     *   <li>Application de la décote</li>
     * </ol>
     */
    @Override
    public void calculImpotSurRevenuNet() {
        validerEntrees();

        abattement = calcAbattement.calculer(revenuNet);
        revenuFiscalReference = revenuNet - abattement;

        partsDeclarants = calcParts.calculerPartsDeclarants(situationFamiliale);
        partsTotales = calcParts.calculerPartsTotales(
            partsDeclarants, nbEnfants, nbEnfantsHandicapes, parentIsole);

        long impotDeclarants = calcImpotBrut.calculer(revenuFiscalReference, partsDeclarants);
        long impotFoyer = calcImpotBrut.calculer(revenuFiscalReference, partsTotales);

        impotAvantDecote = Math.round(
            plafonnement.appliquer(impotDeclarants, impotFoyer, partsDeclarants, partsTotales)
        );

        decote = calcDecote.calculer(impotAvantDecote, partsDeclarants);
        impotFinal = impotAvantDecote - decote;
    }

    /**
     * Vérifie la cohérence des paramètres du foyer fiscal.
     *
     * @throws IllegalArgumentException si un paramètre est invalide
     */
    private void validerEntrees() {
        if (revenuNet < 0) {
            throw new IllegalArgumentException("Le revenu net ne peut pas être négatif.");
        }
        if (nbEnfants < 0) {
            throw new IllegalArgumentException(
                "Le nombre d'enfants ne peut pas être négatif.");
        }
        if (nbEnfantsHandicapes < 0) {
            throw new IllegalArgumentException(
                "Le nombre d'enfants handicapés ne peut pas être négatif.");
        }
        if (nbEnfantsHandicapes > nbEnfants) {
            throw new IllegalArgumentException(
                "Le nombre d'enfants handicapés ne peut pas dépasser le nombre d'enfants.");
        }
        if (situationFamiliale == null) {
            throw new IllegalArgumentException("La situation familiale doit être renseignée.");
        }
        if (parentIsole && situationFamiliale == SituationFamiliale.MARIE) {
            throw new IllegalArgumentException(
                "Un contribuable marié ne peut pas être parent isolé.");
        }
        if (parentIsole && situationFamiliale == SituationFamiliale.PACSE) {
            throw new IllegalArgumentException(
                "Un contribuable pacsé ne peut pas être parent isolé.");
        }
    }

    @Override
    public int getRevenuFiscalReference() {
        return revenuFiscalReference;
    }

    @Override
    public int getAbattement() {
        return abattement;
    }

    @Override
    public int getNbPartsFoyerFiscal() {
        return (int) Math.round(partsTotales * 2);
    }

    @Override
    public int getImpotAvantDecote() {
        return (int) impotAvantDecote;
    }

    @Override
    public int getDecote() {
        return (int) decote;
    }

    @Override
    public int getImpotSurRevenuNet() {
        return (int) impotFinal;
    }
}
