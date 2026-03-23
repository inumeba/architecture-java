import { NextRequest, NextResponse } from "next/server";
import {
  verifyRequestSignature,
  sendTextMessage,
  subscribe,
  unsubscribe,
  sendFlashInfo,
} from "@/lib/messenger";
import { getSupabase } from "@/lib/supabase";
import type { MessengerWebhookEntry, MessengerEvent } from "@/lib/types";

// ---------- GET: Facebook webhook verification ----------

export async function GET(request: NextRequest) {
  const searchParams = request.nextUrl.searchParams;
  const mode = searchParams.get("hub.mode");
  const token = searchParams.get("hub.verify_token");
  const challenge = searchParams.get("hub.challenge");

  if (mode === "subscribe" && token === process.env.FACEBOOK_VERIFY_TOKEN) {
    console.log("Webhook verified");
    return new NextResponse(challenge, { status: 200 });
  }

  return NextResponse.json({ error: "Forbidden" }, { status: 403 });
}

// ---------- POST: Incoming messages ----------

export async function POST(request: NextRequest) {
  // Validate signature
  const signature = request.headers.get("x-hub-signature-256");
  const rawBody = Buffer.from(await request.arrayBuffer());

  if (!verifyRequestSignature(rawBody, signature)) {
    console.warn("Invalid signature on webhook request");
    return NextResponse.json({ error: "Invalid signature" }, { status: 403 });
  }

  const body = JSON.parse(rawBody.toString("utf-8"));

  if (body.object !== "page") {
    return NextResponse.json({ error: "Not a page event" }, { status: 404 });
  }

  const entries: MessengerWebhookEntry[] = body.entry ?? [];

  for (const entry of entries) {
    for (const event of entry.messaging ?? []) {
      await handleEvent(event);
    }
  }

  return NextResponse.json({ status: "ok" }, { status: 200 });
}

// ---------- Event handler ----------

async function handleEvent(event: MessengerEvent) {
  const psid = event.sender.id;
  const text = event.message?.text?.trim().toLowerCase();

  if (!text) return;

  const subscribeCommands = ["start", "s'abonner", "sabonner", "abonner", "oui"];
  const unsubscribeCommands = ["stop", "se désabonner", "desabonner", "non", "arreter"];
  const lastCommands = ["dernier", "last", "news", "flash"];

  if (subscribeCommands.includes(text)) {
    await subscribe(psid);
    await sendTextMessage(
      psid,
      "✅ Vous êtes maintenant abonné aux Flash Info de la JS Kabylie ! 🦁\n\nVous recevrez les dernières nouvelles en temps réel.\n\nEnvoyez « stop » pour vous désabonner."
    );
  } else if (unsubscribeCommands.includes(text)) {
    await unsubscribe(psid);
    await sendTextMessage(
      psid,
      "❌ Vous êtes désabonné des Flash Info.\n\nEnvoyez « start » pour vous réabonner."
    );
  } else if (lastCommands.includes(text)) {
    const { data } = await getSupabase()
      .from("sent_articles")
      .select("*")
      .order("sent_at", { ascending: false })
      .limit(1)
      .single();

    if (data) {
      await sendFlashInfo(psid, {
        title: data.title,
        date: data.published_at ?? "",
        summary: "",
        imageUrl: data.image_url,
        articleHash: data.article_hash,
      });
    } else {
      await sendTextMessage(psid, "Aucun Flash Info disponible pour le moment.");
    }
  } else {
    await sendTextMessage(
      psid,
      "🦁 JSK Flash Info Bot\n\nCommandes :\n• « start » — S'abonner\n• « stop » — Se désabonner\n• « dernier » — Dernier Flash Info\n\nYiwen Ubrid Yiwen Yiswi 💛💚"
    );
  }
}
