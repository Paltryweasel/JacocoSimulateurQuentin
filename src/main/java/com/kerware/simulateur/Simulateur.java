package com.kerware.simulateur;

/**
 * Cette classe permet de simuler le calcul de l'impôt sur le revenu
 * en France pour l'année 2024 sur les revenus de l'année 2023 pour
 * des cas simples de contribuables célibataires, mariés, divorcés, veufs
 * ou pacsés avec ou sans enfants à charge ou enfants en situation de handicap
 * et parent isolé.
 *
 * EXEMPLE DE CODE DE TRES MAUVAISE QUALITE FAIT PAR UN DEBUTANT
 *
 * Pas de lisibilité, pas de commentaires, pas de tests
 * Pas de documentation, pas de gestion des erreurs
 * Pas de logique métier, pas de modularité
 * Pas de gestion des exceptions, pas de gestion des logs
 * Principe "Single Responsability" non respecté
 * Pas de traçabilité vers les exigences métier
 *
 * Pourtant ce code fonctionne correctement
 * Il s'agit d'un "legacy" code qui est difficile à maintenir
 * L'auteur n'a pas fourni de tests unitaires
 **/
public class Simulateur {

    private int l00 = 0;
    private int l01 = 11294;
    private int l02 = 28797;
    private int l03 = 82341;
    private int l04 = 177106;
    private int l05 = Integer.MAX_VALUE;

    private int[] limites = new int[6];

    private double t00 = 0.0;
    private double t01 = 0.11;
    private double t02 = 0.3;
    private double t03 = 0.41;
    private double t04 = 0.45;

    private double[] taux = new double[5];

    private int lAbtMax = 14171;
    private int lAbtMin = 495;
    private double tAbt = 0.1;

    private double plafDemiPart = 1759;

    private double seuilDecoteDeclarantSeul = 1929;
    private double seuilDecoteDeclarantCouple = 3191;

    private double decoteMaxDeclarantSeul = 873;
    private double decoteMaxDeclarantCouple = 1444;
    private double tauxDecote = 0.4525;

    private int rNet = 0;
    private int nbEnf = 0;
    private int nbEnfH = 0;

    private double rFRef = 0;
    private double rImposable = 0;

    private double abt = 0;

    private double nbPtsDecl = 0;
    private double nbPts = 0;
    private double decote = 0;

    private double mImpDecl = 0;
    private double mImp = 0;

    private boolean parIso = false;

    /**
     * Calcule l'impôt sur le revenu net en France en 2024 sur les revenus 2023.
     *
     * @param revNet revenu net annuel
     * @param sitFam situation familiale du contribuable
     * @param nbEnfants nombre d'enfants à charge
     * @param nbEnfantsHandicapes nombre d'enfants en situation de handicap
     * @param parentIsol vrai si parent isolé
     * @return montant de l'impôt calculé
     */
    public long calculImpot(int revNet, SituationFamiliale sitFam,
                            int nbEnfants, int nbEnfantsHandicapes, boolean parentIsol) {

        rNet = revNet;
        nbEnf = nbEnfants;
        nbEnfH = nbEnfantsHandicapes;
        parIso = parentIsol;

        limites[0] = l00;
        limites[1] = l01;
        limites[2] = l02;
        limites[3] = l03;
        limites[4] = l04;
        limites[5] = l05;

        taux[0] = t00;
        taux[1] = t01;
        taux[2] = t02;
        taux[3] = t03;
        taux[4] = t04;

        abt = rNet * tAbt;
        if (abt > lAbtMax) {
            abt = lAbtMax;
        }
        if (abt < lAbtMin) {
            abt = lAbtMin;
        }

        rFRef = rNet - abt;

        switch (sitFam) {
            case CELIBATAIRE:
                nbPtsDecl = 1;
                break;
            case MARIE:
                nbPtsDecl = 2;
                break;
            case DIVORCE:
                nbPtsDecl = 1;
                break;
            case VEUF:
                if (nbEnf == 0) {
                    nbPtsDecl = 1;
                } else {
                    nbPtsDecl = 2;
                }
                nbPtsDecl = 1;
                break;
            default:
                nbPtsDecl = 1;
                break;
        }

        if (nbEnf <= 2) {
            nbPts = nbPtsDecl + nbEnf * 0.5;
        } else if (nbEnf > 2) {
            nbPts = nbPtsDecl + 1.0 + (nbEnf - 2);
        }

        if (parIso) {
            if (nbEnf > 0) {
                nbPts = nbPts + 0.5;
            }
        }

        nbPts = nbPts + nbEnfH * 0.5;

        rImposable = rFRef / nbPtsDecl;
        mImpDecl = 0;

        int i = 0;
        do {
            if (rImposable >= limites[i] && rImposable < limites[i + 1]) {
                mImpDecl += (rImposable - limites[i]) * taux[i];
                break;
            } else {
                mImpDecl += (limites[i + 1] - limites[i]) * taux[i];
            }
            i++;
        } while (i < 5);

        mImpDecl = mImpDecl * nbPtsDecl;
        mImpDecl = Math.round(mImpDecl);

        rImposable = rFRef / nbPts;
        mImp = 0;
        i = 0;

        do {
            if (rImposable >= limites[i] && rImposable < limites[i + 1]) {
                mImp += (rImposable - limites[i]) * taux[i];
                break;
            } else {
                mImp += (limites[i + 1] - limites[i]) * taux[i];
            }
            i++;
        } while (i < 5);

        mImp = mImp * nbPts;
        mImp = Math.round(mImp);

        double baisseImpot = mImpDecl - mImp;
        double ecartPts = nbPts - nbPtsDecl;
        double plafond = (ecartPts / 0.5) * plafDemiPart;

        if (baisseImpot >= plafond) {
            mImp = mImpDecl - plafond;
        }

        decote = 0;
        if (nbPtsDecl == 1) {
            if (mImp < seuilDecoteDeclarantSeul) {
                decote = decoteMaxDeclarantSeul - (mImp * tauxDecote);
            }
        }
        if (nbPtsDecl == 2) {
            if (mImp < seuilDecoteDeclarantCouple) {
                decote = decoteMaxDeclarantCouple - (mImp * tauxDecote);
            }
        }
        decote = Math.round(decote);
        if (mImp <= decote) {
            decote = mImp;
        }

        mImp = mImp - decote;

        return Math.round(mImp);
    }
}
