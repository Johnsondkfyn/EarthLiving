type StatusBadgeProps = {
  label: string;
};

export function StatusBadge({ label }: StatusBadgeProps) {
  return (
    <span className="inline-flex items-center gap-2 rounded-full border border-earth-accent/25 bg-earth-accent/10 px-4 py-2 text-xs font-bold uppercase tracking-[0.2em] text-earth-accent shadow-green-glow">
      <span className="h-2 w-2 rounded-full bg-earth-accent" />
      {label}
    </span>
  );
}
