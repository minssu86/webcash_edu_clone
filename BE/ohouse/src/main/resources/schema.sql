DROP TABLE IF EXISTS test_table;
CREATE TABLE `test_table` (
                          test_seq bigint NOT NULL AUTO_INCREMENT,
                          test_id varchar(255),
                          PRIMARY KEY (test_seq)
);
DROP TABLE IF EXISTS user;
CREATE TABLE IF NOT EXISTS user (
    user_id int PRIMARY KEY AUTO_INCREMENT,
    user_pw varchar(64) NOT NULL,
    user_nickname varchar(10) UNIQUE NOT NULL,
    user_email varchar(50) UNIQUE NOT NULL,
    user_social_kakao varchar(50) UNIQUE NULL,
    user_social_naver varchar(50) UNIQUE NULL
);