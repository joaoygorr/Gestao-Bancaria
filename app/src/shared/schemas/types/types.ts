import { z } from "zod";
import createPessoaFormSchema from "../pessoaForm";
import createContaFormSchema from "../contaForm";
import createMovimentacaoFormSchema from "../movimentacaoForm";

export type pessoaFormData = z.infer<typeof createPessoaFormSchema>;

export type contaFormData = z.infer<typeof createContaFormSchema>;

export type movimentacaoFormData = z.infer<typeof createMovimentacaoFormSchema>;