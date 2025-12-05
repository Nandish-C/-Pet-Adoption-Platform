CREATE TABLE predefined_queries (
    id VARCHAR(255) PRIMARY KEY,
    category VARCHAR(100) NOT NULL,
    query_text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Insert some sample predefined queries
INSERT INTO predefined_queries (id, category, query_text) VALUES
('1', 'General', 'How do I adopt a pet?'),
('2', 'General', 'What are the adoption requirements?'),
('3', 'General', 'How long does the adoption process take?'),
('4', 'Payment', 'What payment methods do you accept?'),
('5', 'Payment', 'Can I get a refund if I change my mind?'),
('6', 'Shelter', 'How can I volunteer at the shelter?'),
('7', 'Shelter', 'What services does the shelter provide?'),
('8', 'Pet Care', 'Do you provide pet care advice?'),
('9', 'Pet Care', 'What should I do if my adopted pet is sick?'),
('10', 'Other', 'I have a different question');
