package com.kerware.simulateurreusine;

import com.kerware.simulateur.Simulateur;

/**
 * Adaptateur qui branche le simulateur legacy {@link Simulateur}
 * sur l'interface {@link ICalculateurImpot}.
 *
 * Permet d'exécuter les tests fonctionnels contre le code hérité
 * afin de valider que le simulateur réusiné produit les mêmes résultats.
 */
public class AdaptateurSimulateurLegacy implements ICalculateurImpot {

    private final Simulateur simulateurLegacy;

    private int revenuNet;
    private com.kerware.simulateur.SituationFamiliale situationFamiliale;
    private int nbEnfants;
    private int nbEnfantsHandicapes;
    private boolean parentIsole;

    private int impotCalcule;

    /**
     * Construit l'adaptateur en instanciant le simulateur legacy.
     */
    public AdaptateurSimulateurLegacy() {
        this.simulateurLegacy = new Simulateur();
    }

    @Override
    public void setRevenusNet(int rn) {
        this.revenuNet = rn;
    }

    @Override
    public void setSituationFamiliale(SituationFamiliale sf) {
        this.situationFamiliale = convertirSituation(sf);
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

    @Override
    public void calculImpotSurRevenuNet() {
        impotCalcule = (int) simulateurLegacy.calculImpot(
            revenuNet, situationFamiliale, nbEnfants, nbEnfantsHandicapes, parentIsole
        );
    }

    @Override
    public int getRevenuFiscalReference() {
        return 0;
    }

    @Override
    public int getAbattement() {
        return 0;
    }

    @Override
    public int getNbPartsFoyerFiscal() {
        return 0;
    }

    @Override
    public int getImpotAvantDecote() {
        return 0;
    }

    @Override
    public int getDecote() {
        return 0;
    }

    @Override
    public int getImpotSurRevenuNet() {
        return impotCalcule;
    }

    /**
     * Convertit une situation familiale du package réusiné vers le package legacy.
     *
     * @param sf situation familiale du package réusiné
     * @return situation familiale du package legacy
     */
    private com.kerware.simulateur.SituationFamiliale convertirSituation(
            SituationFamiliale sf) {
        switch (sf) {
            case MARIE:
                return com.kerware.simulateur.SituationFamiliale.MARIE;
            case PACSE:
                return com.kerware.simulateur.SituationFamiliale.PACSE;
            case DIVORCE:
                return com.kerware.simulateur.SituationFamiliale.DIVORCE;
            case VEUF:
                return com.kerware.simulateur.SituationFamiliale.VEUF;
            case CELIBATAIRE:
            default:
                return com.kerware.simulateur.SituationFamiliale.CELIBATAIRE;
        }
    }
}
