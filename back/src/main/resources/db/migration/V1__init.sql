CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       created_at DATETIME(6) NOT NULL,
                       email VARCHAR(100) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       updated_at DATETIME(6) NOT NULL,
                       username VARCHAR(50) NOT NULL,
                       PRIMARY KEY (id),
                       UNIQUE KEY UK6dotkott2kjsp8vw4d0m25fb7 (email),
                       UNIQUE KEY UKr43af9ap4edm43mmtq01oddj6 (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE themes (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        created_at DATETIME(6) NOT NULL,
                        description TEXT,
                        title VARCHAR(255) NOT NULL,
                        updated_at DATETIME(6) NOT NULL,
                        PRIMARY KEY (id),
                        UNIQUE KEY UK_n7ss7qtcaxh2qai55tig2orrc (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE posts (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       content TEXT NOT NULL,
                       created_at DATETIME(6) NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       updated_at DATETIME(6) NOT NULL,
                       author_id BIGINT NOT NULL,
                       theme_id BIGINT NOT NULL,
                       PRIMARY KEY (id),
                       KEY FK6xvn0811tkyo3nfjk2xvqx6ns (author_id),
                       KEY FKpvlwa732hkbn743dvsx81krrq (theme_id),
                       CONSTRAINT FK6xvn0811tkyo3nfjk2xvqx6ns FOREIGN KEY (author_id) REFERENCES users (id),
                       CONSTRAINT FKpvlwa732hkbn743dvsx81krrq FOREIGN KEY (theme_id) REFERENCES themes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE comments (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          content TEXT NOT NULL,
                          created_at DATETIME(6) NOT NULL,
                          author_id BIGINT NOT NULL,
                          post_id BIGINT NOT NULL,
                          PRIMARY KEY (id),
                          KEY FKn2na60ukhs76ibtpt9burkm27 (author_id),
                          KEY FKh4c7lvsc298whoyd4w9ta25cr (post_id),
                          CONSTRAINT FKn2na60ukhs76ibtpt9burkm27 FOREIGN KEY (author_id) REFERENCES users (id),
                          CONSTRAINT FKh4c7lvsc298whoyd4w9ta25cr FOREIGN KEY (post_id) REFERENCES posts (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE user_theme_subscriptions (
                                          user_id BIGINT NOT NULL,
                                          theme_id BIGINT NOT NULL,
                                          PRIMARY KEY (user_id, theme_id),
                                          KEY FKge9lxngeepnofc4raf41ivwgi (theme_id),
                                          CONSTRAINT FKd8r57t5way96iy4wv0x7x671v FOREIGN KEY (user_id) REFERENCES users (id),
                                          CONSTRAINT FKge9lxngeepnofc4raf41ivwgi FOREIGN KEY (theme_id) REFERENCES themes (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;