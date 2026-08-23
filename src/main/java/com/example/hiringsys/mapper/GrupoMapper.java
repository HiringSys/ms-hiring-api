package com.example.hiringsys.mapper;

import com.example.hiringsys.dto.request.GrupoRequest;
import com.example.hiringsys.dto.response.GrupoResponse;
import com.example.hiringsys.entity.Grupo;
import org.springframework.stereotype.Component;

@Component
public class GrupoMapper {

    public Grupo toEntity(GrupoRequest request) {
        Grupo grupo = new Grupo();
        grupo.setNome(request.nome());
        grupo.setArea(request.area());
        grupo.setEstado(request.estado());
        grupo.setDisponiveis(request.disponiveis());
        grupo.setCargo(request.cargo());
        grupo.setLimiteAprovados(request.limiteAprovados());
        grupo.setEmailEquipe(request.emailEquipe());
        return grupo;
    }

    public GrupoResponse toResponse(Grupo grupo) {
        return new GrupoResponse(
                grupo.getId(),
                grupo.getNome(),
                grupo.getArea(),
                grupo.getEstado(),
                grupo.getDisponiveis(),
                grupo.getCargo(),
                grupo.getLimiteAprovados(),
                grupo.getEmailEquipe(),
                grupo.getCriadoEm()
        );
    }
}
