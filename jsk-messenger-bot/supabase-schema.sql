-- JSK Messenger Bot - Supabase Schema

-- Abonnés Messenger
CREATE TABLE subscribers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  psid TEXT NOT NULL UNIQUE,          -- Facebook Page-Scoped ID
  name TEXT,                          -- Nom du profil (optionnel)
  active BOOLEAN NOT NULL DEFAULT true,
  subscribed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_subscribers_active ON subscribers (active) WHERE active = true;

-- Articles déjà envoyés (dédoublonnage)
CREATE TABLE sent_articles (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  article_hash TEXT NOT NULL UNIQUE,  -- Hash du titre pour dédoublonnage
  title TEXT NOT NULL,
  image_url TEXT,
  published_at TEXT,                  -- Date affichée sur le site
  sent_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_sent_articles_hash ON sent_articles (article_hash);

-- Trigger pour updated_at
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER subscribers_updated_at
  BEFORE UPDATE ON subscribers
  FOR EACH ROW
  EXECUTE FUNCTION update_updated_at();
