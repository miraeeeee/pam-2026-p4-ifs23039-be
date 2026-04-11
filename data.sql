CREATE TABLE IF NOT EXISTS plants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(100) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    manfaat TEXT NOT NULL,
    efek_samping TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Tambahkan ke file data.sql yang sudah ada di BE (pam-2026-p4-ifs18005-be)
-- Tabel plants sudah ada, tambahkan tabel sdgs ini

CREATE TABLE IF NOT EXISTS sdgs (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nomor INTEGER NOT NULL UNIQUE,
    nama VARCHAR(100) NOT NULL,
    label VARCHAR(255) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
    );