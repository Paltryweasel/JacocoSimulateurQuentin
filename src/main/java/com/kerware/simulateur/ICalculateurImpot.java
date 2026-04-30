package com.kerware.simulateur;

/**
 * Interface définissant le contrat d'un calculateur d'impôt sur le revenu.
 */
public interface ICalculateurImpot {

    void setRevenusNet(int rn);
    void setSituationFamiliale(SituationFamiliale sf);
    void setNbEnfantsACharge(int nbe);
    void setNbEnfantsSituationHandicap(int nbesh);
    void setParentIsole(boolean pi);

    void calculImpotSurRevenuNet();

    int getRevenuFiscalReference();
    int getAbattement();
    int getNbPartsFoyerFiscal();
    int getImpotAvantDecote();
    int getDecote();
    int getImpotSurRevenuNet();
}
