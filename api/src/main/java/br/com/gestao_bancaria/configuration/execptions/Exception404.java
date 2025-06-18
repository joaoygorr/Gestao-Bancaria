package br.com.gestao_bancaria.configuration.execptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class Exception404 extends RuntimeException {
    public Exception404(String message) {
        super(message);
    }
}
