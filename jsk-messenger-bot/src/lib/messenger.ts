import axios from "axios";
import crypto from "crypto";
import { getSupabase } from "./supabase";
import type { FlashInfoArticle } from "./types";

const PAGE_ACCESS_TOKEN = process.env.FACEBOOK_PAGE_ACCESS_TOKEN!;
const GRAPH_API = "https://graph.facebook.com/v19.0/me/messages";

// ---------- Signature validation ----------

export function verifyRequestSignature(
  rawBody: Buffer,
  signature: string | null
): boolean {
  if (!signature) return false;
  const appSecret = process.env.FACEBOOK_APP_SECRET!;
  const [algo, hash] = signature.split("=");
  const expected = crypto
    .createHmac(algo, appSecret)
    .update(rawBody)
    .digest("hex");
  return crypto.timingSafeEqual(Buffer.from(hash), Buffer.from(expected));
}

// ---------- Send helpers ----------

async function callSendAPI(psid: string, message: Record<string, unknown>) {
  await axios.post(
    GRAPH_API,
    { recipient: { id: psid }, message },
    {
      params: { access_token: PAGE_ACCESS_TOKEN },
      timeout: 10000,
    }
  );
}

export async function sendTextMessage(psid: string, text: string) {
  await callSendAPI(psid, { text });
}

export async function sendFlashInfo(psid: string, article: FlashInfoArticle) {
  const elements: Record<string, unknown>[] = [
    {
      title: article.title,
      subtitle: article.summary || article.date,
      image_url:
        article.imageUrl || "https://jskabylie.dz/assets/club_logo-DdPMF-uK.png",
      default_action: {
        type: "web_url",
        url: "https://jskabylie.dz/news",
      },
      buttons: [
        {
          type: "web_url",
          url: "https://jskabylie.dz/news",
          title: "Lire la suite",
        },
      ],
    },
  ];

  await callSendAPI(psid, {
    attachment: {
      type: "template",
      payload: {
        template_type: "generic",
        elements,
      },
    },
  });
}

// ---------- Broadcast to all active subscribers ----------

export async function broadcastFlashInfo(article: FlashInfoArticle) {
  const { data: subscribers, error } = await getSupabase()
    .from("subscribers")
    .select("psid")
    .eq("active", true);

  if (error || !subscribers) {
    console.error("Failed to fetch subscribers:", error);
    return;
  }

  const results = await Promise.allSettled(
    subscribers.map((sub) => sendFlashInfo(sub.psid, article))
  );

  const failed = results.filter((r) => r.status === "rejected").length;
  console.log(
    `Broadcast "${article.title}" → ${subscribers.length} subscribers, ${failed} failed`
  );
}

// ---------- Subscriber management ----------

export async function subscribe(psid: string, name?: string) {
  const { error } = await getSupabase().from("subscribers").upsert(
    { psid, name: name ?? null, active: true, subscribed_at: new Date().toISOString() },
    { onConflict: "psid" }
  );
  if (error) console.error("Subscribe error:", error);
}

export async function unsubscribe(psid: string) {
  const { error } = await getSupabase()
    .from("subscribers")
    .update({ active: false })
    .eq("psid", psid);
  if (error) console.error("Unsubscribe error:", error);
}
