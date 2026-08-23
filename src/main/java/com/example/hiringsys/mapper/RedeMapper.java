package com.example.hiringsys.mapper;

import com.example.hiringsys.dto.request.RedeRequest;
import com.example.hiringsys.dto.response.RedeResponse;
import com.example.hiringsys.entity.Rede;
import org.springframework.stereotype.Component;

@Component
public class RedeMapper {

    public Rede toEntity(RedeRequest request) {
        Rede rede = new Rede();
        rede.setUrl(request.url());
        rede.setTipo(request.tipo());
        return rede;
    }

    public RedeResponse toResponse(Rede rede) {
        return new RedeResponse(
                rede.getId(),
                rede.getTipo(),
                rede.getUrl()
        );
    }
}
