# Copilot Workspace Instructions

## Playwright Auto-Run Preference

When the user asks to open a browser, test a page, validate UI behavior, or reproduce a frontend bug, run the Playwright MCP browser flow automatically instead of asking the user to do manual browser actions.

Use this default sequence:
1. Open the target URL with Playwright MCP navigation.
2. If auth is required, perform the minimal login flow using available test credentials from context.
3. Execute requested UI interactions (click, type, upload file, etc.).
4. Capture console messages (error level first, then info if needed).
5. Return exact observed result and error text.

## Localhost Defaults

- Prefer `http://localhost:3000`.
- If unavailable, try `http://localhost:3001`.
- If the app is not running, start it from the correct project folder before Playwright actions.

## External URL Defaults

- If the user provides an explicit URL (https://... or http://...), open and test that URL first.
- Do not force localhost when a web URL is provided.
- If the provided URL needs authentication, perform only the minimal flow requested or possible from available context.
- If no URL is provided by the user, then apply the localhost defaults above.

## Response Style For Browser Tests

- Report concrete outcomes from Playwright execution.
- Include exact console error text when present.
- Do not claim manual browser tests were performed by the user.

## Sensitive Data Policy

- Treat all personal data and secrets as local-only by default.
- Never ask to paste or reveal full tokens, passwords, API keys, cookies, private keys, or personal identifiers in chat.
- If a secret is needed for troubleshooting, request only a redacted value (for example: first 4 and last 4 chars).
- Do not print secrets in terminal output summaries; always mask them.
- Prefer using local environment files and existing secure variables instead of copying sensitive values into messages.
