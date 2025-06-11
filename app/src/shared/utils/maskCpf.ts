export default function MaskCpf(cpf: string) {
    const formattedCpf = cpf.replace(/\D/g, "");

    if (formattedCpf.length <= 3) {
        return formattedCpf;
    } else if (formattedCpf.length <= 6) {
        return formattedCpf.replace(/(\d{3})(\d{1,3})/, "$1.$2");
    } else if (formattedCpf.length <= 9) {
        return formattedCpf.replace(/(\d{3})(\d{3})(\d{1,3})/, "$1.$2.$3");
    } else if (formattedCpf.length <= 11) {
        return formattedCpf.replace(/(\d{3})(\d{3})(\d{3})(\d{1})/, "$1.$2.$3-$4");
    }

    return formattedCpf;
}