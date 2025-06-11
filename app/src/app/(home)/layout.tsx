"use client";
import { ReactNode } from "react";
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";
import { Container } from "@mui/material";
import Navbar from "@/shared/components/navbar/page";

type Props = {
  children: ReactNode;
};

export default function Layout({ children }: Props) {
  return (
    <Container
      component="main"
      className="min-h-screen flex flex-col items-center py-10"
    >
      <Navbar />
      {children}
    </Container>
  );
}
