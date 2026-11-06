-- MONETRA Default Categories Seed Migration V2
-- Default categories have user_id = NULL so they are accessible to all users

INSERT INTO categories (user_id, name, type, icon, color, is_default) VALUES
(NULL, 'Salary', 'INCOME', 'briefcase', '#10B981', TRUE),
(NULL, 'Freelancing', 'INCOME', 'laptop', '#06B6D4', TRUE),
(NULL, 'Food & Dining', 'EXPENSE', 'utensils', '#F59E0B', TRUE),
(NULL, 'Transportation', 'EXPENSE', 'car', '#3B82F6', TRUE),
(NULL, 'Shopping', 'EXPENSE', 'shopping-bag', '#EC4899', TRUE),
(NULL, 'Education', 'EXPENSE', 'book-open', '#8B5CF6', TRUE),
(NULL, 'Entertainment', 'EXPENSE', 'film', '#F43F5E', TRUE),
(NULL, 'Healthcare', 'EXPENSE', 'activity', '#EF4444', TRUE),
(NULL, 'Housing', 'EXPENSE', 'home', '#6366F1', TRUE),
(NULL, 'Utilities', 'EXPENSE', 'zap', '#EAB308', TRUE),
(NULL, 'Other', 'EXPENSE', 'more-horizontal', '#64748B', TRUE);

