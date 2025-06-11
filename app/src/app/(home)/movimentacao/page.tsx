"use client";
import { movimentacaoApi, pessoaApi } from "@/shared/api/api";
import MovimentacaoTableItem from "@/shared/components/items/movimentacaoTableItem";
import createMovimentacaoFormSchema from "@/shared/schemas/movimentacaoForm";
import { IPessoaWithConta } from "@/shared/schemas/types/IPessoaWithConta";
import { movimentacaoFormData } from "@/shared/schemas/types/types";
import FormatValor from "@/shared/utils/formatValor";
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
import { useEffect, useMemo, useState } from "react";
import { Controller, useForm, useWatch } from "react-hook-form";
import { toast } from "react-toastify";

export default function Page() {
  const [loading, setLoading] = useState(false);
  const [pessoasComContas, setPessoasComContas] = useState<IPessoaWithConta[]>(
    []
  );

  const {
    register,
    handleSubmit,
    reset,
    control,
    setValue,
    formState: { errors },
  } = useForm<movimentacaoFormData>({
    resolver: zodResolver(createMovimentacaoFormSchema),
    defaultValues: {
      pessoa: "",
      conta: "",
      tipo: "",
    },
  });

  const pessoaSelecionada = useWatch({ control, name: "pessoa" });
  const contaSelecionada = useWatch({ control, name: "conta" });

  const saveMovement = async (dado: movimentacaoFormData) => {
    setLoading(true);
    try {
      await movimentacaoApi.createMovement({
        ...dado,
        data: new Date().toLocaleString("pt-BR", {
          timeZone: "America/Cuiaba",
        }),
        valor: dado.valor
          .replace("R$", "")
          .replace(/\./g, "")
          .replace(",", ".")
          .trim(),
      });
      toast.success("Movimentacão criada com sucesso!");
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
        const conta = await pessoaApi.getAllPersonWithAccounts();
        setPessoasComContas(conta?.data);
      } catch (error) {
        if (error instanceof AxiosError) {
          toast.error(error?.response?.data?.message || "Erro busca listagem.");
        }
      }
    }
    fetchData();
  }, [loading]);

  const contas = useMemo(() => {
    return (
      pessoasComContas.find(
        (p) => String(p.pessoa.id) === String(pessoaSelecionada)
      )?.contas ?? []
    );
  }, [pessoasComContas, pessoaSelecionada]);

  const contaSelecionadaObj = useMemo(() => {
    return contas.find((c) => String(c.id) === String(contaSelecionada));
  }, [contas, contaSelecionada]);

  const movimentacoes = useMemo(() => {
    return contaSelecionadaObj?.movimentacoes ?? [];
  }, [contaSelecionadaObj]);

  const saldo = contaSelecionadaObj?.totalValor ?? 0;

  return (
    <div className="w-full max-w-2xl">
      <h1 className="text-2xl font-bold mb-6 text-gray-800">
        Cadastro de Movimentação
      </h1>

      <Box
        component="form"
        className="space-y-4 flex flex-col self-center gap-4 bg-white shadow-lg rounded-xl p-8"
        onSubmit={handleSubmit(saveMovement)}
      >
        <FormControl>
          <Controller
            name="pessoa"
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
                  error={!!errors.pessoa}
                  displayEmpty
                  {...field}
                  value={field.value || ""}
                >
                  <MenuItem value="" disabled>
                    Selecione uma pessoa
                  </MenuItem>
                  {pessoasComContas.map((c) => (
                    <MenuItem key={c.pessoa.id} value={String(c.pessoa.id)}>
                      {`${c.pessoa.nome} - ${MaskCpf(c.pessoa.cpf)}`}
                    </MenuItem>
                  ))}
                </Select>
                {errors.pessoa && (
                  <FormHelperText>{errors.pessoa.message}</FormHelperText>
                )}
              </FormControl>
            )}
          />
        </FormControl>

        <FormControl>
          <Controller
            name="conta"
            control={control}
            render={({ field }) => (
              <FormControl>
                <FormLabel htmlFor="numeroContaField">
                  Número da Conta<span className="text-red-500">*</span>
                </FormLabel>
                <Select
                  id="numeroContaField"
                  variant="standard"
                  fullWidth
                  disabled={loading}
                  error={!!errors.conta}
                  displayEmpty
                  {...field}
                  value={field.value || ""}
                >
                  <MenuItem value="" disabled>
                    Selecione um número da conta
                  </MenuItem>
                  {contas?.map((c) => (
                    <MenuItem key={c.id} value={String(c.id)}>
                      {`${c.numeroConta} - ${FormatValor(
                        Number(c.totalValor)
                      )}`}
                    </MenuItem>
                  ))}
                </Select>
                {errors.conta && (
                  <FormHelperText>{errors.conta.message}</FormHelperText>
                )}
              </FormControl>
            )}
          />
        </FormControl>

        <FormControl>
          <Controller
            name="tipo"
            control={control}
            render={({ field }) => (
              <FormControl>
                <FormLabel htmlFor="tipoField">
                  Depositar/Retirar<span className="text-red-500">*</span>
                </FormLabel>
                <Select
                  id="tipoField"
                  variant="standard"
                  fullWidth
                  disabled={loading}
                  error={!!errors.tipo}
                  {...field}
                  displayEmpty
                  value={field.value || ""}
                >
                  <MenuItem value="" disabled>
                    Selecione um tipo
                  </MenuItem>
                  <MenuItem value="DEPOSITAR">Depositar</MenuItem>
                  <MenuItem value="RETIRAR">Retirar</MenuItem>
                </Select>
                {errors.tipo && (
                  <FormHelperText>{errors.tipo.message}</FormHelperText>
                )}
              </FormControl>
            )}
          />
        </FormControl>

        <FormControl>
          <FormLabel htmlFor="valorField">
            Valor<span className="text-red-500">*</span>
          </FormLabel>
          <TextField
            id="valorField"
            variant="standard"
            placeholder="R$ 500,00"
            fullWidth
            disabled={loading}
            color={errors?.valor ? "error" : "primary"}
            helperText={errors.valor?.message}
            {...register("valor", {
              onChange: (e) => {
                const numberValue =
                  Number(e.target.value.replace(/[^\d]/g, "")) / 100;
                setValue("valor", FormatValor(numberValue));
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
        <h1 className="text-2xl mb-6 font-bold text-gray-800">Extrato</h1>
        <Table className="w-full max-w-4xl bg-white shadow-lg rounded-xl overflow-hidden">
          <TableHead>
            <TableRow>
              <TableCell>Data</TableCell>
              <TableCell>Valor</TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {movimentacoes?.map((item) => (
              <MovimentacaoTableItem key={item.id} item={item} />
            ))}

            {movimentacoes.length === 0 && (
              <TableRow>
                <TableCell colSpan={4}>
                  Nenhuma movimentação cadastrada
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
        <span className="text-xl font-semibold mt-4 block">
          Saldo: {FormatValor(Number(saldo))}
        </span>
      </div>
    </div>
  );
}
