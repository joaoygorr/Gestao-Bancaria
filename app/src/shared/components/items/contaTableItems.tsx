"use cliente";
import { Delete, Edit } from "@mui/icons-material";
import { TableCell, TableRow } from "@mui/material";
import Modal from "../dialog/modal";
import MaskCpf from "@/shared/utils/maskCpf";
import { IConta } from "@/shared/schemas/types/IConta";
import { useState } from "react";
import { contaApi } from "@/shared/api/api";
import { toast } from "react-toastify";
import { AxiosError } from "axios";
import { contaFormData } from "@/shared/schemas/types/types";

type Props = {
  item: IConta;
  onEdit: (item: contaFormData) => void;
  setConta: React.Dispatch<React.SetStateAction<IConta[]>>;
  length: number;
};

export default function ContaTableItem({
  item,
  onEdit,
  setConta,
  length,
}: Props) {
  const [open, setOpen] = useState(false);

  const handleDeleteConta = async (id: string) => {
    if (!id) return;
    try {
      await contaApi.delete(id);
      toast.success("Conta excluída com sucesso");
      setConta((prev: IConta[]) => prev.filter((p) => p.id !== id));
    } catch (error: unknown) {
      if (error instanceof AxiosError) {
        toast.error(
          error?.response?.data?.message || "Erro na exclusão de pessoa"
        );
        setOpen(false);
      }
    }
  };

  return (
    <TableRow hover>
      <TableCell>{length}</TableCell>
      <TableCell>{item.pessoa.nome}</TableCell>
      <TableCell>{MaskCpf(item.pessoa.cpf)}</TableCell>
      <TableCell>{item.numeroConta}</TableCell>
      {!item && <TableCell>Nenhum registro</TableCell>}
      <TableCell>
        <button
          className="text-blue-600 px-4 py-1.5 rounded-md cursor-pointer"
          onClick={() =>
            onEdit({
              id: item.id,
              idPessoa: String(item.pessoa.id),
              numeroConta: item.numeroConta,
            })
          }
        >
          <Edit />
        </button>
        <button
          className="text-red-600 px-4 py-1.5 rounded-md cursor-pointer"
          onClick={() => setOpen(true)}
        >
          <Delete />
        </button>
      </TableCell>
      <Modal
        title="Deseja excluir esta conta?"
        onClose={() => setOpen(false)}
        open={open}
        onDelete={() => handleDeleteConta(item.id || "")}
      />
    </TableRow>
  );
}
