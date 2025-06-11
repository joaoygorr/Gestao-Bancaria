"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";

export default function Navbar() {
  const pathname = usePathname();

  const buttons = [
    { title: "Pessoa", route: "/pessoa" },
    { title: "Conta", route: "/conta" },
    { title: "Movimentação", route: "/movimentacao" },
  ];

  return (
    <nav className="mb-10 flex space-x-4">
      {buttons.map((button, i) => (
        <Link
          key={i}
          href={button.route}
          className={`text-blue-700 px-4 py-2 rounded-md normal-case shadow-none transition 
            ${
              pathname === button.route
                ? "bg-blue-600 text-white"
                : "hover:bg-blue-100"
            }`}
        >
          {button.title}
        </Link>
      ))}
    </nav>
  );
}
