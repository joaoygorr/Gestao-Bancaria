import { z } from "zod";

const createMovimentacaoFormSchema = z.object({
    id: z.number().optional(),
    pessoa: z.string().min(1, "A pessoa é obrigatória"),
    conta: z.string().min(1, "A Conta é obrigatória"),
    tipo: z.string().min(1, "Tipo Movimentação é obrigatório"),
    valor: z.string().trim()
        .nonempty("Valor é obrigatório"),
    data: z.string().optional(),
    totaValor: z.string().optional()
});

export default createMovimentacaoFormSchema;