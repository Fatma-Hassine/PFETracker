INSERT INTO utilisateurs (
    email,
    mot_de_passe,
    nom_complet,
    must_change_password,
    account_locked,
    failed_login_attempts,
    role,
    enabled,
    type_utilisateur
)
SELECT
    'admin@enicar.ucar.tn',
    '$2a$12$TwAExqFMWKkBaKJB3V2lMOoypS0LRcGQMFxNz4Nj5V5oIgjWdlxHe',
    'Administrateur Système',
    false,
    false,
    0,
    'ROLE_ADMIN',
    true,
    'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM utilisateurs
    WHERE email = 'admin@enicar.ucar.tn'
);

INSERT INTO admins (
    utilisateur_id,
    droit_reinitialisation,
    gestion_tous_departements,
    affectation_forcee_globale
)
SELECT
    (SELECT id FROM utilisateurs WHERE email = 'admin@enicar.ucar.tn'),
    true,
    true,
    true
WHERE NOT EXISTS (
    SELECT 1 FROM admins
    WHERE utilisateur_id = (
        SELECT id FROM utilisateurs WHERE email = 'admin@enicar.ucar.tn'
    )
);