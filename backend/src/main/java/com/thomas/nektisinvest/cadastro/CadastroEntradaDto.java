package com.thomas.nektisinvest.cadastro;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroEntradaDto(
        @NotBlank(message = "Informe o seu nome completo.")
        @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres.")
        String nome,

        @NotBlank(message = "Informe o seu e-mail.")
        @Email(message = "E-mail inválido.")
        @Size(max = 180, message = "E-mail muito longo.")
        String email,

        @NotBlank(message = "Informe o seu WhatsApp.")
        @Pattern(
                regexp = "\\+[1-9]\\d{7,14}",
                message = "WhatsApp deve estar no formato internacional.")
        String telefone) {}
