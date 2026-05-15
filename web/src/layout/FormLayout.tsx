export const FormLayout = ({ children }: { children: React.ReactNode }) => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-400">
      <div className="bg-slate-50 p-8 rounded-lg shadow-md w-full max-w-lg">
        {children}
      </div>
    </div>
  );
};
