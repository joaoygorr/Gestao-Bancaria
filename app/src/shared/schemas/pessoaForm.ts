import { z } from "zod";

const createPessoaFormSchema = z.object({
    id: z.string().or(z.number()).optional(),
    nome: z.string()
        .trim()
        .nonempty("O nome é obrigatório"),
    cpf: z
        .string()
        .trim()
        .nonempty("O CPF é obrigatório")
        .refine((cpf) => {
            cpf = cpf.replace(/[^\d]+/g, "");

            if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) return false;

            let soma = 0;
            for (let i = 0; i < 9; i++) soma += parseInt(cpf.charAt(i)) * (10 - i);
            let resto = (soma * 10) % 11;
            if (resto === 10 || resto === 11) resto = 0;
            if (resto !== parseInt(cpf.charAt(9))) return false;

            soma = 0;
            for (let i = 0; i < 10; i++) soma += parseInt(cpf.charAt(i)) * (11 - i);
            resto = (soma * 10) % 11;
            if (resto === 10 || resto === 11) resto = 0;

            return resto === parseInt(cpf.charAt(10));
        }, {
            message: "CPF inválido",
        }),
    endereco: z
        .string()
        .trim()
        .nonempty("O endereço é obrigatório")

});

export default createPessoaFormSchema;