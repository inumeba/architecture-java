export interface FlashInfoArticle {
  title: string;
  date: string;
  summary: string;
  imageUrl: string | null;
  articleHash: string;
}

export interface Subscriber {
  id: string;
  psid: string;
  name: string | null;
  active: boolean;
  subscribed_at: string;
}

export interface SentArticle {
  id: string;
  article_hash: string;
  title: string;
  image_url: string | null;
  published_at: string | null;
  sent_at: string;
}

export interface MessengerWebhookEntry {
  id: string;
  time: number;
  messaging: MessengerEvent[];
}

export interface MessengerEvent {
  sender: { id: string };
  recipient: { id: string };
  timestamp: number;
  message?: {
    mid: string;
    text: string;
  };
  postback?: {
    title: string;
    payload: string;
  };
}
