"use client";
import { pessoaApi } from "@/shared/api/api";
import PessoaTableItem from "@/shared/components/items/pessoaTableItems";
import createPessoaFormSchema from "@/shared/schemas/pessoaForm";
import { pessoaFormData } from "@/shared/schemas/types/types";
import MaskCpf from "@/shared/utils/maskCpf";
import CaptalizeWord from "@/shared/utils/transformWord";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Box,
  FormControl,
  FormLabel,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
} from "@mui/material";
import { AxiosError } from "axios";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "react-toastify";

export default function Page() {
  const [loading, setLoading] = useState(false);
  const [pessoa, setPessoa] = useState<pessoaFormData[]>([]);
  const [pessoaEdit, setPessoaEdit] = useState<pessoaFormData>();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<pessoaFormData>({
    resolver: zodResolver(createPessoaFormSchema),
  });

  const createPerson = async (data: pessoaFormData) => {
    setLoading(true);
    try {
      const res = await pessoaApi.create({
        ...data,
        cpf: data.cpf.replace(/\D/g, ""),
      });
      toast.success("Pessoa criada com sucesso!");
      setPessoa([...pessoa, res?.data]);
      reset({
        nome: "",
        cpf: "",
        endereco: "",
      });
    } catch (error) {
      if (error instanceof AxiosError) {
        toast.error(
          error?.response?.data?.message || "Erro na requisição Post."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    async function fetchData() {
      try {
        const response = await pessoaApi.getAll();
        setPessoa(response?.data);
      } catch (error) {
        if (error instanceof AxiosError) {
          toast.error(
            error?.response?.data?.message || "Erro na busca por pessoas."
          );
        }
      }
    }
    fetchData();
  }, []);

  const handleEditPessoa = async (data: pessoaFormData) => {
    setLoading(true);
    try {
      const res = await pessoaApi.updatePerson(data);
      setPessoa((prev) =>
        prev.map((p) => (p.id === res.data.id ? res.data : p))
      );
      reset({
        nome: "",
        cpf: "",
        endereco: "",
      });
      setPessoaEdit(undefined);
      toast.success("Pessoa atualizada com sucesso!");
    } catch (error) {
      if (error instanceof AxiosError) {
        toast.error(
          error.response?.data?.error || "Erro na requisição Update."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-2xl">
      <h1 className="text-2xl font-bold mb-6 text-gray-800">
        Cadastro de Pessoa
      </h1>
      <Box
        component="form"
        className="space-y-4 flex flex-col self-center gap-4 bg-white shadow-lg rounded-xl p-8"
        onSubmit={handleSubmit(pessoaEdit ? handleEditPessoa : createPerson)}
      >
        <FormControl>
          <FormLabel htmlFor="nameField">
            Nome<span className="text-red-500">*</span>
          </FormLabel>
          <TextField
            id="nameField"
            variant="standard"
            placeholder="Ex: João da Silva"
            fullWidth
            disabled={loading}
            defaultValue={pessoaEdit?.nome}
            color={errors?.nome ? "error" : "primary"}
            helperText={errors.nome?.message}
            {...register("nome", {
              onChange: (e) => {
                e.target.value = CaptalizeWord(
                  e.target.value.replace(/[^A-Za-zÀ-ÿ\s]/g, "")
                );
                return e;
              },
            })}
          />
        </FormControl>

        <FormControl>
          <FormLabel htmlFor="cpfField">
            CPF<span className="text-red-500">*</span>
          </FormLabel>
          <TextField
            id="cpfField"
            variant="standard"
            placeholder="123.456.789-00"
            fullWidth
            onInput={(e) => {
              const input = e.target as HTMLInputElement;
              if (input.value.length > 11) {
                input.value = input.value.slice(0, 14);
              }
            }}
            disabled={loading}
            defaultValue={pessoaEdit?.cpf}
            color={errors?.cpf ? "error" : "primary"}
            helperText={errors.cpf?.message}
            {...register("cpf", {
              onChange: (e) => {
                e.target.value = MaskCpf(e.target.value);
                return e;
              },
            })}
          />
        </FormControl>

        <FormControl>
          <FormLabel htmlFor="enderecoField">
            Endereço<span className="text-red-500">*</span>
          </FormLabel>
          <TextField
            id="enderecoField"
            variant="standard"
            placeholder="Rua Exemplo, 123"
            fullWidth
            disabled={loading}
            defaultValue={pessoaEdit?.endereco}
            color={errors?.endereco ? "error" : "primary"}
            helperText={errors.endereco?.message}
            {...register("endereco")}
          />
        </FormControl>

        <Box className="text-right">
          <button
            type="submit"
            className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 transition cursor-pointer"
          >
            Salvar
          </button>
        </Box>
      </Box>

      <div className="mt-12">
        <h1 className="text-2xl mb-6 font-bold text-gray-800">
          Pessoas Cadastradas
        </h1>
        <Table className="w-full max-w-4xl bg-white shadow-lg rounded-xl overflow-hidden">
          <TableHead>
            <TableRow>
              <TableCell>Nome</TableCell>
              <TableCell>CPF</TableCell>
              <TableCell>Endereço</TableCell>
              <TableCell align="center" sx={{ width: 150 }}>
                Ações
              </TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {pessoa?.map((item) => (
              <PessoaTableItem
                key={item.id}
                item={item}
                onEdit={(e) => {
                  setPessoaEdit(e);
                  reset(e);
                }}
                setPessoa={setPessoa}
              />
            ))}

            {pessoa.length === 0 && (
              <TableRow>
                <TableCell colSpan={4}>Nenhuma pessoa cadastrada</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
