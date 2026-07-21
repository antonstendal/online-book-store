DELETE FROM books_categories;
DELETE FROM books;
DELETE FROM categories;

INSERT INTO categories (id, name, description, is_deleted)
VALUES
(1, 'Programming', 'Books about programming and software development', 0),
(2, 'Fantasy', 'Fantasy books', false),
(3, 'Science', 'Science books', false);
