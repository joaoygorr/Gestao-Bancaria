export default function CaptalizeWord(word: string) {
    if (!word) return "";
    return word
        .split(" ")
        .map((word) =>
            word ? word[0].toLocaleUpperCase() + word.substring(1) : ""
        )
        .join(" ");
}