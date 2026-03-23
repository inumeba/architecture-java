import { NextRequest, NextResponse } from "next/server";
import { scrapeFlashInfo } from "@/lib/scraper";
import { broadcastFlashInfo } from "@/lib/messenger";
import { getSupabase } from "@/lib/supabase";

export async function GET(request: NextRequest) {
  // Protect the cron endpoint
  const authHeader = request.headers.get("authorization");
  if (authHeader !== `Bearer ${process.env.CRON_SECRET}`) {
    return NextResponse.json({ error: "Unauthorized" }, { status: 401 });
  }

  try {
    const articles = await scrapeFlashInfo();
    console.log(`Scraped ${articles.length} Flash Info articles`);

    let newCount = 0;

    for (const article of articles) {
      // Check if already sent
      const { data: existing } = await getSupabase()
        .from("sent_articles")
        .select("id")
        .eq("article_hash", article.articleHash)
        .single();

      if (existing) continue;

      // New article — store and broadcast
      const { error: insertError } = await getSupabase()
        .from("sent_articles")
        .insert({
          article_hash: article.articleHash,
          title: article.title,
          image_url: article.imageUrl,
          published_at: article.date,
        });

      if (insertError) {
        console.error("Insert error:", insertError);
        continue;
      }

      await broadcastFlashInfo(article);
      newCount++;
      console.log(`New Flash Info sent: ${article.title}`);
    }

    return NextResponse.json({
      status: "ok",
      scraped: articles.length,
      new: newCount,
    });
  } catch (error) {
    console.error("Cron scrape failed:", error);
    return NextResponse.json(
      { error: "Scrape failed" },
      { status: 500 }
    );
  }
}
