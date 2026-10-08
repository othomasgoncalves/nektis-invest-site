import { Unbounded } from "next/font/google";
import localFont from "next/font/local";

export const unbounded = Unbounded({
  subsets: ["latin"],
  weight: ["300", "400", "500", "600", "700"],
  display: "swap",
  variable: "--font-unbounded",
});

export const satoshi = localFont({
  src: [{ path: "./Satoshi-Variable.woff2", style: "normal" }],
  weight: "300 900",
  display: "swap",
  variable: "--font-satoshi",
  fallback: ["ui-sans-serif", "system-ui", "sans-serif"],
});
