-- Sequence 管理表
DROP TABLE IF EXISTS sequence;
CREATE TABLE sequence (
         name VARCHAR(50) NOT NULL,
         current_value BIGINT NOT NULL DEFAULT 1,
         increment BIGINT NOT NULL DEFAULT 1,
         PRIMARY KEY (name)
) ENGINE=InnoDB;
 
-- 取当前值的函数
DROP FUNCTION IF EXISTS currval;
DELIMITER $
CREATE FUNCTION currval (seq_name VARCHAR(50))
         RETURNS INTEGER
         LANGUAGE SQL
         DETERMINISTIC
         CONTAINS SQL
         SQL SECURITY DEFINER
         COMMENT ''
BEGIN
         DECLARE value INTEGER;
         SET value = 0;
         SELECT current_value INTO value
                   FROM sequence
                   WHERE name = seq_name;               
         RETURN value;
END
$
DELIMITER ;
 
-- 取下一个值的函数
DROP FUNCTION IF EXISTS nextval;
DELIMITER $
CREATE FUNCTION nextval (seq_name VARCHAR(50))
         RETURNS INTEGER
         LANGUAGE SQL
         DETERMINISTIC
         CONTAINS SQL
         SQL SECURITY DEFINER
         COMMENT ''
BEGIN
         UPDATE sequence
                   SET current_value = current_value + increment
                   WHERE name = seq_name;
         commit;
         RETURN currval(seq_name);
END
$
DELIMITER ;
 
-- 更新当前值的函数
DROP FUNCTION IF EXISTS setval;
DELIMITER $
CREATE FUNCTION setval (seq_name VARCHAR(50), value INTEGER)
         RETURNS INTEGER
         LANGUAGE SQL
         DETERMINISTIC
         CONTAINS SQL
         SQL SECURITY DEFINER
         COMMENT ''
BEGIN
         UPDATE sequence
                   SET current_value = value
                   WHERE name = seq_name;
         commit;
         RETURN currval(seq_name);
END
$
DELIMITER ;
 
-- 测试
-- INSERT INTO sequence VALUES ('TestSeq', 0, 1);
-- SELECT SETVAL('TestSeq', 10);
-- SELECT CURRVAL('TestSeq');
-- SELECT NEXTVAL('TestSeq');
