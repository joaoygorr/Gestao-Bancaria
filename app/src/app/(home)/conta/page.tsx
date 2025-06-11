"use client";
import { contaApi, pessoaApi } from "@/shared/api/api";
import ContaTableItem from "@/shared/components/items/contaTableItems";
import createContaFormSchema from "@/shared/schemas/contaForm";
import { IConta } from "@/shared/schemas/types/IConta";
import { contaFormData, pessoaFormData } from "@/shared/schemas/types/types";
import MaskCpf from "@/shared/utils/maskCpf";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Box,
  FormControl,
  FormHelperText,
  FormLabel,
  MenuItem,
  Select,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
} from "@mui/material";
import { AxiosError } from "axios";
import { useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import { toast } from "react-toastify";

export default function Page() {
  const [loading, setLoading] = useState(false);
  const [pessoa, setPessoa] = useState<pessoaFormData[]>([]);
  const [conta, setConta] = useState<IConta[]>([]);
  const [contaEdit, setContaEdit] = useState<contaFormData>();

  const {
    register,
    handleSubmit,
    reset,
    control,
    formState: { errors },
  } = useForm<contaFormData>({
    resolver: zodResolver(createContaFormSchema),
    defaultValues: {
      idPessoa: "",
    },
  });

  const saveAccount = async (data: contaFormData) => {
    setLoading(true);
    try {
      await contaApi.createAccount(data);
      toast.success("Conta criada com sucesso!");
      reset();
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
        const pessoa = await pessoaApi.getAll();
        const conta = await contaApi.getAllAccounts();
        setConta(conta?.data);
        setPessoa(pessoa?.data);
      } catch (error) {
        if (error instanceof AxiosError) {
          toast.error(error?.response?.data?.message || "Erro busca listagem.");
        }
      }
    }
    fetchData();
  }, [loading]);

  const handleEditConta = async (data: contaFormData) => {
    setLoading(true);
    try {
      await contaApi.updateAccount(data);
      reset({ numeroConta: "", idPessoa: "" });
      setContaEdit(undefined);
      toast.success("Conta atualizada com sucesso!");
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
        Cadastro de Conta
      </h1>

      <Box
        component="form"
        className="space-y-4 flex flex-col self-center gap-4 bg-white shadow-lg rounded-xl p-8"
        onSubmit={handleSubmit(contaEdit ? handleEditConta : saveAccount)}
      >
        <FormControl>
          <Controller
            name="idPessoa"
            control={control}
            render={({ field }) => (
              <FormControl>
                <FormLabel htmlFor="pessoaField">
                  Pessoa<span className="text-red-500">*</span>
                </FormLabel>
                <Select
                  id="pessoaField"
                  variant="standard"
                  fullWidth
                  disabled={loading}
                  displayEmpty
                  error={!!errors.idPessoa}
                  defaultValue={contaEdit?.idPessoa}
                  {...field}
                  value={field.value || ""}
                >
                  <MenuItem value="" disabled>
                    Selecione uma pessoa
                  </MenuItem>
                  {pessoa.map((pe) => (
                    <MenuItem key={pe.id} value={String(pe.id)}>
                      {`${pe.nome} - ${MaskCpf(pe.cpf)}`}
                    </MenuItem>
                  ))}
                </Select>
                {errors.idPessoa && (
                  <FormHelperText>{errors.idPessoa.message}</FormHelperText>
                )}
              </FormControl>
            )}
          />
        </FormControl>

        <FormControl>
          <FormLabel htmlFor="numeroContaField">
            Número da Conta<span className="text-red-500">*</span>
          </FormLabel>
          <TextField
            id="numeroContaField"
            variant="standard"
            placeholder="000000"
            fullWidth
            disabled={loading}
            defaultValue={contaEdit?.numeroConta}
            color={errors?.numeroConta ? "error" : "primary"}
            helperText={errors.numeroConta?.message}
            {...register("numeroConta", {
              onChange: (e) => {
                e.target.value = e.target.value.replace(/\D/g, "");
                return e;
              },
            })}
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
          Contas Cadastradas
        </h1>
        <Table className="w-full max-w-4xl bg-white shadow-lg rounded-xl overflow-hidden">
          <TableHead>
            <TableRow>
              <TableCell>Nome</TableCell>
              <TableCell>CPF</TableCell>
              <TableCell>Número da Conta</TableCell>
              <TableCell className="box-actions">Ações</TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {conta?.map((item) => (
              <ContaTableItem
                key={item.id}
                item={item}
                setConta={setConta}
                onEdit={(e) => {
                  setContaEdit(e);
                  reset(e);
                }}
              />
            ))}

            {conta.length === 0 && (
              <TableRow>
                <TableCell colSpan={4}>Nenhuma conta cadastrada</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
