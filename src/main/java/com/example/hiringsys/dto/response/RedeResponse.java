package com.example.hiringsys.dto.response;

import com.example.hiringsys.enums.TipoRede;

public record RedeResponse(
        Long id,
        String url,
        TipoRede tipo
) {
}
