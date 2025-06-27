"use client";

import { IMovimentacao } from "@/shared/schemas/types/IMovimentacao";
import FormatValor from "@/shared/utils/formatValor";
import { TableCell, TableRow } from "@mui/material";

type Props = {
  item: IMovimentacao;
  length: number;
};

export default function MovimentacaoTableItem({ item, length }: Props) {
  return (
    <TableRow hover>
      <TableCell>{length}</TableCell>
      <TableCell>{item.data.replace(",", " -")}</TableCell>
      <TableCell
        className={
          item.tipo === "DEPOSITAR"
            ? "!text-green-600 !font-semibold"
            : "!text-red-600 !font-semibold"
        }
      >
        {item.tipo !== "DEPOSITAR"
          ? `-${FormatValor(Number(item.valor))}`
          : FormatValor(Number(item.valor))}
      </TableCell>
    </TableRow>
  );
}
