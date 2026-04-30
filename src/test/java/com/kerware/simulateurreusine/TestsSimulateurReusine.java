package com.kerware.simulateurreusine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fonctionnels du simulateur d'impôt sur le revenu 2024.
 *
 * Chaque test vérifie qu'un scénario métier précis produit le résultat attendu.
 * Les valeurs attendues ont été calculées à partir du simulateur de référence
 * (code legacy validé) et des règles fiscales 2024 (revenus 2023).
 *
 * Couverture ciblée : ≥ 90 % des lignes du simulateur réusiné.
 */
@DisplayName("Tests fonctionnels - Simulateur d'impôt 2024")
class TestsSimulateurReusine {

    private SimulateurReusine simulateur;

    @BeforeEach
    void initialiser() {
        simulateur = new SimulateurReusine();
    }

    // =========================================================================
    // Méthode utilitaire pour configurer et calculer l'impôt
    // =========================================================================

    private int calculer(int revenu, SituationFamiliale situation,
                         int nbEnfants, int nbHandicapes, boolean parentIsole) {
        simulateur.setRevenusNet(revenu);
        simulateur.setSituationFamiliale(situation);
        simulateur.setNbEnfantsACharge(nbEnfants);
        simulateur.setNbEnfantsSituationHandicap(nbHandicapes);
        simulateur.setParentIsole(parentIsole);
        simulateur.calculImpotSurRevenuNet();
        return simulateur.getImpotSurRevenuNet();
    }

    // =========================================================================
    // REQ-01 : Célibataire sans enfants — barème progressif
    // =========================================================================

    @Test
    @DisplayName("REQ-01a : Célibataire, revenu nul → impôt nul (en dessous du seuil)")
    void celibataire_revenuNul_impotNul() {
        assertEquals(0, calculer(0, SituationFamiliale.CELIBATAIRE, 0, 0, false));
    }

    @Test
    @DisplayName("REQ-01b : Célibataire, revenu 10 000 € → en dessous du seuil d'imposition")
    void celibataire_revenu10000_nonImposable() {
        assertEquals(0, calculer(10000, SituationFamiliale.CELIBATAIRE, 0, 0, false));
    }

    @Test
    @DisplayName("REQ-01c : Célibataire, revenu 15 000 € → très faible imposition (décote totale)")
    void celibataire_revenu15000_decoteTotale() {
        assertEquals(0, calculer(15000, SituationFamiliale.CELIBATAIRE, 0, 0, false));
    }

    @Test
    @DisplayName("REQ-01d : Célibataire, revenu 30 000 € → impôt 1 637 €")
    void celibataire_revenu30000_impot1637() {
        assertEquals(1637, calculer(30000, SituationFamiliale.CELIBATAIRE, 0, 0, false));
    }

    @Test
    @DisplayName("REQ-01e : Célibataire, revenu 80 000 € → impôt 14 886 €")
    void celibataire_revenu80000_impot14886() {
        assertEquals(14886, calculer(80000, SituationFamiliale.CELIBATAIRE, 0, 0, false));
    }

    @Test
    @DisplayName("REQ-01f : Célibataire, revenu 200 000 € (tranche 45 %) → impôt 60 768 €")
    void celibataire_revenu200000_impot60768() {
        assertEquals(60768, calculer(200000, SituationFamiliale.CELIBATAIRE, 0, 0, false));
    }

    // =========================================================================
    // REQ-02 : Abattement forfaitaire 10 %
    // =========================================================================

    @Test
    @DisplayName("REQ-02a : Abattement plafonné à 14 171 € pour revenu élevé (150 000 €)")
    void abattement_plafond_revenuEleve() {
        // Abattement = min(150000*0.1=15000, 14171) = 14171
        simulateur.setRevenusNet(150000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        assertEquals(14171, simulateur.getAbattement());
        assertEquals(150000 - 14171, simulateur.getRevenuFiscalReference());
        assertEquals(39919, simulateur.getImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-02b : Abattement planché à 495 € pour revenu très faible (3 000 €)")
    void abattement_plancher_revenuFaible() {
        simulateur.setRevenusNet(3000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        assertEquals(495, simulateur.getAbattement());
        assertEquals(3000 - 495, simulateur.getRevenuFiscalReference());
    }

    @Test
    @DisplayName("REQ-02c : Abattement standard à 10 % dans la zone normale (30 000 €)")
    void abattement_tauxNormal() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        assertEquals(3000, simulateur.getAbattement());
        assertEquals(27000, simulateur.getRevenuFiscalReference());
    }

    // =========================================================================
    // REQ-03 : Couple marié / pacsé (2 parts déclarants)
    // =========================================================================

    @Test
    @DisplayName("REQ-03a : Marié sans enfants, revenu 65 000 € → impôt 4 122 €")
    void marie_sansEnfants_revenu65000() {
        assertEquals(4122, calculer(65000, SituationFamiliale.MARIE, 0, 0, false));
    }

    @Test
    @DisplayName("REQ-03b : Marié sans enfants, revenu 100 000 € → impôt 13 572 €")
    void marie_sansEnfants_revenu100000() {
        assertEquals(13572, calculer(100000, SituationFamiliale.MARIE, 0, 0, false));
    }

    @Test
    @DisplayName("REQ-03c : Pacsé est traité comme marié (2 parts déclarants)")
    void pacse_traitéCommeMarié() {
        int impotMarie = calculer(65000, SituationFamiliale.MARIE, 0, 0, false);
        int impotPacse = calculer(65000, SituationFamiliale.PACSE, 0, 0, false);
        assertEquals(impotMarie, impotPacse);
    }

    @Test
    @DisplayName("REQ-03d : Couple - décote s'applique sous le seuil couple (40 000 €)")
    void marie_decote_couple() {
        simulateur.setRevenusNet(40000);
        simulateur.setSituationFamiliale(SituationFamiliale.MARIE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        assertTrue(simulateur.getDecote() > 0, "La décote doit être positive pour un couple à revenu modeste");
        assertEquals(698, simulateur.getImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-03e : Couple revenu très faible (20 000 €) → décote totale, impôt nul")
    void marie_revenu20000_impotNul() {
        assertEquals(0, calculer(20000, SituationFamiliale.MARIE, 0, 0, false));
    }

    // =========================================================================
    // REQ-04 : Quotient familial — enfants à charge
    // =========================================================================

    @Test
    @DisplayName("REQ-04a : Marié, 3 enfants → impôt réduit par le quotient familial (685 €)")
    void marie_3enfants_impot685() {
        assertEquals(685, calculer(65000, SituationFamiliale.MARIE, 3, 0, false));
    }

    @Test
    @DisplayName("REQ-04b : Marié, 2 enfants, revenu faible → décote totale, impôt nul")
    void marie_2enfants_revenu30000_impotNul() {
        assertEquals(0, calculer(30000, SituationFamiliale.MARIE, 2, 0, false));
    }

    @Test
    @DisplayName("REQ-04c : Célibataire, 1 enfant → plafonnement QF appliqué (5 027 €)")
    void celibataire_1enfant_plafonnementQF() {
        assertEquals(5027, calculer(50000, SituationFamiliale.CELIBATAIRE, 1, 0, false));
    }

    @Test
    @DisplayName("REQ-04d : Célibataire, 2 enfants → plafonnement QF appliqué (3 268 €)")
    void celibataire_2enfants_plafonnementQF() {
        assertEquals(3268, calculer(50000, SituationFamiliale.CELIBATAIRE, 2, 0, false));
    }

    @Test
    @DisplayName("REQ-04e : Nombre de parts foyer (MARIE 3 enfants) = 8 demi-parts")
    void nbPartsFoyer_marie3enfants() {
        simulateur.setRevenusNet(65000);
        simulateur.setSituationFamiliale(SituationFamiliale.MARIE);
        simulateur.setNbEnfantsACharge(3);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        // 2 déclarants + 3 enfants (1+1+1) = 4 parts = 8 demi-parts
        assertEquals(8, simulateur.getNbPartsFoyerFiscal());
    }

    // =========================================================================
    // REQ-05 : Enfants en situation de handicap
    // =========================================================================

    @Test
    @DisplayName("REQ-05a : Marié, 3 enfants dont 1 handicapé → impôt nul (décote totale)")
    void marie_3enfants_1handicap_impotNul() {
        assertEquals(0, calculer(65000, SituationFamiliale.MARIE, 3, 1, false));
    }

    @Test
    @DisplayName("REQ-05b : Divorcé PI, 3 enfants dont 1 handicapé → impôt nul")
    void divorce_3enfants_1handicap_parentIsole_impotNul() {
        assertEquals(0, calculer(50000, SituationFamiliale.DIVORCE, 3, 1, true));
    }

    // =========================================================================
    // REQ-06 : Parent isolé
    // =========================================================================

    @Test
    @DisplayName("REQ-06a : Divorcé parent isolé, 1 enfant, 35 000 € → impôt 550 €")
    void divorce_parentIsole_1enfant() {
        assertEquals(550, calculer(35000, SituationFamiliale.DIVORCE, 1, 0, true));
    }

    @Test
    @DisplayName("REQ-06b : Divorcé parent isolé, 2 enfants, 35 000 € → décote totale")
    void divorce_parentIsole_2enfants_impotNul() {
        assertEquals(0, calculer(35000, SituationFamiliale.DIVORCE, 2, 0, true));
    }

    @Test
    @DisplayName("REQ-06c : Divorcé parent isolé, 3 enfants, 50 000 € → impôt 1 €")
    void divorce_parentIsole_3enfants_revenu50000() {
        assertEquals(1, calculer(50000, SituationFamiliale.DIVORCE, 3, 0, true));
    }

    // =========================================================================
    // REQ-07 : Veuf(ve)
    // =========================================================================

    @Test
    @DisplayName("REQ-07a : Veuf sans enfants → même traitement que célibataire (4 086 €)")
    void veuf_sansEnfants_revenu40000() {
        assertEquals(4086, calculer(40000, SituationFamiliale.VEUF, 0, 0, false));
    }

    @Test
    @DisplayName("REQ-07b : Veuf avec 2 enfants → quotient familial réduit l'impôt (1 269 €)")
    void veuf_2enfants_revenu40000() {
        assertEquals(1269, calculer(40000, SituationFamiliale.VEUF, 2, 0, false));
    }

    // =========================================================================
    // REQ-08 : Plafonnement du quotient familial
    // =========================================================================

    @Test
    @DisplayName("REQ-08a : Célibataire 120 000 €, 1 enfant → plafonnement QF actif (26 750 €)")
    void plafonnementQF_celibataire_revenuEleve() {
        simulateur.setRevenusNet(120000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(1);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        int impotAvantDecote = simulateur.getImpotAvantDecote();
        assertEquals(26750, impotAvantDecote);
        assertEquals(26750, simulateur.getImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-08b : Marié 200 000 €, 1 enfant → plafonnement QF actif (42 888 €)")
    void plafonnementQF_marie_revenuEleve() {
        assertEquals(42888, calculer(200000, SituationFamiliale.MARIE, 1, 0, false));
    }

    // =========================================================================
    // REQ-09 : Décote
    // =========================================================================

    @Test
    @DisplayName("REQ-09a : Célibataire avec décote partielle → décote > 0 et impôt réduit")
    void decote_partielle_celibataire() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        assertEquals(91, simulateur.getDecote());
        assertEquals(1637, simulateur.getImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-09b : Impôt avant décote est bien accessible")
    void impotAvantDecote_accessible() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        assertEquals(1728, simulateur.getImpotAvantDecote());
    }

    // =========================================================================
    // REQ-10 : Divorces et situations à 1 déclarant
    // =========================================================================

    @ParameterizedTest(name = "Divorcé, revenu {0}, {1} enfants PI={2} → impôt {3}")
    @CsvSource({
        "35000, 1, true,  550",
        "35000, 2, true,  0",
        "50000, 3, true,  1",
        "50000, 0, false, 6786"
    })
    @DisplayName("REQ-10 : Scénarios divorcé (paramétré)")
    void divorce_scenariosParametres(int revenu, int enfants,
                                     boolean parentIsole, int attendu) {
        assertEquals(attendu,
            calculer(revenu, SituationFamiliale.DIVORCE, enfants, 0, parentIsole));
    }

    // =========================================================================
    // REQ-11 : Validation des entrées
    // =========================================================================

    @Test
    @DisplayName("REQ-11a : Revenu négatif → exception IllegalArgumentException")
    void validation_revenuNegatif() {
        simulateur.setRevenusNet(-1);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        assertThrows(IllegalArgumentException.class,
            () -> simulateur.calculImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-11b : Nombre d'enfants négatif → exception")
    void validation_enfantsNegatif() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(-1);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        assertThrows(IllegalArgumentException.class,
            () -> simulateur.calculImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-11c : Enfants handicapés > enfants totaux → exception")
    void validation_handicapesSuperieurEnfants() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(1);
        simulateur.setNbEnfantsSituationHandicap(2);
        simulateur.setParentIsole(false);
        assertThrows(IllegalArgumentException.class,
            () -> simulateur.calculImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-11d : Situation familiale nulle → exception")
    void validation_situationNulle() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(null);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        assertThrows(IllegalArgumentException.class,
            () -> simulateur.calculImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-11e : Marié parent isolé → incohérence → exception")
    void validation_marieParentIsole() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(SituationFamiliale.MARIE);
        simulateur.setNbEnfantsACharge(1);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(true);
        assertThrows(IllegalArgumentException.class,
            () -> simulateur.calculImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-11f : Pacsé parent isolé → incohérence → exception")
    void validation_pacseParentIsole() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(SituationFamiliale.PACSE);
        simulateur.setNbEnfantsACharge(1);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(true);
        assertThrows(IllegalArgumentException.class,
            () -> simulateur.calculImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-11g : Enfants handicapés négatif → exception")
    void validation_handicapesNegatif() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(1);
        simulateur.setNbEnfantsSituationHandicap(-1);
        simulateur.setParentIsole(false);
        assertThrows(IllegalArgumentException.class,
            () -> simulateur.calculImpotSurRevenuNet());
    }

    // =========================================================================
    // REQ-12 : Cohérence globale des résultats intermédiaires
    // =========================================================================

    @Test
    @DisplayName("REQ-12a : Impôt final = impôt avant décote - décote")
    void coherence_impotFinal() {
        simulateur.setRevenusNet(30000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        int attendu = simulateur.getImpotAvantDecote() - simulateur.getDecote();
        assertEquals(attendu, simulateur.getImpotSurRevenuNet());
    }

    @Test
    @DisplayName("REQ-12b : Revenu fiscal de référence = revenu net - abattement")
    void coherence_rfr() {
        simulateur.setRevenusNet(50000);
        simulateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        simulateur.setNbEnfantsACharge(0);
        simulateur.setNbEnfantsSituationHandicap(0);
        simulateur.setParentIsole(false);
        simulateur.calculImpotSurRevenuNet();

        assertEquals(50000 - simulateur.getAbattement(),
            simulateur.getRevenuFiscalReference());
    }

    @Test
    @DisplayName("REQ-12c : L'impôt final ne peut pas être négatif")
    void impotFinal_nonNegatif() {
        assertEquals(0, calculer(5000, SituationFamiliale.CELIBATAIRE, 0, 0, false));
    }
}
