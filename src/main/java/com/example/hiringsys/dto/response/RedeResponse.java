package com.example.hiringsys.dto.response;
import com.example.hiringsys.enums.TipoRede;
public record RedeResponse(Long id, TipoRede tipo, String url) {}
