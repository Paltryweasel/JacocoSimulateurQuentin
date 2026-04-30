# Simulateur d'Impôt sur le Revenu 2024 — Réusiné

Mini-projet Jacoco — Réusinage d'un code legacy avec tests fonctionnels, couverture JaCoCo et analyse statique CheckStyle.

---

## Objectif

Réusiner le simulateur d'impôt sur le revenu 2024 (revenus 2023) à partir d'un code legacy de mauvaise qualité, en appliquant les bonnes pratiques de développement :

- Lisibilité avec des concepts métier
- Pas de nombres magiques (constantes nommées)
- Responsabilités éclatées en plusieurs classes
- Méthodes courtes et commentées
- Tests fonctionnels avec filet de sécurité

---

## Structure du projet

```
src/
├── main/java/
│   ├── com.kerware.simulateur/              # Code legacy (conservé intact)
│   │   ├── Simulateur.java
│   │   ├── ICalculateurImpot.java
│   │   └── SituationFamiliale.java
│   │
│   └── com.kerware.simulateurreusine/       # Code réusiné
│       ├── ParametresFiscaux2024.java        # Constantes du barème 2024
│       ├── CalculateurAbattement.java        # Abattement 10% (min/max)
│       ├── CalculateurNombreParts.java       # Quotient familial
│       ├── CalculateurImpotBrut.java         # Barème progressif
│       ├── PlafonnementQuotientFamilial.java # Plafond demi-part
│       ├── CalculateurDecote.java            # Décote seul/couple
│       ├── SimulateurReusine.java            # Orchestrateur principal
│       ├── AdaptateurSimulateurLegacy.java   # Adaptateur legacy → interface
│       ├── ICalculateurImpot.java            # Interface
│       └── SituationFamiliale.java          # Enum situations familiales
│
└── test/java/
    └── com.kerware.simulateurreusine/
        ├── TestsSimulateurReusine.java        # 44 tests fonctionnels
        └── TestsAdaptateurSimulateurLegacy.java # 8 tests adaptateur
```

---

## Résultats

| Critère | Résultat |
|---|---|
| Tests fonctionnels | ✅ 52/52 passent (100%) |
| Couverture JaCoCo (lignes) | ✅ 100% — exigence ≥ 90% |
| Violations CheckStyle (`but-unicaen.xml`) | ✅ 0 violation |

---

## Lancer le projet

### Prérequis
- Java 17+
- Maven 3.8+

### Compiler et tester
```bash
mvn test
```

### Vérification complète (tests + JaCoCo + CheckStyle)
```bash
mvn verify
```

### Générer les rapports HTML
```bash
mvn site
```

Les rapports sont ensuite disponibles dans `target/site/` :
- **JaCoCo** : `target/site/jacoco/index.html`
- **CheckStyle** : `target/site/checkstyle.html`

---

## Règles fiscales appliquées (2024 — revenus 2023)

### Barème progressif
| Tranche | Taux |
|---|---|
| 0 € – 11 294 € | 0 % |
| 11 294 € – 28 797 € | 11 % |
| 28 797 € – 82 341 € | 30 % |
| 82 341 € – 177 106 € | 41 % |
| Au-delà de 177 106 € | 45 % |

### Abattement forfaitaire
- Taux : 10 % du revenu net
- Plancher : 495 €
- Plafond : 14 171 €

### Quotient familial
- Célibataire / Divorcé / Veuf : 1 part
- Marié / Pacsé : 2 parts
- Enfants (1er et 2e) : +0,5 part chacun
- Enfants (à partir du 3e) : +1 part chacun
- Parent isolé avec enfants : +0,5 part
- Enfant handicapé : +0,5 part supplémentaire
- Plafond de l'avantage par demi-part : 1 759 €

### Décote
- Déclarant seul : si impôt < 1 929 € → décote = 873 € − (impôt × 0,4525)
- Couple : si impôt < 3 191 € → décote = 1 444 € − (impôt × 0,4525)
