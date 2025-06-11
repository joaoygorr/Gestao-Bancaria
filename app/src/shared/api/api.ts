import axios, { AxiosInstance } from "axios";
import { contaFormData, movimentacaoFormData, pessoaFormData } from "../schemas/types/types";
import { IResponse } from "../schemas/types/IResponse";
import { IConta } from "../schemas/types/IConta";
import { IMovimentacao } from "../schemas/types/IMovimentacao";
import { IPessoaWithConta } from "../schemas/types/IPessoaWithConta";

export class Api {
    private api: AxiosInstance;

    constructor(url: string) {
        this.api = axios.create({
            baseURL: `http://localhost:8080/api${url}`,
            headers: {
                "Content-Type": "application/json",
            },
            withCredentials: true
        });
    }

    async create(pessoa: pessoaFormData): Promise<IResponse<pessoaFormData>> {
        return await this.api.post("/create", pessoa);
    }

    async delete(id: string): Promise<void> {
        return await this.api.delete(`/${id}`);
    }

    async createAccount(conta: contaFormData): Promise<IResponse<contaFormData>> {
        return await this.api.post("/create", conta);
    }

    async createMovement(conta: movimentacaoFormData): Promise<IResponse<movimentacaoFormData>> {
        return await this.api.post("/create", conta);
    }

    async getAll(): Promise<IResponse<pessoaFormData[]>> {
        return await this.api.get("");
    }

    async getAllPersonWithAccounts(): Promise<IResponse<IPessoaWithConta[]>> {
        return await this.api.get("/pessoas/contas");
    }

    async getAllAccounts(): Promise<IResponse<IConta[]>> {
        return await this.api.get("");
    }

    async getAllMovementsByPerson(id: number): Promise<IResponse<IMovimentacao[]>> {
        return await this.api.get(`/${id}`);
    }

    async updatePerson(person: pessoaFormData): Promise<IResponse<pessoaFormData>> {
        return await this.api.put(`/${Number(person.id)}`, person);
    }

    async updateAccount(account: contaFormData): Promise<IResponse<contaFormData>> {
        return await this.api.put(`/${Number(account.id)}`, account);
    }
}

export const pessoaApi = new Api("/pessoa");

export const contaApi = new Api("/conta");

export const movimentacaoApi = new Api("/movimentacao");