package br.com.gestao_bancaria.configuration.execptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class Exception409 extends RuntimeException {
    public Exception409(String message) {
        super(message);
    }
}
