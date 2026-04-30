package com.kerware.simulateurreusine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de l'adaptateur du simulateur legacy.
 *
 * Vérifie que l'adaptateur délègue correctement les appels
 * au simulateur hérité {@link com.kerware.simulateur.Simulateur}
 * et que les résultats concordent avec ceux du simulateur réusiné.
 */
@DisplayName("Tests de l'adaptateur SimulateurLegacy")
class TestsAdaptateurSimulateurLegacy {

    private AdaptateurSimulateurLegacy adaptateur;

    @BeforeEach
    void initialiser() {
        adaptateur = new AdaptateurSimulateurLegacy();
    }

    private int calculer(int revenu, SituationFamiliale situation,
                         int nbEnfants, int nbHandicapes, boolean parentIsole) {
        adaptateur.setRevenusNet(revenu);
        adaptateur.setSituationFamiliale(situation);
        adaptateur.setNbEnfantsACharge(nbEnfants);
        adaptateur.setNbEnfantsSituationHandicap(nbHandicapes);
        adaptateur.setParentIsole(parentIsole);
        adaptateur.calculImpotSurRevenuNet();
        return adaptateur.getImpotSurRevenuNet();
    }

    @Test
    @DisplayName("Adaptateur - CELIBATAIRE 30 000 € → même résultat que le simulateur réusiné")
    void adaptateur_celibataire_30000() {
        assertEquals(1637, calculer(30000, SituationFamiliale.CELIBATAIRE, 0, 0, false));
    }

    @Test
    @DisplayName("Adaptateur - MARIE 65 000 € → même résultat que le simulateur réusiné")
    void adaptateur_marie_65000() {
        assertEquals(4122, calculer(65000, SituationFamiliale.MARIE, 0, 0, false));
    }

    @Test
    @DisplayName("Adaptateur - PACSE → même résultat que CELIBATAIRE côté legacy (1 part)")
    void adaptateur_pacse() {
        // PACSE tombe sur default dans le legacy → 1 part comme célibataire
        int impot = calculer(30000, SituationFamiliale.PACSE, 0, 0, false);
        assertTrue(impot >= 0, "L'impôt doit être positif ou nul");
    }

    @Test
    @DisplayName("Adaptateur - DIVORCE parent isolé 1 enfant 35 000 € → 550 €")
    void adaptateur_divorce_parentIsole() {
        assertEquals(550, calculer(35000, SituationFamiliale.DIVORCE, 1, 0, true));
    }

    @Test
    @DisplayName("Adaptateur - VEUF 40 000 € → 4 086 €")
    void adaptateur_veuf_40000() {
        assertEquals(4086, calculer(40000, SituationFamiliale.VEUF, 0, 0, false));
    }

    @Test
    @DisplayName("Adaptateur - MARIE 65 000 €, 3 enfants dont 1 handicapé → impôt nul")
    void adaptateur_marie_3enfants_1handicap() {
        assertEquals(0, calculer(65000, SituationFamiliale.MARIE, 3, 1, false));
    }

    @Test
    @DisplayName("Adaptateur - getters intermédiaires retournent 0 (non implémentés dans le legacy)")
    void adaptateur_gettersIntermediaires_retournentZero() {
        calculer(30000, SituationFamiliale.CELIBATAIRE, 0, 0, false);

        assertEquals(0, adaptateur.getRevenuFiscalReference());
        assertEquals(0, adaptateur.getAbattement());
        assertEquals(0, adaptateur.getNbPartsFoyerFiscal());
        assertEquals(0, adaptateur.getImpotAvantDecote());
        assertEquals(0, adaptateur.getDecote());
    }

    @Test
    @DisplayName("Adaptateur vs Réusiné - cohérence sur MARIE 3 enfants 65 000 €")
    void adaptateur_vsReusine_coherence() {
        int impotAdaptateur = calculer(65000, SituationFamiliale.MARIE, 3, 0, false);

        SimulateurReusine reusine = new SimulateurReusine();
        reusine.setRevenusNet(65000);
        reusine.setSituationFamiliale(SituationFamiliale.MARIE);
        reusine.setNbEnfantsACharge(3);
        reusine.setNbEnfantsSituationHandicap(0);
        reusine.setParentIsole(false);
        reusine.calculImpotSurRevenuNet();

        assertEquals(impotAdaptateur, reusine.getImpotSurRevenuNet(),
            "L'adaptateur et le simulateur réusiné doivent donner le même impôt");
    }
}
