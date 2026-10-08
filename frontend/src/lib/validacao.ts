import { z } from "zod";
import { telefoneValido } from "./telefone";

export const cadastroSchema = z.object({
  nome: z
    .string()
    .trim()
    .min(3, "Informe o seu nome completo.")
    .max(120, "Nome muito longo."),
  email: z
    .string()
    .trim()
    .min(1, "Informe o seu e-mail.")
    .max(180, "E-mail muito longo.")
    .pipe(z.email("E-mail inválido.")),
  telefone: z
    .string()
    .trim()
    .min(1, "Informe o seu WhatsApp.")
    .refine(telefoneValido, "WhatsApp inválido. Use o formato internacional."),
});

export type FormCadastro = z.infer<typeof cadastroSchema>;

export type ErrosCampo = Partial<Record<keyof FormCadastro, string>>;

export function errosDeCampo(erro: z.ZodError<FormCadastro>): ErrosCampo {
  const erros: ErrosCampo = {};
  for (const problema of erro.issues) {
    const chave = problema.path[0];
    if (typeof chave === "string" && !(chave in erros)) {
      erros[chave as keyof FormCadastro] = problema.message;
    }
  }
  return erros;
}
