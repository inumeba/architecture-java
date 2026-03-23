import axios from "axios";
import * as cheerio from "cheerio";
import { createHash } from "crypto";
import type { FlashInfoArticle } from "./types";

const JSK_NEWS_URL = "https://jskabylie.dz/news";

function hashTitle(title: string): string {
  return createHash("sha256").update(title.trim().toLowerCase()).digest("hex");
}

export async function scrapeFlashInfo(): Promise<FlashInfoArticle[]> {
  const { data: html } = await axios.get(JSK_NEWS_URL, {
    timeout: 15000,
    headers: {
      "User-Agent":
        "JSK-Messenger-Bot/1.0 (Flash Info Scraper; +https://jskabylie.dz)",
      Accept: "text/html",
    },
  });

  const $ = cheerio.load(html);
  const articles: FlashInfoArticle[] = [];

  // Each news card on the /news page is rendered inside a container.
  // We look for elements that contain the category label "Flash Info"
  // and extract the sibling title, date, image, and summary.
  $("a[href*='/news/'], div, article, section, li").each((_, el) => {
    const block = $(el);
    const text = block.text();

    // Only keep blocks that are tagged "Flash Info"
    // The site renders the category as a standalone text node "Flash Info"
    const hasFlashTag =
      block.find("*").filter((__, child) => {
        const t = $(child).text().trim();
        return t === "Flash Info" || t === "Flash info";
      }).length > 0;

    if (!hasFlashTag) return;

    // Extract title — typically the most prominent heading or link text
    const titleEl =
      block.find("h1, h2, h3, h4, h5, h6").first().text().trim() ||
      block.find("a").first().text().trim();
    if (!titleEl || titleEl === "Flash Info" || titleEl === "Flash info") return;

    // Extract date
    const dateMatch = text.match(/\d{1,2}\s+\w{3,4}\s+\d{4}/);
    const date = dateMatch ? dateMatch[0] : "";

    // Extract image URL
    const img = block.find("img").first();
    const imageUrl =
      img.attr("src") || img.attr("data-src") || img.attr("loading") || null;

    // Extract summary (first chunk of meaningful text, excluding title/date/category)
    const fullText = block.text().replace(/Flash Info/gi, "").replace(titleEl, "").trim();
    const summary = fullText.slice(0, 300).replace(/\s+/g, " ").trim();

    const articleHash = hashTitle(titleEl);

    // Avoid duplicates within same scrape run
    if (articles.some((a) => a.articleHash === articleHash)) return;

    articles.push({
      title: titleEl,
      date,
      summary,
      imageUrl: imageUrl && imageUrl.startsWith("/")
        ? `https://jskabylie.dz${imageUrl}`
        : imageUrl,
      articleHash,
    });
  });

  return articles;
}
