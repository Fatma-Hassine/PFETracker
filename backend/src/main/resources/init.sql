PRAGMA foreign_keys = OFF;

INSERT OR IGNORE INTO users (id, email, first_name, last_name, role, department_id, is_active, password) VALUES
(1, 'student1@univ.tn',    'Ahmed',   'Ben Ali',   'STUDENT',      1, 1, '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
(2, 'student2@univ.tn',    'Sarra',   'Trabelsi',  'STUDENT',      1, 1, '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
(3, 'student3@univ.tn',    'Youssef', 'Mansouri',  'STUDENT',      2, 1, '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
(4, 'supervisor1@univ.tn', 'Mohamed', 'Chaabane',  'SUPERVISOR',   1, 1, '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
(5, 'supervisor2@univ.tn', 'Leila',   'Jebali',    'SUPERVISOR',   2, 1, '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
(6, 'manager1@univ.tn',    'Karim',   'Bouazizi',  'DEPT_MANAGER', 1, 1, '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
(7, 'director@univ.tn',    'Fatma',   'Essid',     'DIRECTOR',  NULL, 1, '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');

INSERT OR IGNORE INTO pfes (id, student_id, supervisor_id, title, status, global_progress, created_at) VALUES
(1, 1, 4, 'Système de gestion des PFE avec IA',       'IN_PROGRESS', 45.0, '2025-02-01 09:00:00'),
(2, 2, 4, 'Application mobile de suivi de stage',     'IN_PROGRESS', 30.0, '2025-02-01 09:00:00'),
(3, 3, 5, 'Plateforme e-learning adaptative',         'DELAYED',     15.0, '2025-02-01 09:00:00');

INSERT OR IGNORE INTO tasks (id, pfe_id, title, description, status, deadline, assigned_to, created_by, completion_percentage) VALUES
(1,  1, 'Rédaction du cahier des charges',   'Définir les besoins fonctionnels et non-fonctionnels', 'VALIDATED',   '2025-03-01 23:59:00', 1, 4, 100),
(2,  1, 'Conception de la base de données',  'Modélisation UML et schéma relationnel',               'VALIDATED',   '2025-03-15 23:59:00', 1, 4, 100),
(3,  1, 'Développement backend Spring Boot', 'Implémentation des endpoints REST',                    'IN_PROGRESS', '2025-05-01 23:59:00', 1, 4,  60),
(4,  1, 'Développement frontend Angular',    'Interface utilisateur responsive',                     'NOT_STARTED', '2025-06-01 23:59:00', 1, 4,   0),
(5,  1, 'Tests et recette',                  'Tests unitaires et tests d''intégration',              'NOT_STARTED', '2025-06-20 23:59:00', 1, 4,   0),
(6,  2, 'Analyse des besoins',               'Recueil des exigences utilisateurs',                   'VALIDATED',   '2025-03-10 23:59:00', 2, 4, 100),
(7,  2, 'Prototype UI/UX',                   'Maquettes Figma des écrans principaux',                'SUBMITTED',   '2025-04-01 23:59:00', 2, 4,  85),
(8,  2, 'Développement API REST',            'Backend Node.js + Express',                            'NOT_STARTED', '2025-05-15 23:59:00', 2, 4,   0),
(9,  3, 'Étude bibliographique',             'Revue de littérature sur le e-learning adaptatif',     'IN_PROGRESS', '2025-03-20 23:59:00', 3, 5,  20),
(10, 3, 'Architecture système',              'Définir l''architecture microservices',                'NOT_STARTED', '2025-04-20 23:59:00', 3, 5,   0);

INSERT OR IGNORE INTO meetings (id, title, description, meeting_date, duration, created_by, participant_id, pfe_id, status, meeting_link, meet_provider, is_online, reminder_sent_24h, reminder_sent_15min) VALUES
(1, 'Point d''avancement S7',    'Revue hebdomadaire PFE 1',      '2025-04-10 10:00:00', 60, 4, 1, 1, 'COMPLETED', 'https://meet.google.com/abc-defg-hij', 'GOOGLE_MEET', 1, 1, 1),
(2, 'Correction cahier charges', 'Retours sur les corrections',   '2025-04-17 14:00:00', 45, 4, 1, 1, 'COMPLETED', 'https://meet.google.com/klm-nopq-rst', 'GOOGLE_MEET', 1, 1, 1),
(3, 'Suivi backend',             'Demo des endpoints API',        '2025-05-15 11:00:00', 60, 4, 1, 1, 'ACCEPTED',  'https://meet.google.com/uvw-xyza-bcd', 'GOOGLE_MEET', 1, 0, 0),
(4, 'Revue prototype Sarra',     'Validation maquettes Figma',    '2025-05-20 15:00:00', 30, 4, 2, 2, 'PENDING',   'https://meet.google.com/efg-hijk-lmn', 'GOOGLE_MEET', 1, 0, 0),
(5, 'Rattrapage planning',       'Mise à jour planning Youssef',  '2025-05-22 09:00:00', 45, 5, 3, 3, 'PENDING',   'https://meet.google.com/opq-rstu-vwx', 'GOOGLE_MEET', 1, 0, 0);

INSERT OR IGNORE INTO notifications (id, user_id, message, type, is_read, created_at, sent_by_email) VALUES
(1, 1, 'Nouvelle réunion planifiée : Point d''avancement S7',  'MEETING', 1, '2025-04-08 10:00:00', 0),
(2, 1, 'Tâche validée : Rédaction du cahier des charges',      'TASK',    1, '2025-03-05 14:30:00', 0),
(3, 1, 'Corrections demandées sur la tâche : Prototype UI/UX', 'TASK',    0, '2025-04-12 09:15:00', 0),
(4, 2, 'Invitation réunion : Revue prototype Sarra',           'MEETING', 0, '2025-04-18 11:00:00', 0),
(5, 4, 'Tâche soumise pour révision : Prototype UI/UX',        'TASK',    0, '2025-04-11 16:00:00', 0);

INSERT OR IGNORE INTO comments (id, task_id, user_id, content, created_at) VALUES
(1, 3, 4, 'Bien avancé sur les endpoints messages. N''oublie pas les tests unitaires.',   '2025-04-20 10:30:00'),
(2, 3, 1, 'Merci pour le retour, je vais ajouter les tests cette semaine.',               '2025-04-20 11:00:00'),
(3, 7, 4, 'Le prototype est bien mais l''UX du formulaire de connexion manque de clarté.','2025-04-12 09:15:00');

INSERT OR IGNORE INTO user_notification_preferences
  (user_id, email_enabled, in_app_enabled, websocket_enabled,
   message_notifications, meeting_notifications, task_notifications,
   comment_notifications, alert_notifications,
   email_on_message, email_on_meeting_reminder, email_on_task_update,
   email_on_comment_mention, daily_digest_enabled, digest_send_time,
   created_at)
VALUES
(1, 1,1,1, 1,1,1,1,1, 0,1,1,1, 0,'08:00', '2025-02-01 09:00:00'),
(2, 1,1,1, 1,1,1,1,1, 0,1,1,1, 0,'08:00', '2025-02-01 09:00:00'),
(3, 1,1,1, 1,1,1,1,1, 0,1,1,1, 0,'08:00', '2025-02-01 09:00:00'),
(4, 1,1,1, 1,1,1,1,1, 0,1,1,1, 0,'08:00', '2025-02-01 09:00:00'),
(5, 1,1,1, 1,1,1,1,1, 0,1,1,1, 0,'08:00', '2025-02-01 09:00:00'),
(6, 1,1,1, 1,1,1,1,1, 1,1,1,1, 1,'08:00', '2025-02-01 09:00:00'),
(7, 1,1,1, 1,1,1,1,1, 1,1,1,1, 1,'08:00', '2025-02-01 09:00:00');

PRAGMA foreign_keys = ON;

UPDATE users 
SET password = '$2a$12$TjjOxKQOEFAaV6VsCvhvK.MWMEB/R7flFM42q.gkNgDNYvnps1tSa';