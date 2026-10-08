package com.thomas.nektisinvest.cadastro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thomas.nektisinvest.acesso.CanalAcesso;
import com.thomas.nektisinvest.assinatura.SituacaoAssinatura;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SituacaoCadastroDto(SituacaoAssinatura situacao, List<Link> links) {

    public record Link(CanalAcesso canal, String url) {}

    public static SituacaoCadastroDto semLinks(SituacaoAssinatura situacao) {
        return new SituacaoCadastroDto(situacao, null);
    }
}
