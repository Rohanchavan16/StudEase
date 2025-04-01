CREATE DATABASE student_db;
USE student_db;

CREATE TABLE students (
    id INT PRIMARY KEY UNIQUE KEY,
    name VARCHAR(100),
    year varchar(100),
    department VARCHAR(100),
    contactno BIGINT
);

select * from students;
DROP table students;
ALTER TABLE students MODIFY contactno BIGINT;

