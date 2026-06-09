import { Footer } from "@/components/Footer";
import { Navbar } from "@/components/Navbar";
import { NewsletterForm } from "./NewsletterForm";

export default function NewsletterPage() {
  return (
    <main className="min-h-screen bg-[#020912] text-white">
      <Navbar />
      <section className="relative grid min-h-[calc(100vh-5rem)] place-items-center overflow-hidden px-6 py-20">
        <div className="absolute left-1/2 top-1/2 h-[34rem] w-[34rem] -translate-x-1/2 -translate-y-1/2 rounded-full bg-earth-accent/10 blur-3xl" />
        <div className="absolute inset-0 opacity-20 [background-image:linear-gradient(rgba(255,255,255,0.07)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.05)_1px,transparent_1px)] [background-size:48px_48px]" />
        <div className="relative w-full max-w-2xl rounded-[2rem] border border-white/10 bg-[#071522]/90 p-8 text-center shadow-2xl shadow-black/40 backdrop-blur-xl md:p-12">
          <p className="text-xs font-black uppercase tracking-[0.35em] text-earth-accent">Newsletter</p>
          <h1 className="mt-5 text-4xl font-black uppercase leading-none tracking-[-0.05em] md:text-6xl">
            Follow the build.
          </h1>
          <p className="mx-auto mt-5 max-w-xl text-base leading-7 text-white/68">
            Tilmeld dig for development updates, beta invitations og launch announcements. Backend kommer senere, så dette er mock UI for nu.
          </p>
          <NewsletterForm />
        </div>
      </section>
      <Footer />
    </main>
  );
}
