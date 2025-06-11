"use cliente";
import { pessoaFormData } from "@/shared/schemas/types/types";
import { TableCell, TableRow } from "@mui/material";
import { Delete, Edit } from "@mui/icons-material";
import MaskCpf from "@/shared/utils/maskCpf";
import { pessoaApi } from "@/shared/api/api";
import { toast } from "react-toastify";
import { AxiosError } from "axios";
import { useState } from "react";
import Modal from "../dialog/modal";

type Props = {
  item: pessoaFormData;
  onEdit: (item: pessoaFormData) => void;
  setPessoa: React.Dispatch<React.SetStateAction<pessoaFormData[]>>;
};

export default function PessoaTableItem({ item, onEdit, setPessoa }: Props) {
  const [open, setOpen] = useState(false);

  const handleDeletePessoa = async (id: string) => {
    if (!id) return;
    try {
      await pessoaApi.delete(id);
      toast.success("Pessoa excluída com sucesso");
      setPessoa((prev: pessoaFormData[]) =>
        prev.filter((p) => String(p.id) !== String(id))
      );
      setOpen(false);
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
      <TableCell>{item.nome}</TableCell>
      <TableCell>{MaskCpf(item.cpf)}</TableCell>
      <TableCell>{item.endereco}</TableCell>

      <TableCell>
        <button
          className="text-blue-600 px-4 py-1.5 rounded-md cursor-pointer"
          onClick={() => onEdit(item || "")}
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
        title="Deseja excluir esta pessoa?"
        onClose={() => setOpen(false)}
        open={open}
        onDelete={() => handleDeletePessoa(String(item.id) || "")}
      />
    </TableRow>
  );
}
