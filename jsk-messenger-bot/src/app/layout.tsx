export const metadata = {
  title: "JSK Flash Info Bot",
  description: "Bot Messenger pour les Flash Info de la JS Kabylie",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="fr">
      <body>{children}</body>
    </html>
  );
}
