import { useAuth } from "../../context/AuthContext";

export const ContentContainer = () => {
  const { user } = useAuth();

  if (!user) {
    return null;
  }

  return (
    <p className="text-2xl font-bold">
      This is Content Page for {user.username}
    </p>
  );
};
