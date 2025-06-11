"use client";
import { Box, Dialog, DialogContent, DialogTitle } from "@mui/material";

type Props = {
  open: boolean;
  onClose: () => void;
  onDelete: () => void;
  title: string;
};

export default function Modal({ onClose, onDelete, open, title }: Props) {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        <Box className="flex justify-center gap-5">
          <button
            onClick={onClose}
            className="bg-blue-600 text-white px-6 py-2 rounded-md hover:bg-blue-700 transition cursor-pointer"
          >
            Cancelar
          </button>
          <button
            onClick={onDelete}
            className="bg-red-600 text-white px-6 py-2 rounded-md hover:bg-red-700 transition cursor-pointer"
          >
            Excluir
          </button>
        </Box>
      </DialogContent>
    </Dialog>
  );
}
