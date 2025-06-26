import { z } from "zod";

const createContaFormSchema = z.object({
    id: z.string().or(z.number()).optional(),
    idPessoa: z.string().min(1, "A pessoa é obrigatória"),
    numeroConta: z.string()
        .trim()
        .nonempty("O número da conta é obrigatório")
        .max(15, "O número da conta deve ter no máximo 15 caracteres")
});

export default createContaFormSchema;

