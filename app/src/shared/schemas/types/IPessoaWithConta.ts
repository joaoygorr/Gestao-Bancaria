import { IMovimentacao } from "./IMovimentacao";
import { pessoaFormData } from "./types";

export interface IPessoaWithConta {
    contas: Conta[];
    pessoa: pessoaFormData;
}

interface Conta {
    id: string;
    numeroConta: string;
    totalValor: number;
    movimentacoes: IMovimentacao[]
}