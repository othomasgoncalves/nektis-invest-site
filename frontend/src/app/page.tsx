"use client";

import { ComoFunciona } from "@/components/secoes/ComoFunciona";
import { Comunidade } from "@/components/secoes/Comunidade";
import { ContagemRegressiva } from "@/components/secoes/ContagemRegressiva";
import { Duvidas } from "@/components/secoes/Duvidas";
import { FluxoEntrada } from "@/components/secoes/FluxoEntrada";
import { Footer } from "@/components/secoes/Footer";
import { Header } from "@/components/secoes/Header";
import { Hero } from "@/components/secoes/Hero";
import { JoaoPedro } from "@/components/secoes/JoaoPedro";
import { NektisTech } from "@/components/secoes/NektisTech";
import { Plano } from "@/components/secoes/Plano";
import { Video } from "@/components/secoes/Video";
import { useOferta } from "@/lib/oferta";

export default function Home() {
  const oferta = useOferta();

  return (
    <>
      <Header />
      <main>
        <Hero />
        <ContagemRegressiva
          prazo={oferta.prazoInscricao}
          inscricoesAbertas={oferta.inscricoesAbertas}
        />
        <Video />
        <ComoFunciona />
        <Comunidade />
        <JoaoPedro />
        <Plano oferta={oferta} />
        <FluxoEntrada oferta={oferta} />
        <Duvidas />
        <NektisTech />
      </main>
      <Footer />
    </>
  );
}
