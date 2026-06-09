import { Card } from "@/components/Card";
import { Footer } from "@/components/Footer";
import { Navbar } from "@/components/Navbar";
import { ContactForm } from "./ContactForm";

export default function ContactPage() {
  return (
    <main className="min-h-screen bg-[#020912] text-white">
      <Navbar />
      <section className="relative overflow-hidden px-6 py-20">
        <div className="absolute right-10 top-0 h-96 w-96 rounded-full bg-earth-accent/10 blur-3xl" />
        <div className="relative mx-auto grid max-w-7xl gap-8 lg:grid-cols-[0.85fr_1fr] lg:items-center">
          <div>
            <p className="text-xs font-black uppercase tracking-[0.35em] text-earth-accent">Contact</p>
            <h1 className="mt-5 text-5xl font-black uppercase leading-none tracking-[-0.06em] md:text-7xl">
              Talk to Earth Living.
            </h1>
            <p className="mt-6 max-w-2xl text-lg leading-8 text-white/68">
              Har du feedback, spørgsmål eller en ide til projektet, kan du sende en besked her. Backend kommer senere, så formen viser kun en lokal success confirmation.
            </p>
          </div>
          <Card className="bg-[#071522]/90 backdrop-blur-xl">
            <ContactForm />
          </Card>
        </div>
      </section>
      <Footer />
    </main>
  );
}
